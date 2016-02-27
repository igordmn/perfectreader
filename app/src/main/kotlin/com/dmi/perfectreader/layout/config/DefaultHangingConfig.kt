package com.dmi.perfectreader.layout.config

class DefaultHangingConfig : HangingConfig {
    override fun leftHangFactor(ch: Char): Float {
        when (ch) {
        // скобки
            '(' -> return 0.5f
            '[' -> return 0.5f
            '{' -> return 0.5f
            '<' -> return 0.5f

        // кавычки
            '"' -> return 0.5f
            '\'' -> return 0.5f
            '«' -> return 0.5f
            '»' -> return 0.5f
            '„' -> return 0.5f
            '“' -> return 0.5f
            '‘' -> return 0.5f
            '‚' -> return 0.5f
            '‹' -> return 0.5f

            else -> return 0f
        }
    }

    override fun rightHangFactor(ch: Char): Float {
        when (ch) {
        // скобки
            ')' -> return 0.5f
            ']' -> return 0.5f
            '}' -> return 0.5f
            '>' -> return 0.5f

        // кавычки
            '"' -> return 0.5f
            '\'' -> return 0.5f
            '»' -> return 0.5f
            '«' -> return 0.5f
            '“' -> return 0.5f
            '”' -> return 0.5f
            '’' -> return 0.5f
            '›' -> return 0.5f

        // знаки препинания
            ',' -> return 0.5f
            '.' -> return 0.5f
            '…' -> return 0.25f
            ':' -> return 0.50f
            ';' -> return 0.50f
            '!' -> return 0.50f
            '‼' -> return 0.50f
            '?' -> return 0.50f
            '،' -> return 0.50f
            '۔' -> return 0.50f
            '、' -> return 0.50f
            '。' -> return 0.50f
            '，' -> return 0.50f
            '．' -> return 0.50f
            '﹐' -> return 0.50f
            '﹑' -> return 0.50f
            '﹒' -> return 0.50f
            '｡' -> return 0.50f
            '､' -> return 0.50f

        // тире, дефисы, мягкий перенос
            '\u2010' -> return 0.50f
            '\u2011' -> return 0.50f
            '\u2012' -> return 0.50f
            '\u2013' -> return 0.50f
            '\u2014' -> return 0.50f
            '\u00AD' -> return 0.50f
            '\u002D' -> return 0.50f

            else -> return 0f
        }
    }
}
