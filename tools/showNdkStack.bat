@echo off

if [%1]==[] goto showFromAdb

ndk-stack -sym ../app/build/intermediates/ndk/debug/obj/local/armeabi-v7a -dump %1
goto :eof

:showFromAdb
adb logcat |  ndk-stack -sym ../app/build/intermediates/ndk/debug/obj/local/armeabi-v7a