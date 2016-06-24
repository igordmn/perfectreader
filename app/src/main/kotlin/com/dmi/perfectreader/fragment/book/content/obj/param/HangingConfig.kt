package com.dmi.perfectreader.fragment.book.content.obj.param

interface HangingConfig {
    fun leftHangFactor(ch: Char): Float
    fun rightHangFactor(ch: Char): Float
}

object NoneHangingConfig : HangingConfig {
    override fun leftHangFactor(ch: Char) = 0F
    override fun rightHangFactor(ch: Char) = 0F
}

object DefaultHangingConfig : HangingConfig {
    override fun leftHangFactor(ch: Char): Float {
        when (ch) {
        // скобки
            '(' -> return 0.5F
            '[' -> return 0.5F
            '{' -> return 0.5F
            '<' -> return 0.5F

        // кавычки
            '"' -> return 0.5F
            '\'' -> return 0.5F
            '«' -> return 0.5F
            '»' -> return 0.5F
            '„' -> return 0.5F
            '“' -> return 0.5F
            '‘' -> return 0.5F
            '‚' -> return 0.5F
            '‹' -> return 0.5F

            else -> return 0F
        }
    }

    override fun rightHangFactor(ch: Char): Float {
        when (ch) {
        // скобки
            ')' -> return 0.5F
            ']' -> return 0.5F
            '}' -> return 0.5F
            '>' -> return 0.5F

        // кавычки
            '"' -> return 0.5F
            '\'' -> return 0.5F
            '»' -> return 0.5F
            '«' -> return 0.5F
            '“' -> return 0.5F
            '”' -> return 0.5F
            '’' -> return 0.5F
            '›' -> return 0.5F

        // знаки препинания
            ',' -> return 0.5F
            '.' -> return 0.5F
            '…' -> return 0.25F
            ':' -> return 0.50F
            ';' -> return 0.50F
            '!' -> return 0.50F
            '‼' -> return 0.50F
            '?' -> return 0.50F
            '،' -> return 0.50F
            '\u06D4' -> return 0.50F
            '、' -> return 0.50F
            '。' -> return 0.50F
            '，' -> return 0.50F
            '．' -> return 0.50F
            '﹐' -> return 0.50F
            '﹑' -> return 0.50F
            '﹒' -> return 0.50F
            '｡' -> return 0.50F
            '､' -> return 0.50F

        // тире, дефисы, мягкий перенос
            '\u2010' -> return 0.50F
            '\u2011' -> return 0.50F
            '\u2012' -> return 0.50F
            '\u2013' -> return 0.50F
            '\u2014' -> return 0.50F
            '\u00AD' -> return 0.50F
            '\u002D' -> return 0.50F

            else -> return 0F
        }
    }
}