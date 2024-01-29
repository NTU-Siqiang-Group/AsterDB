cmake_minimum_required(VERSION 3.12 FATAL_ERROR)

project(GraphDB)
include(GNUInstallDirs)
include_directories(${PROJECT_SOURCE_DIR}/src/include)
include_directories("/usr/local/include/duck_pg_query")

find_package(Drogon CONFIG REQUIRED)
set(CMAKE_CXX_FLAGS  "${CMAKE_CXX_FLAGS} -w")
set(CMAKE_CXX_STANDARD 17)
add_library(
  graphdbshared 
  src/server/http_server.cc
  src/sql/parser.cc
)

add_executable(
  graphdb 
  src/bin/httpserver.cc
)
add_executable(
  testpg
  src/bin/test_pg.cc
)

target_link_libraries(graphdbshared PRIVATE Drogon::Drogon duckdb_pg_query)
target_link_libraries(graphdb PRIVATE graphdbshared)
target_link_libraries(testpg PRIVATE duckdb_pg_query pthread)