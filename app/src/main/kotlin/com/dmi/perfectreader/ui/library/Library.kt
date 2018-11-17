package com.dmi.perfectreader.ui.library

import android.net.Uri
import com.dmi.perfectreader.MainContext
import com.dmi.perfectreader.book.UserBooks
import com.dmi.perfectreader.book.content.BookDescription
import com.dmi.util.android.view.Id
import com.dmi.util.collection.removeLast
import com.dmi.util.lang.unsupported
import com.dmi.util.lang.value
import com.dmi.util.scope.ObservableList
import com.dmi.util.scope.Scope
import com.dmi.util.scope.observable
import com.dmi.util.scope.observableProperty
import com.dmi.util.screen.Screen
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

class Library(
        val context: MainContext,
        val close: () -> Unit,
        val openBook: (uri: Uri) -> Unit,
        loadState: (Folders) -> LibraryState,
        private val folders: Folders = androidFolders(context),
        val state: LibraryState = loadState(folders),
        private val userBooks: UserBooks = context.userBooks,
        private val scope: Scope = Scope()
) : Screen by Screen(scope) {
    var popup: Id? by observableProperty(state::popup)

    private var refreshNotifier by observable(Unit)

    val recentBooks: List<Item.Book>? by scope.async {
        refreshNotifier
        userBooks.recentBooks(count = 8).map {
            loadBookItem(context, it.uri, 0)
        }
    }

    val locations = ObservableList<Location>().apply {
        for (item in state.locations.list) {
            add(Location(item))
        }
        afterAdd.subscribe {
            state.locations.list.add(top!!.state)
        }
        afterRemove.subscribe {
            state.locations.list.removeLast()
        }
    }
    var currentIndex: Int by observableProperty(state.locations::currentIndex)
    val currentLocation: Location get() = locations[currentIndex]

    var sort: Sort by observable(Sort(Sort.Field.Name, Sort.Method.ASC))
    val items: List<Item>? by scope.async {
        refreshNotifier
        currentLocation.folder.items().let(sort::apply)
    }

    fun hasRecentBooks() = runBlocking { userBooks.hasRecentBooks() }

    fun open(item: Item) {
        when (item) {
            is Item.Folder -> {
                repeat(locations.size - 1 - currentIndex) {
                    locations.remove()
                }

                locations.add(Location(LibraryLocationState(item.state)))
                currentIndex = locations.size - 1
            }
            is Item.Book -> openBook(item.uri)
        }
    }

    fun refresh() {
        refreshNotifier = Unit
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
                val items: suspend () -> List<Item>,
                val state: Any
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

    interface Folders {
        val root: Item.Folder
        fun load(state: Any): Item.Folder
    }

    fun Location(state: LibraryLocationState) = Location(folders.load(state.folderState), state)

    inner class Location(val folder: Item.Folder, val state: LibraryLocationState) {
        var scrollPosition by value(state::scrollPosition)
    }
}

@Serializable
class LibraryState(var popup: Id? = null, val locations: LibraryLocationsState)

fun LibraryLocationsState(folders: Library.Folders) = LibraryLocationsState(arrayListOf(LibraryLocationState(folders.root.state)), currentIndex = 0)

@Serializable
class LibraryLocationsState(val list: ArrayList<LibraryLocationState>, var currentIndex: Int)

@Serializable
class LibraryLocationState(val folderState: Any, var scrollPosition: LibraryScrollPosition = LibraryScrollPosition(0, 0))

@Serializable
class LibraryScrollPosition(val index: Int, val offset: Int)