package com.dmi.perfectreader.library

import android.net.Uri
import com.dmi.perfectreader.MainContext
import com.dmi.perfectreader.book.content.BookDescription
import com.dmi.util.scope.ObservableStack
import com.dmi.util.scope.Scope
import com.dmi.util.scope.observable
import com.dmi.util.screen.Screen
import kotlinx.serialization.Serializable

class Library(
        val context: MainContext,
        val close: () -> Unit,
        val openBook: (uri: Uri) -> Unit,
        val state: LibraryState,
        private val root: Item.Folder = androidRoot(context),
        private val scope: Scope = Scope()
) : Screen by Screen(scope) {
    val folders = ObservableStack<Item.Folder>().apply {
        push(root)
    }

    var currentIndex: Int by observable(0)
    var sort: Sort by observable(Sort.Name(Sort.Method.ASC))

    // todo remove catch, fix throwing exceptions in scope.async
    val items: List<Item>? by scope.async {
        currentFolder.items().let(sort::apply)
    }
    private val currentFolder: Item.Folder get() = folders[currentIndex]

    fun open(item: Item) {
        when (item) {
            is Item.Folder -> {
                repeat(currentIndex) {
                    folders.pop()
                }

                folders.push(item)
                currentIndex = 0
            }
            is Item.Book -> openBook(item.uri)
        }
    }

    fun back() {
        if (currentIndex < folders.size - 1) {
            currentIndex++
        } else {
            close()
        }
    }

    sealed class Item {
        class Folder(
                val name: String,
                val deepBookCount: Int,
                val items: suspend () -> List<Item>
        ) : Item()

        class Book(
                val uri: Uri,
                val fileSize: Long,
                val description: BookDescription,
                val readPercent: Double = 0.0
        ) : Item()
    }

    sealed class Sort(private val method: Method) {
        class Name(private val method: Method) : Sort(method) {
            override fun books(list: List<Item.Book>) = method.apply(list) { it.description.name ?: it.description.fileName }
        }

        class Author(private val method: Method) : Sort(method) {
            override fun books(list: List<Item.Book>) = method.apply(list) { it.description.author ?: it.description.fileName }
        }

        class Size(private val method: Method) : Sort(method) {
            override fun books(list: List<Item.Book>) = method.apply(list) { it.fileSize }
        }

        fun apply(list: List<Item>): List<Item> {
            val folders = ArrayList<Item.Folder>()
            val books = ArrayList<Item.Book>()

            for (item in list) {
                when (item) {
                    is Item.Folder -> folders.add(item)
                    is Item.Book -> books.add(item)
                }
            }

            return folders(folders) + books(books)
        }

        private fun folders(list: List<Item.Folder>) = method.apply(list) { it.name }
        protected abstract fun books(list: List<Item.Book>): List<Item.Book>

        enum class Method {
            ASC {
                override fun <T, R : Comparable<R>> apply(list: List<T>, selector: (T) -> R?) = list.sortedBy(selector)
            },

            DESC {
                override fun <T, R : Comparable<R>> apply(list: List<T>, selector: (T) -> R?) = list.sortedByDescending(selector)
            };

            abstract fun <T, R : Comparable<R>> apply(list: List<T>, selector: (T) -> R?): List<T>
        }
    }
}

@Serializable
data class LibraryState(val x: Int)