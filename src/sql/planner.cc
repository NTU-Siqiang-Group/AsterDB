#include "sql/planner.h"

namespace GraphDB {
std::unique_ptr<Statement> GraphSQLPlanner::BindTable(duckdb_libpgquery::PGList* ast) {
  std::unique_ptr<Statement> ret(nullptr);
  for (auto entry = ast->head; entry != nullptr; entry = entry->next) {
    ret = BindStatement(reinterpret_cast<duckdb_libpgquery::PGNode*>(entry->data.ptr_value));
  }
  return ret;
}

std::unique_ptr<Statement> GraphSQLPlanner::BindStatement(duckdb_libpgquery::PGNode* stmt) {
  switch (stmt->type) {
    case duckdb_libpgquery::T_PGRawStmt:
      return BindStatement(reinterpret_cast<duckdb_libpgquery::PGRawStmt *>(stmt)->stmt);
    case duckdb_libpgquery::T_PGInsertStmt:
      return BindInsertStatement(reinterpret_cast<duckdb_libpgquery::PGInsertStmt *>(stmt));
    case duckdb_libpgquery::T_PGDeleteStmt:
      return BindDeleteStatement(reinterpret_cast<duckdb_libpgquery::PGDeleteStmt *>(stmt));
    case duckdb_libpgquery::T_PGSelectStmt:
      return BindSelectStatement(reinterpret_cast<duckdb_libpgquery::PGSelectStmt *>(stmt));
    default:
      return nullptr;
  }
}

std::unique_ptr<InsertStatement> GraphSQLPlanner::BindInsertStatement(duckdb_libpgquery::PGInsertStmt* stmt) {
  std::string table_name = std::string(stmt->relation->relname);
  auto select_stmt = reinterpret_cast<duckdb_libpgquery::PGSelectStmt *>(stmt->selectStmt);
  auto lists = select_stmt->valuesLists;
  std::vector<std::vector<std::unique_ptr<Expression>>> insert_list;
  for (auto value_list = lists->head; value_list != nullptr; value_list = value_list->next) {
    auto target = static_cast<duckdb_libpgquery::PGList *>(value_list->data.ptr_value);
    std::vector<std::unique_ptr<Expression>> value_tuples;
    for (auto node = target->head; node != nullptr; node = node->next) {
      auto val = reinterpret_cast<duckdb_libpgquery::PGNode *>(node->data.ptr_value);
      value_tuples.push_back(BindConstant(val));
    }
    insert_list.push_back(std::move(value_tuples));
  }
  return std::make_unique<InsertStatement>(table_name, std::move(insert_list));
}

std::unique_ptr<DeleteStatement> GraphSQLPlanner::BindDeleteStatement(duckdb_libpgquery::PGDeleteStmt* stmt) {
  std::string table_name = std::string(stmt->relation->relname);
  std::unique_ptr<Expression> where_clause;
  if (stmt->whereClause != nullptr) {
    where_clause = BindExpression(reinterpret_cast<duckdb_libpgquery::PGNode *>(stmt->whereClause));
  }
  return std::make_unique<DeleteStatement>(table_name, std::move(where_clause));
}

std::unique_ptr<SelectStatement> GraphSQLPlanner::BindSelectStatement(duckdb_libpgquery::PGSelectStmt* stmt) {
  return nullptr;
}

std::unique_ptr<ConstantExpression> GraphSQLPlanner::BindConstant(duckdb_libpgquery::PGNode* node) {
  Datum ret;
  if (node->type != duckdb_libpgquery::T_PGAConst) {
    return nullptr;
  }
  auto constant_node = reinterpret_cast<duckdb_libpgquery::PGAConst *>(node);
  const auto& val = constant_node->val;
  switch (val.type) {
    case duckdb_libpgquery::T_PGInteger:
      ret.type = DataType::INT;
      ret.int_val = val.val.ival;
      break;
    case duckdb_libpgquery::T_PGString: {
      std::string str(val.val.str);
      ret.type = DataType::STRING;
      ret.str_val = str;
      break;
    }
    case duckdb_libpgquery::T_PGFloat:
      ret.type = DataType::FLOAT;
      ret.float_val = std::stod(val.val.str);
      break;
    case duckdb_libpgquery::T_PGNull:
      ret.type = DataType::NULLTYPE;
      break;
    default: break;
  }
  return std::make_unique<ConstantExpression>(ret);
}

std::unique_ptr<PGAExpression> GraphSQLPlanner::BindPGAExpression(duckdb_libpgquery::PGAExpr* node) {
  auto op_name = std::string((reinterpret_cast<duckdb_libpgquery::PGValue *>(node->name->head->data.ptr_value))->val.str);
  if (node->kind != duckdb_libpgquery::PG_AEXPR_OP) {
    return nullptr;
  }
  std::unique_ptr<Expression> left = BindExpression(reinterpret_cast<duckdb_libpgquery::PGNode *>(node->lexpr));
  std::unique_ptr<Expression> right = BindExpression(reinterpret_cast<duckdb_libpgquery::PGNode *>(node->rexpr));
  return std::make_unique<PGAExpression>(op_name, std::move(left), std::move(right));
}

std::unique_ptr<BoolExpression> GraphSQLPlanner::BindBoolExpression(duckdb_libpgquery::PGBoolExpr* node) {
  if (node == nullptr) {
    return nullptr;
  }
  std::string op_name;
  switch (node->boolop) {
    case duckdb_libpgquery::PG_AND_EXPR:
    case duckdb_libpgquery::PG_OR_EXPR: {
      if (node->boolop == duckdb_libpgquery::PG_AND_EXPR) {
        op_name = "and";
      } else {
        op_name = "or";
      }
      auto exprs = BindExpressionList(node->args);
      auto expr = std::make_unique<BoolExpression>(op_name, std::move(exprs[0]), std::move(exprs[1]));
      for (size_t i = 2; i < exprs.size(); i ++) {
        expr = std::make_unique<BoolExpression>(op_name, std::move(expr), std::move(exprs[i]));
      }
    }
    case duckdb_libpgquery::PG_NOT_EXPR: {
      // TODO
      return nullptr;
    }
  }
}
std::vector<std::unique_ptr<Expression>> GraphSQLPlanner::BindExpressionList(duckdb_libpgquery::PGList* list) {
  auto select_list = std::vector<std::unique_ptr<Expression>>{};
  for (auto node = list->head; node != nullptr; node = lnext(node)) {
    auto target = reinterpret_cast<duckdb_libpgquery::PGNode *>(node->data.ptr_value);
    auto expr = BindExpression(target);
    select_list.push_back(std::move(expr));
  }
  return select_list;
}

std::unique_ptr<Expression> GraphSQLPlanner::BindExpression(duckdb_libpgquery::PGNode* node) {
  if (node == nullptr) {
    return nullptr;
  }
  switch (node->type) {
    case duckdb_libpgquery::T_PGAConst:
      return BindConstant(node);
    case duckdb_libpgquery::T_PGColumnRef:
      return BindColumnRefExpression(reinterpret_cast<duckdb_libpgquery::PGColumnRef *>(node));
    case duckdb_libpgquery::T_PGBoolExpr:
      return BindBoolExpression(reinterpret_cast<duckdb_libpgquery::PGBoolExpr *>(node));
    case duckdb_libpgquery::T_PGAExpr:
      return BindPGAExpression(reinterpret_cast<duckdb_libpgquery::PGAExpr *>(node));
    default:
      return nullptr;
  }
}

std::unique_ptr<ColumnRefExpression> GraphSQLPlanner::BindColumnRefExpression(duckdb_libpgquery::PGColumnRef* node) {
  auto fields = node->fields;
  std::vector<std::string> column_names;
  auto head_node = static_cast<duckdb_libpgquery::PGNode *>(fields->head->data.ptr_value);
  for (auto node = fields->head; node != nullptr; node = node->next) {
    column_names.push_back(reinterpret_cast<duckdb_libpgquery::PGValue *>(node->data.ptr_value)->val.str);
  }
  if (column_names.size() > 1) {
    return std::make_unique<ColumnRefExpression>(column_names[0], column_names[1]);
  }
  // TODO: find table
  return std::make_unique<ColumnRefExpression>("vertex", column_names[0]);
}


} // namespace GraphDB