cmake_minimum_required(VERSION 3.24)
project(Player LANGUAGES CXX)

find_package(PkgConfig REQUIRED)
pkg_check_modules(GSTREAMER REQUIRED IMPORTED_TARGET
        gstreamer-1.0>=1.20 gstreamer-app-1.0 gstreamer-video-1.0)

add_library(player SHARED player.cpp)
target_compile_features(player PRIVATE cxx_std_17)
target_link_libraries(player PRIVATE PkgConfig::GSTREAMER)
target_compile_options(player PRIVATE -Wall -Wextra -Wpedantic)
set_target_properties(player PROPERTIES CXX_VISIBILITY_PRESET hidden)
install(TARGETS player LIBRARY DESTINATION .)