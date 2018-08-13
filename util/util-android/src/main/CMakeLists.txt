cmake_minimum_required(VERSION 3.6.0)

add_library(
        utilAndroid SHARED
        cpp/util/Debug.cpp
        cpp/util/JniUtils.cpp
        cpp/CorrectGamma.cpp
        cpp/EntryPoint.cpp
        cpp/OpenGL.cpp
        cpp/Resizes.cpp
)

add_library(
        cair SHARED
        cppLibs/cair/CAIR.cpp
)

target_include_directories(
        utilAndroid PRIVATE
        cppLibs/cair
)

target_link_libraries(
        utilAndroid
        cair
        android
        jnigraphics
        log
        GLESv2
)