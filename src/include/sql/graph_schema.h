#pragma once

#include <map>
#include <string>
#include <vector>

namespace GraphDB {
struct SchemaMeta {
  std::string table_name;
  std::vector<std::string> column_names;
};
} // namespace GraphDB