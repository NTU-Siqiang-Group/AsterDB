#include "rocksdb/graph.h"
#include "rocksdb/sst_file_writer.h"
#include "rocksdb/options.h"
#include "rocksdb/env.h"

#include <string>
#include <filesystem>
#include <vector>
#include <gflags/gflags.h>
#include <unordered_map>
#include <fstream>
#include <algorithm>
int main() {
  rocksdb::Options options;
  // options.create_if_missing = true;
  rocksdb::RocksGraph* db = new rocksdb::RocksGraph(options, 3);
  for (int i = 0; i < 425957; i++) {
    rocksdb::Edges edges;
    db->GetAllEdges(i, &edges);
  }
  delete db;
  return 0;
}