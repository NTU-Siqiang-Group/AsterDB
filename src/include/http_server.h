#pragma once

#include "drogon/HttpController.h"
#include "db.h"

namespace GraphDB {
class GraphServerCtrl : public drogon::HttpController<GraphServerCtrl, false> {
 public:
  METHOD_LIST_BEGIN
  METHOD_ADD(GraphServerCtrl::query, "/query", drogon::Get);
  METHOD_LIST_END
 private:
  void query(const drogon::HttpRequestPtr& req,
             std::function<void(const drogon::HttpResponsePtr&)>&& callback);
  GraphDB* db_;
};
} // namespace GraphDB
