package com.dmi.perfectreader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.dmi.perfectreader.library.LibraryActivity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, LibraryActivity::class.java))
        finish()
    }
}