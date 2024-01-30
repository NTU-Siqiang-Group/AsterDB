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
  std::vector<std::vector<Datum>> insert_list;
  for (auto value_list = lists->head; value_list != nullptr; value_list = value_list->next) {
    auto target = static_cast<duckdb_libpgquery::PGList *>(value_list->data.ptr_value);
    std::vector<Datum> value_tuples;
    for (auto node = target->head; node != nullptr; node = node->next) {
      auto val = reinterpret_cast<duckdb_libpgquery::PGNode *>(node->data.ptr_value);
      value_tuples.push_back(BindConstant(val));
    }
    insert_list.push_back(value_tuples);
  }
  return std::make_unique<InsertStatement>(table_name, insert_list);
}

Datum GraphSQLPlanner::BindConstant(duckdb_libpgquery::PGNode* node) {
  Datum ret;
  if (node->type != duckdb_libpgquery::T_PGAConst) {
    return ret;
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
    default:
  }
  return ret;
}

} // namespace GraphDB