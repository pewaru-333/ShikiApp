cmake_minimum_required(VERSION 3.24)
project(Player LANGUAGES CXX)

include(FetchContent)
FetchContent_Declare(libass_headers
    GIT_REPOSITORY https://github.com/libass/libass.git
    GIT_TAG 0.17.4
    GIT_SHALLOW TRUE
    SOURCE_SUBDIR headers_only)
FetchContent_MakeAvailable(libass_headers)

add_library(player SHARED player.cpp)
target_include_directories(player PRIVATE "${libass_headers_SOURCE_DIR}/libass")
target_compile_features(player PRIVATE cxx_std_20)
target_compile_definitions(player PRIVATE
    UNICODE _UNICODE NOMINMAX WIN32_LEAN_AND_MEAN WINVER=0x0A00 _WIN32_WINNT=0x0A00)
target_link_libraries(player PRIVATE windowsapp d3d11 dxgi)
target_compile_options(player PRIVATE /EHsc /utf-8 /W4 /permissive-)
set_property(TARGET player PROPERTY
    MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")