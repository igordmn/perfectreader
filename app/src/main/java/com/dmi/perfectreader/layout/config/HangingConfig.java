package com.dmi.perfectreader.layout.config;

public class HangingConfig {
    public float leftHangFactor(char ch) {
        switch (ch) {
            // скобки
            case '(':
                return 0.5F;
            case '[':
                return 0.5F;
            case '{':
                return 0.5F;
            case '<':
                return 0.5F;

            // кавычки
            case '"':
                return 0.5F;
            case '\'':
                return 0.5F;
            case '«':
                return 0.5F;
            case '»':
                return 0.5F;
            case '„':
                return 0.5F;
            case '“':
                return 0.5F;
            case '‘':
                return 0.5F;
            case '‚':
                return 0.5F;
            case '‹':
                return 0.5F;

            default:
                return 0;
        }
    }

    public float rightHangFactor(char ch) {
        switch (ch) {
            // скобки
            case ')':
                return 0.5F;
            case ']':
                return 0.5F;
            case '}':
                return 0.5F;
            case '>':
                return 0.5F;

            // кавычки
            case '"':
                return 0.5F;
            case '\'':
                return 0.5F;
            case '»':
                return 0.5F;
            case '«':
                return 0.5F;
            case '“':
                return 0.5F;
            case '”':
                return 0.5F;
            case '’':
                return 0.5F;
            case '›':
                return 0.5F;

            // знаки препинания
            case ',':
                return 0.5F;
            case '.':
                return 0.5F;
            case '…':
                return 0.25F;
            case ':':
                return 0.50F;
            case ';':
                return 0.50F;
            case '!':
                return 0.50F;
            case '‼':
                return 0.50F;
            case '?':
                return 0.50F;
            case '،':
                return 0.50F;
            case '۔':
                return 0.50F;
            case '、':
                return 0.50F;
            case '。':
                return 0.50F;
            case '，':
                return 0.50F;
            case '．':
                return 0.50F;
            case '﹐':
                return 0.50F;
            case '﹑':
                return 0.50F;
            case '﹒':
                return 0.50F;
            case '｡':
                return 0.50F;
            case '､':
                return 0.50F;

            // тире, дефисы, мягкий перенос
            case '\u2010':
                return 0.50F;
            case '\u2011':
                return 0.50F;
            case '\u2012':
                return 0.50F;
            case '\u2013':
                return 0.50F;
            case '\u2014':
                return 0.50F;
            case '\u00AD':
                return 0.50F;
            case '\u002D':
                return 0.50F;

            default:
                return 0;
        }
    }
}
