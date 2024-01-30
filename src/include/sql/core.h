#pragma once

#include "storage/store.h"

namespace GraphDB {
/*maintain the execution context for a query:
1. Transaction manager (sequence number, optional)
2. kv store
*/
class ExecutionContext {
 public:
  ExecutionContext(GraphStore* store): store_(store) {}
 private:
  GraphStore* store_;
};

enum DataType {
  INT,
  STRING,
  FLOAT,
  BOOL,
  NULLTYPE,
};

struct Datum {
  DataType type;
  int64_t int_val;
  std::string str_val;
  double float_val;
};
} // namespace GraphDB