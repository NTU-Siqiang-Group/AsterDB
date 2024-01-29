#include "duck_pg_query/postgres_parser.hpp"
#include "duck_pg_query/nodes/parsenodes.hpp"
#include <stdio.h>
#include <iostream>

int main() {
  auto parser = duckdb::PostgresParser();
  parser.Parse("INSERT INTO vertex (id) VALUES (1);");
  std::cout << parser.error_message << std::endl;
  auto tree = parser.parse_tree;
  for (auto entry = tree->head; entry != nullptr; entry = entry->next) {
    std::cout << entry << std::endl;
    auto node = reinterpret_cast<duckdb_libpgquery::PGNode *>(entry->data.ptr_value);
    std::cout << reinterpret_cast<duckdb_libpgquery::PGRawStmt *>(node)->stmt->type << std::endl;
  }
  return 0;
}