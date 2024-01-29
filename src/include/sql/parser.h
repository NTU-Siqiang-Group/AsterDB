#pragma once

/*
The intial version of SQL parser supports:
1. Add vertex
  INSERT INTO vertex (id) VALUES (xxx);
2. Add edge
  INSERT INTO edge(from, to) VALUES (xxx, xxx);
3. Delete vertex
  DELETE FROM vertex WHERE id = xxx;
4. Delete edge
  DELETE FROM edge WHERE from = xxx AND to = xxx;
5. Get all OUT neighbors of a vertex
  SELECT to FROM edge WHERE from = xxx;
6. Get all IN neighbors of a vertex
  SELECT from FROM edge WHERE to = xxx;
*/
#include "duck_pg_query/postgres_parser.hpp"
#include "duck_pg_query/nodes/parsenodes.hpp"

#include <string>

namespace GraphDB {
class GraphSQLParser {
 private:
  duckdb_libpgquery::PGList* Parse(std::string& sql) {
    auto parser = duckdb::PostgresParser();
    parser.Parse(sql);
    return parser.parse_tree;
  }
};
} // namespace GraphDB