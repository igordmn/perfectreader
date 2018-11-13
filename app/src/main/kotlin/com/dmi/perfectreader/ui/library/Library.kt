package com.dmi.perfectreader.ui.library

import android.net.Uri
import com.dmi.perfectreader.MainContext
import com.dmi.perfectreader.book.UserBooks
import com.dmi.perfectreader.book.content.BookDescription
import com.dmi.util.android.view.Id
import com.dmi.util.lang.unsupported
import com.dmi.util.scope.ObservableList
import com.dmi.util.scope.Scope
import com.dmi.util.scope.observable
import com.dmi.util.scope.observableProperty
import com.dmi.util.screen.Screen
import kotlinx.serialization.Serializable

class Library(
        val context: MainContext,
        val close: () -> Unit,
        val openBook: (uri: Uri) -> Unit,
        val state: LibraryState,
        private val root: Item.Folder = androidRoot(context),
        private val userBooks: UserBooks = context.userBooks,
        private val scope: Scope = Scope()
) : Screen by Screen(scope) {
    var popup: Id? by observableProperty(state::popup)

    val recentBooks: List<Item.Book>? by scope.async {
        userBooks.lastBooks(count = 5).map {
            loadBookItem(context, it.uri, 0)
        }
    }

    val folders = ObservableList<Item.Folder>().apply {
        add(root)
    }

    var currentIndex: Int by observable(0)
    var sort: Sort by observable(Sort(Sort.Field.Name, Sort.Method.ASC))

    val items: List<Item>? by scope.async {
        currentFolder.items().let(sort::apply)
    }
    private val currentFolder: Item.Folder get() = folders[currentIndex]

    fun open(item: Item) {
        when (item) {
            is Item.Folder -> {
                repeat(folders.size - 1 - currentIndex) {
                    folders.remove()
                }

                folders.add(item)
                currentIndex = folders.size - 1
            }
            is Item.Book -> openBook(item.uri)
        }
    }

    fun refresh() {
        currentIndex = currentIndex
    }

    fun back() {
        if (currentIndex > 0) {
            currentIndex--
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
                val readPercent: Double?
        ) : Item()
    }

    class Sort(val field: Field, val method: Method) {
        fun apply(list: List<Item>): List<Item> {
            val folders = ArrayList<Item.Folder>()
            val books = ArrayList<Item.Book>()

            for (item in list) {
                when (item) {
                    is Item.Folder -> folders.add(item)
                    is Item.Book -> books.add(item)
                }
            }

            return folders(folders) + field.books(method, books)
        }

        private fun folders(list: List<Item.Folder>) = method.apply(list) { it.name }

        sealed class Field {
            abstract fun books(method: Method, list: List<Item.Book>): List<Item.Book>

            object Name : Field() {
                override fun books(method: Method, list: List<Item.Book>) = method.apply(list) {
                    it.description.name ?: it.description.fileName
                }
            }

            object Author : Field() {
                override fun books(method: Method, list: List<Item.Book>) = method.apply(list) {
                    SortItem(it.description.author, it.description.name ?: it.description.fileName)
                }

                private class SortItem(val author: String?, val name: String) : Comparable<SortItem> {
                    override fun compareTo(other: SortItem): Int = when {
                        author != null && other.author == null -> -1
                        author == null && other.author != null -> 1
                        author != null && other.author != null -> author.compareTo(other.author)
                        author == null && other.author == null -> name.compareTo(other.name)
                        else -> unsupported()
                    }
                }
            }

            object Size : Field() {
                override fun books(method: Method, list: List<Item.Book>) = method.apply(list) { it.fileSize }
            }

            object ReadPercent : Field() {
                override fun books(method: Method, list: List<Item.Book>) = method.apply(list) { it.readPercent }
            }
        }

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
class LibraryState(var popup: Id? = null)