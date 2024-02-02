#include "duck_pg_query/postgres_parser.hpp"
#include "duck_pg_query/nodes/parsenodes.hpp"
#include <stdio.h>
#include <iostream>

int main() {
  auto parser = duckdb::PostgresParser();
  parser.Parse("DELETE FROM edge WHERE src = 1;");
  std::cout << parser.error_message << std::endl;
  auto tree = parser.parse_tree;
  int idx = 0;
  for (auto entry = tree->head; entry != nullptr; entry = entry->next) {
    std::cout << entry << std::endl;
    auto node = reinterpret_cast<duckdb_libpgquery::PGNode *>(entry->data.ptr_value);
    auto stmt = reinterpret_cast<duckdb_libpgquery::PGRawStmt *>(node)->stmt;
    std::cout << stmt->type << std::endl;
    std::cout << reinterpret_cast<duckdb_libpgquery::PGDeleteStmt *>(stmt)->whereClause->type << std::endl;
    auto delete_stmt = reinterpret_cast<duckdb_libpgquery::PGDeleteStmt *>(stmt);
    auto where_clause = reinterpret_cast<duckdb_libpgquery::PGAExpr *>(delete_stmt->whereClause);
    std::cout << std::string((reinterpret_cast<duckdb_libpgquery::PGValue *>(where_clause->name->head->data.ptr_value))->val.str) << std::endl;
    idx ++;
  }
  std::cout << "num: " << idx << std::endl;
  return 0;
}