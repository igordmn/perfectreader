package com.dmi.util.log

object ConsoleLog : Log {
    override fun v(message: String) = println("V", null, message)
    override fun v(t: Throwable, message: String) = println("V", t, message)
    override fun d(message: String) = println("D", null, message)
    override fun d(t: Throwable, message: String) = println("D", t, message)
    override fun i(message: String) = println("I", null, message)
    override fun i(t: Throwable, message: String) = println("I", t, message)
    override fun w(message: String) = println("VW", null, message)
    override fun w(t: Throwable, message: String) = println("W", t, message)
    override fun e(message: String) = println("E", null, message)
    override fun e(t: Throwable, message: String) = println("E", t, message)
    override fun wtf(message: String) = println("WTF", null, message)
    override fun wtf(t: Throwable, message: String) = println("WTF", t, message)
}

private fun println(level: String, t: Throwable?, message: String) = println(formatMessage(level, t, message))

private fun formatMessage(level: String, t: Throwable?, message: String): String {
    val tag = logTag()
    val fullMessage = logFullMessage(t, message)
    return "$level/$tag: $fullMessage"
}