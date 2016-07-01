package com.dmi.perfectreader.fragment.book

import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.perfectreader.fragment.book.location.LocationConverter
import com.dmi.perfectreader.fragment.book.bitmap.BitmapDecoder
import com.dmi.util.android.base.BaseViewModel
import com.dmi.util.graphic.SizeF
import com.dmi.util.lang.returnUnit

class Book(
        private val createSized: (SizeF) -> SizedBook,
        private val data: BookData,
        val bitmapDecoder: BitmapDecoder,
        val locationConverter: LocationConverter
) : BaseViewModel() {
    val locationObservable = data.locationObservable

    val renderModel: BookRenderModel get() = sized!!.renderModel

    private var sized: SizedBook? = null

    fun resize(size: SizeF) {
        sized?.destroy()
        sized = createSized(size)
    }

    override fun destroy() {
        super.destroy()
        sized?.destroy()
    }

    fun reformat() = sized?.reformat().returnUnit()

    fun goLocation(location: Location) {
        data.location = location
        sized?.goLocation(location)
    }

    fun goNextPage() {
        sized?.let {
            it.goNextPage()
            data.location = it.location
        }
    }

    fun goPreviousPage() {
        sized?.let {
            it.goPreviousPage()
            data.location = it.location
        }
    }
}