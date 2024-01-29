#pragma once

#include <string>

#include "sql/parser.h"

namespace GraphDB {
// interface of GraphDB
class GraphDB {
 public:
  virtual void ExecuteQuery(const std::string& query, std::string& result) = 0;
};

class GraphDBImpl : public GraphDB {
 public:
  virtual void ExecuteQuery(const std::string& query, std::string& result) override {
    
  }
 private:
  GraphSQLParser parser_;
};
} // end namespace GraphDB