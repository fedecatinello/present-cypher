cmake_minimum_required(VERSION 3.6)
project(present_cypher)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES present.c present.h)
add_executable(present_cypher ${SOURCE_FILES})