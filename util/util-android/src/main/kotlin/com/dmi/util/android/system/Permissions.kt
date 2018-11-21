package com.dmi.util.android.system

import android.content.Context
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Permissions(private val context: Context) {
    suspend fun ask(permissions: Array<String>): Boolean = suspendCoroutine { cont ->
        TedPermission.with(context)
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        cont.resume(true)
                    }

                    override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                        cont.resume(false)
                    }
                })
                .setPermissions(*permissions)
                .check()
    }
}