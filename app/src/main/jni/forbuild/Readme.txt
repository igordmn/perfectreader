Для обновления библиотек:

1. Скачайте их с помощью git:
https://github.com/igordmn/freetype.git
https://github.com/igordmn/harfbuzz.git
https://github.com/igordmn/icu.git

2. Поместите их в папки:
app/src/main/jni/forbuild/repo/freetype
app/src/main/jni/forbuild/repo/harfbuzz
app/src/main/jni/forbuild/repo/icu

3. Выполните команду ndk-build в папке:
app/src/main/jni/forbuild

4. Обновите папки include, скопировав файлы *.h, *.hh, *.hpp:
app/src/main/jni/include/freetype
app/src/main/jni/include/harfbuzz
app/src/main/jni/include/icu/common

из папок:
app/src/main/jni/forbuild/repo/freetype/include
app/src/main/jni/forbuild/repo/harfbuzz/src
app/src/main/jni/forbuild/repo/icu/source/common

5. Скопируйте все из папки:
app/src/main/jni/forbuild/libs

в папку:
app/src/main/jni/libs

6. Удалите папки:
app/src/main/jni/forbuild/libs
app/src/main/jni/forbuild/obj
app/src/main/jni/forbuild/repo
