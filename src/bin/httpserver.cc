#include "http_server.h"

int main() {
  auto handler = std::make_shared<GraphDB::GraphServerCtrl>();
  drogon::app().
    addListener("0.0.0.0", 9897).
    registerController(handler).
    setThreadNum(10).
    setLogLevel(trantor::Logger::kDebug).
    setLogPath("./", "graphdb_server").
    run();
  return 0;
}