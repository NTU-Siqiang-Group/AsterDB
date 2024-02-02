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

enum class ExpressionType {
  COLUMN_REF,
  CONSTANT,
  BOOL_EXPR,
  PGA_EXPR,
};

class Expression {
 public:
  Expression(ExpressionType type): type_(type) {}
  ExpressionType type_;
  virtual std::string ToString() {
    return "";
  }
};

class ConstantExpression : public Expression {
 public:
  ConstantExpression(Datum val): Expression(ExpressionType::CONSTANT), val_(val) {}
  virtual std::string ToString() override {
    switch (val_.type) {
      case DataType::INT:
        return std::to_string(val_.int_val);
      case DataType::STRING:
        return val_.str_val;
      case DataType::FLOAT:
        return std::to_string(val_.float_val);
      case DataType::BOOL:
        return val_.int_val == 1 ? "true" : "false";
      case DataType::NULLTYPE:
        return "null";
    }
  }
 private:
  Datum val_;
};

class ColumnRefExpression : public Expression {
 public:
  ColumnRefExpression(std::string table_name, std::string col_name):
    Expression(ExpressionType::COLUMN_REF), col_name_(col_name), table_name_(table_name) {}
  virtual std::string ToString() override {
    return table_name_ + "." + col_name_;
  }
 private:
  std::string col_name_;
  std::string table_name_;
};

class PGAExpression : public Expression {
 public:
  PGAExpression(std::string name, std::unique_ptr<Expression> lexpr, std::unique_ptr<Expression> rexpr): 
    Expression(ExpressionType::PGA_EXPR), name_(name), lexpr_(std::move(lexpr)), rexpr_(std::move(rexpr)) {}
  virtual std::string ToString() override {
    return lexpr_->ToString() + " " + name_ + " " + rexpr_->ToString();
  }
 private:
  std::string name_;
  std::unique_ptr<Expression> lexpr_;
  std::unique_ptr<Expression> rexpr_;
};

class BoolExpression : public Expression {
 public:
  BoolExpression(std::string name, std::unique_ptr<Expression> lexpr, std::unique_ptr<Expression> rexpr): 
    Expression(ExpressionType::BOOL_EXPR), name_(name), lexpr_(std::move(lexpr)), rexpr_(std::move(rexpr)) {}
  virtual std::string ToString() override {
    return lexpr_->ToString() + " " + name_ + " " + rexpr_->ToString();
  }
 private:
  std::string name_;
  std::unique_ptr<Expression> lexpr_;
  std::unique_ptr<Expression> rexpr_;
};

class Statement {
 public:
  Statement(std::string table_name, StatementType type): table_name_(table_name), type_(type) {}
  virtual std::string ToString() {
    return "";
  }
 private:
  std::string table_name_;
  StatementType type_;
};
class InsertStatement : public Statement {
 public:
  InsertStatement(std::string table_name, std::vector<std::vector<std::unique_ptr<Expression>>>&& insert_list): 
    Statement(table_name, StatementType::INSERT), table_name_(table_name), insert_list_(std::move(insert_list)) {}
  virtual std::string ToString() override {
    std::string ret = "INSERT INTO " + table_name_ + " VALUES (";
    for (auto& value_list: insert_list_) {
      ret += "(";
      for (auto& value: value_list) {
        ret += value->ToString() + ",";
      }
      ret = ret.substr(0, ret.size() - 1);
      ret += "),";
    }
    ret = ret.substr(0, ret.size() - 1);
    ret += ")";
    return ret;
  }
 private:
  std::string table_name_;
  std::vector<std::vector<std::unique_ptr<Expression>>> insert_list_;
};
class DeleteStatement : public Statement {
 public:
  DeleteStatement(std::string table_name, std::unique_ptr<Expression> where): 
    Statement(table_name, StatementType::DELETE), table_name_(table_name), where_clause_(std::move(where)) {}
  virtual std::string ToString() override {
    return "DELETE FROM " + table_name_ + " WHERE " + where_clause_->ToString();
  }
 private:
  std::string table_name_;
  std::unique_ptr<Expression> where_clause_;
};

class SelectStatement : public Statement {};

class GraphSQLPlanner {
 public:
  std::unique_ptr<Statement> BindTable(duckdb_libpgquery::PGList* ast);
 private:
  std::unique_ptr<Statement> BindStatement(duckdb_libpgquery::PGNode* node);
  std::unique_ptr<InsertStatement> BindInsertStatement(duckdb_libpgquery::PGInsertStmt* stmt);
  std::unique_ptr<DeleteStatement> BindDeleteStatement(duckdb_libpgquery::PGDeleteStmt* stmt);
  std::unique_ptr<SelectStatement> BindSelectStatement(duckdb_libpgquery::PGSelectStmt* stmt);
  std::unique_ptr<Expression> BindExpression(duckdb_libpgquery::PGNode* node);
  std::vector<std::unique_ptr<Expression>> BindExpressionList(duckdb_libpgquery::PGList* list);

  std::unique_ptr<ConstantExpression> BindConstant(duckdb_libpgquery::PGNode* node);
  std::unique_ptr<PGAExpression> BindPGAExpression(duckdb_libpgquery::PGAExpr* node);
  std::unique_ptr<BoolExpression> BindBoolExpression(duckdb_libpgquery::PGBoolExpr* node);
  std::unique_ptr<ColumnRefExpression> BindColumnRefExpression(duckdb_libpgquery::PGColumnRef* node);
};
} // namespace GraphDB