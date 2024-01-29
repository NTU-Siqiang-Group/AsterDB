#include "http_server.h"

namespace GraphDB {
void GraphServerCtrl::query(const drogon::HttpRequestPtr& req,
             std::function<void(const drogon::HttpResponsePtr&)>&& callback) {
  std::string result;
  auto req_obj = *req->jsonObject();
  auto query = req_obj["query"].asString();
  if (db_ != nullptr) {
    db_->ExecuteQuery(query, result);
  }
  Json::Value ret;
  ret["result"] = result;
  auto resp = drogon::HttpResponse::newHttpJsonResponse(ret);
  callback(resp);
}
} // namespace GraphDB
