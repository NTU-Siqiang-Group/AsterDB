#pragma once

#include <memory>

#include "duck_pg_query/postgres_parser.hpp"
#include "duck_pg_query/nodes/parsenodes.hpp"

#include "sql/core.h"

/*
Currently, we don't need to plan or optimize the AST. Therefore,
we only bind the table (i.e., vertex and edge) to the AST.
*/
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
namespace GraphDB {
enum class StatementType {
  INSERT,
  DELETE,
  SELECT
};
class Statement {
  public:
    Statement(std::string table_name, StatementType type): table_name_(table_name), type_(type) {}
  private:
    std::string table_name_;
    StatementType type_;
};
class InsertStatement : public Statement {
  public:
    InsertStatement(std::string table_name, std::vector<std::vector<Datum>> insert_list): Statement(table_name, StatementType::INSERT), insert_list_(insert_list) {}
  private:
    std::vector<std::vector<Datum>> insert_list_;
};
class DeleteStatement : public Statement {};
class SelectStatement : public Statement {};
class GraphSQLPlanner {
 public:
  std::unique_ptr<Statement> BindTable(duckdb_libpgquery::PGList* ast);
 private:
  std::unique_ptr<Statement> BindStatement(duckdb_libpgquery::PGNode* node);
  std::unique_ptr<InsertStatement> BindInsertStatement(duckdb_libpgquery::PGInsertStmt* stmt);
  std::unique_ptr<DeleteStatement> BindDeleteStatement(duckdb_libpgquery::PGDeleteStmt* stmt);
  std::unique_ptr<SelectStatement> BindSelectStatement(duckdb_libpgquery::PGSelectStmt* stmt);
  Datum BindConstant(duckdb_libpgquery::PGNode* node);
};
} // namespace GraphDB