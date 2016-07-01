package com.dmi.util.android.system

enum class ThreadPriority {
    // android.os.Process\-4, java.lang.Thread\7
    DISPLAY,

    // android.os.Process\0, java.lang.Thread\5
    NORMAL,

    // android.os.Process\10, java.lang.Thread\4
    BACKGROUND
}

fun Thread.setPriority(priority: ThreadPriority) {
    setPriority(when (priority) {
        ThreadPriority.DISPLAY -> 7
        ThreadPriority.NORMAL -> 5
        ThreadPriority.BACKGROUND -> 4
    })
}