#pragma once

#include <string>

#include "duck_pg_query/postgres_parser.hpp"
#include "duck_pg_query/nodes/parsenodes.hpp"
#include "sql/core.h"
#include "sql/planner.h"


namespace GraphDB {
// interface of GraphDB
class GraphDB {
 public:
  virtual void ExecuteQuery(const std::string& query, std::string& result) = 0;
};

class GraphDBImpl : public GraphDB {
 public:
  virtual void ExecuteQuery(const std::string& query, std::string& result) override {
    auto parser = duckdb::PostgresParser();
    parser.Parse(query);
    
    auto tree = parser.parse_tree;
    GraphSQLPlanner planner;
    auto statement = planner.BindTable(tree);
    ExecutionContext context(nullptr);
    // TODO: build plan and execute
  }
};
} // end namespace GraphDB