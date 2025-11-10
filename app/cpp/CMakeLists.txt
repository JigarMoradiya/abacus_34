cmake_minimum_required(VERSION 3.10...3.25)


project(native-lib)
add_library(
    native-lib
    SHARED
    libnative-lib.cpp
)