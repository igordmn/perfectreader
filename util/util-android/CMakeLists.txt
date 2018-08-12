cmake_minimum_required(VERSION 3.6.0)

add_library(
             utilAndroid
             SHARED
             src/main/cpp/util/Debug.cpp
             src/main/cpp/util/JniUtils.cpp
             src/main/cpp/CorrectGamma.cpp
             src/main/cpp/EntryPoint.cpp
             src/main/cpp/OpenGl.cpp
             )

target_link_libraries(
                       utilAndroid
                       android
                       jnigraphics
                       log
                       GLESv2
                       )