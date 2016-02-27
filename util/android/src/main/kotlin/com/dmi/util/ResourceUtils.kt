package com.dmi.util

import android.annotation.SuppressLint
import android.content.res.Resources
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object ResourceUtils {
    @SuppressLint("NewApi")
    fun readTextRawResource(resources: Resources,
                            resourceId: Int): String {
        try {
            resources.openRawResource(resourceId).use { inputStream ->
                InputStreamReader(inputStream).use { inputStreamReader ->
                    BufferedReader(
                            inputStreamReader).use { bufferedReader ->
                        var nextLine: String
                        val body = StringBuilder()

                        nextLine = bufferedReader.readLine()
                        while (nextLine != null) {
                            body.append(nextLine)
                            body.append('\n')
                            nextLine = bufferedReader.readLine()
                        }

                        return body.toString()
                    }
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }
}
