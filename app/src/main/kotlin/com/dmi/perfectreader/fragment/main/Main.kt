package com.dmi.perfectreader.fragment.main

import android.content.Intent
import android.net.Uri
import com.dmi.perfectreader.app.bookLoadScheduler
import com.dmi.perfectreader.data.UserData
import com.dmi.perfectreader.fragment.book.BookData
import com.dmi.perfectreader.fragment.book.parse.BookContentParserFactory
import com.dmi.perfectreader.fragment.reader.Reader
import com.dmi.util.android.base.BaseViewModel
import com.dmi.util.ext.LambdaObservable
import com.dmi.util.log
import com.dmi.util.mainScheduler
import com.dmi.util.rx.rxObservable
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.Result.Failure
import com.github.kittinunf.result.Result.Success
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.map
import com.github.kittinunf.result.success
import rx.lang.kotlin.BehaviorSubject
import java.io.IOException

class Main(
        private val intent: Intent,
        private val bookContentParserFactory: BookContentParserFactory,
        private val userData: UserData,
        private val createReader: (BookData) -> Reader,
        val close: () -> Unit
) : BaseViewModel() {
    val isLoadingObservable = BehaviorSubject<Boolean>()
    val loadErrorObservable = BehaviorSubject<LoadError?>()
    val readerObservable = BehaviorSubject<Reader?>()

    private var isLoading: Boolean by rxObservable(true, isLoadingObservable)
    private var loadError: LoadError? by rxObservable(null, loadErrorObservable)
    private var reader: Reader? by rxObservable(null, readerObservable)

    init {
        subscribe(
                LambdaObservable { openReader() }
                        .subscribeOn(bookLoadScheduler)
                        .observeOn(mainScheduler)
        ) { result ->
            isLoading = false
            result.success {
                reader = initChild(it)
            }
            result.failure {
                loadError = it
            }
        }
    }

    private fun openReader(): Result<Reader, LoadError> = try {
        loadData(requestedBookURI(intent)).map(createReader)
    } catch (e: IOException) {
        log.e(e, "Book load error")
        Failure(LoadError.IO())
    }

    private fun loadData(requestedURI: Uri?) = bookFile(requestedURI).map { loadData(it) }

    private fun bookFile(requestedURI: Uri?): Result<Uri, LoadError> =
            if (requestedURI != null) {
                userData.saveLastBookFile(requestedURI)
                Success(requestedURI)
            } else {
                val lastBookURI = userData.loadLastBookURI()
                if (lastBookURI != null) {
                    Success(lastBookURI)
                } else {
                    Failure(LoadError.NeedOpenThroughFileManager())
                }
            }

    private fun loadData(uri: Uri) = BookData(userData, uri, loadContent(uri))
    private fun loadContent(uri: Uri) = bookContentParserFactory.parserFor(uri).parse()

    private fun requestedBookURI(intent: Intent): Uri? = intent.data

    sealed class LoadError : Exception() {
        class IO() : LoadError()
        class NeedOpenThroughFileManager : LoadError()
    }
}