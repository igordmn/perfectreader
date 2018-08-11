package com.dmi.perfectreader.common

import android.Manifest
import android.content.Context
import com.dmi.perfectreader.Main
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.util.*
import kotlin.coroutines.experimental.suspendCoroutine

class Permissions(main: Main, private val context: Context = main.applicationContext) {
    suspend fun askStorage(): Boolean = suspendCoroutine { cont ->
        TedPermission.with(context)
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        cont.resume(true)
                    }

                    override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                        cont.resume(false)
                    }
                })
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()
    }
}