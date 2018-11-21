package com.dmi.perfectreader.ui.library

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns.*
import android.util.SparseArray
import androidx.core.database.getStringOrNull
import androidx.core.util.set
import com.dmi.perfectreader.MainContext
import com.dmi.perfectreader.R
import com.dmi.perfectreader.book.UserBooks
import com.dmi.perfectreader.book.parse.BookParsers
import com.dmi.util.android.view.string
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

private data class ContentEntry(val id: Int, val parentId: Int, val uri: Uri, val size: Long)

private sealed class FolderName {
    abstract fun toString(context: Context): String
}

@Serializable
@Suppress("CanSealedSubClassBeObject")
private class RootFolderName : FolderName() {
    override fun toString(context: Context) = context.string(R.string.libraryFolders)
}

@Serializable
private class FixedFolderName(val name: String) : FolderName() {
    override fun toString(context: Context) = name
}

// todo this class is persist on disk across different app versions. think how to migrate from one version to another
@Serializable
private data class FolderState(val name: FolderName, val deepChildCount: Int, val volume: String, val id: Int)

private class ContentTree {
    var entry: ContentEntry? = null
    val children = ArrayList<ContentTree>()
    var deepChildCount = 0
}

// todo benchmark with many files, and add caching if it is slow or consuming a lot of memory
// todo maybe add internal memory
fun androidFolders(
        context: MainContext,
        androidContext: Context = context.android
) = object : Library.Folders {
    override val root = run {
        val name = androidContext.string(R.string.libraryFolders)
        val deepChildCount = -1
        val volume = "external"
        val id = 0
        Library.Item.Folder(
                name, deepChildCount,
                items = { loadItems(context, volume, id) },
                state = FolderState(RootFolderName(), deepChildCount, volume, id))
    }

    override fun load(state: Any): Library.Item.Folder {
        state as FolderState
        return Library.Item.Folder(
                state.name.toString(androidContext),
                state.deepChildCount,
                items = { loadItems(context, state.volume, state.id) },
                state = state
        )
    }
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
            .removeUnsupportedAndEmpty(parsers)
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
    )?.use {
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

private fun ContentTree.removeUnsupportedAndEmpty(parsers: BookParsers): ContentTree {
    val it = children.iterator()
    while (it.hasNext()) {
        val child = it.next()
        val entry = child.entry
        child.removeUnsupportedAndEmpty(parsers)
        if (entry == null || child.children.size == 0 && (entry.size == 0L || !parsers.isSupported(entry.uri))) {
            it.remove()
        }
    }
    return this
}

private fun ContentTree.calculateDeepCount(): ContentTree {
    val entry = entry
    if (entry != null && children.size == 0) {
        deepChildCount++
    } else {
        for (child in children) {
            child.calculateDeepCount()
            deepChildCount += child.deepChildCount
        }
    }
    return this
}

private suspend fun ContentTree.toItem(context: MainContext, volumeName: String): Library.Item {
    val entry = entry!!
    return if (children.size == 0) {
        loadBookItem(context, entry.uri, entry.size)
    } else {
        val name = entry.uri.lastPathSegment!!
        val stateName = FixedFolderName(entry.uri.lastPathSegment!!)
        suspend fun items() = loadItems(context, volumeName, entry.id)
        Library.Item.Folder(name, deepChildCount, ::items, FolderState(stateName, deepChildCount, volumeName, entry.id))
    }
}

suspend fun loadBookItem(
        context: MainContext,
        uri: Uri,
        size: Long,
        parsers: BookParsers = context.bookParsers,
        userBooks: UserBooks = context.userBooks
): Library.Item.Book {
    val parser = parsers[uri]
    val description = try {
        parser.description()
    } catch (e: Exception) {
        parser.descriptionOnFail()
    }
    val userBook = userBooks.load(uri)
    val readPercent = userBook?.percent
    return Library.Item.Book(uri, size, description, readPercent)
}

private fun <V> SparseArray<V>.getOrPut(key: Int, create: () -> V): V {
    var value = this[key]
    if (value == null) {
        value = create()
        this[key] = value
    }
    return value
}