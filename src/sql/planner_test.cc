#include "sql/planner.h"
#include "gtest/gtest.h"
#include <iostream>

namespace GraphDB {
class SQLPlannerTest : public testing::Test {
 public:
  duckdb::PostgresParser GetParser() {
    return duckdb::PostgresParser();
  }
};

TEST_F(SQLPlannerTest, ParseSimpleInsert) {
  auto sqls = std::vector<std::string>{
    "INSERT INTO vertex (id) VALUES (100);",
    "INSERT INTO vertex VALUES (200);",
    "INSERT INTO edge (src, dst) VALUES (100, 200);",
    "INSERT INTO edge VALUES (200, 100);"
  };
  auto parser = GetParser();
  parser.Parse("INSERT INTO vertex (id) VALUES (1);");
  auto tree = parser.parse_tree;
  GraphSQLPlanner planner;
  auto statement = planner.BindTable(tree);
  std::cout << statement->ToString() << std::endl;
}

TEST_F(SQLPlannerTest, ParseSimpleDelete) {
  auto parser = GetParser();
  parser.Parse("DELETE FROM vertex WHERE id = 1;");
  auto tree = parser.parse_tree;
  GraphSQLPlanner planner;
  auto statement = planner.BindTable(tree);
  std::cout << statement->ToString() << std::endl;
}

} // end namespace GraphDB

int main(int argc, char** argv) {
  testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}