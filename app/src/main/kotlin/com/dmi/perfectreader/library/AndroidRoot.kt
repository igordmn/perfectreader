package com.dmi.perfectreader.library

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns.*
import android.util.SparseArray
import androidx.core.database.getStringOrNull
import androidx.core.util.set
import com.dmi.perfectreader.MainContext
import com.dmi.perfectreader.R
import com.dmi.perfectreader.book.parse.BookParsers
import com.dmi.util.android.view.string
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private data class ContentEntry(val id: Int, val parentId: Int, val uri: Uri, val size: Long)

private class ContentTree {
    var entry: ContentEntry? = null
    val children = ArrayList<ContentTree>()
    var deepChildCount = 0
}

// todo benchmark with many files, and add caching if it is slow or consuming a lot of memory
// todo add external sd card
fun androidRoot(
        context: MainContext,
        androidContext: Context = context.android
) = Library.Item.Folder(androidContext.string(R.string.libraryFolders), -1) {
    //    loadItems(main, "internal", id = 0) + loadItems(main, "external", id = 0)
    loadItems(context, "external", id = 0)
}

private suspend fun loadItems(
        context: MainContext,
        volumeName: String,
        id: Int,
        androidContext: Context = context.android,
        parsers: BookParsers = context.bookParsers
): List<Library.Item> = withContext(Dispatchers.IO) {
    entries(androidContext, volumeName)
            .toTree(id)
            .removeUnsupported(parsers).removeEmpty()
            .calculateDeepCount()
            .children
            .map {
                it.toItem(context, volumeName)
            }
}

private fun Sequence<ContentEntry>.toTree(id: Int): ContentTree {
    val idToTree = SparseArray<ContentTree>()

    forEach {
        val parent = idToTree.getOrPut(it.parentId, ::ContentTree)
        val self = idToTree.getOrPut(it.id, ::ContentTree)
        self.entry = it
        parent.children.add(self)
    }

    return idToTree[id] ?: ContentTree()
}

private fun entries(context: Context, volumeName: String) = sequence {
    context.contentResolver.query(
            MediaStore.Files.getContentUri(volumeName),
            arrayOf(_ID, PARENT, DATA, SIZE),
            null, null, null
    ).use {
        it!!

        while (it.moveToNext()) {
            val data = it.getStringOrNull(2)
            if (data != null) {
                val uri = Uri.parse("file://$data")
                yield(ContentEntry(
                        it.getInt(0),
                        it.getInt(1),
                        uri,
                        it.getLong(3)
                ))
            }
        }
    }
}

private fun ContentTree.removeUnsupported(parsers: BookParsers): ContentTree {
    val it = children.iterator()
    while (it.hasNext()) {
        val child = it.next()
        val entry = child.entry
        if (entry != null && entry.size > 0 && !parsers.isSupported(entry.uri)) {
            it.remove()
        }
        child.removeUnsupported(parsers)
    }
    return this
}

private fun ContentTree.removeEmpty(): ContentTree {
    val it = children.iterator()
    while (it.hasNext()) {
        val child = it.next()
        val entry = child.entry
        child.removeEmpty()
        if (entry == null || entry.size == 0L && child.children.size == 0) {
            it.remove()
        }
    }
    return this
}

private fun ContentTree.calculateDeepCount(): ContentTree {
    val entry = entry
    if (entry != null && entry.size > 0) {
        deepChildCount++
    } else {
        for (child in children) {
            child.calculateDeepCount()
            deepChildCount += child.deepChildCount
        }
    }
    return this
}

private suspend fun ContentTree.toItem(
        context: MainContext,
        volumeName: String,
        parsers: BookParsers = context.bookParsers
): Library.Item {
    val entry = entry!!
    return if (entry.size > 0) {
        val parser = parsers[entry.uri]
        val description = try {
            parser.description()
        } catch (e: Exception) {
            parser.descriptionOnFail()
        }
        Library.Item.Book(entry.uri, entry.size, description)
    } else {
        val name = entry.uri.lastPathSegment!!
        suspend fun items() = loadItems(context, volumeName, entry.id)
        Library.Item.Folder(name, deepChildCount, ::items)
    }
}

private fun <V> SparseArray<V>.getOrPut(key: Int, create: () -> V): V {
    var value = this[key]
    if (value == null) {
        value = create()
        this[key] = value
    }
    return value
}