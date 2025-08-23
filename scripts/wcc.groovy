conf = new BaseConfiguration();
conf.setProperty("updatePolicy", 2);
graph = TinkerGraph.open(conf);
g = graph.traversal();
graph.currentVertexId = GRAPH_VERTEX_NUM;

allIds = [];
for (int i = 0; i < graph.currentVertexId; i++) {
  allIds.add(i);
}

wcc = [:];
changeMade = true;
for (int i = 0; i < allIds.size(); i++) {
  wcc[allIds[i]] = i;
}
t = System.nanoTime();
while (changeMade) {
  changeMade = false;
  newWcc = [:];
  for (int i = 0; i < allIds.size(); i++) {
    newWcc[allIds[i]] = wcc[allIds[i]];
    bothIds = g.V(allIds[i]).both().id().fold().next();
    minVal = wcc[allIds[i]];
    for (int j = 0; j < bothIds.size(); j++) {
      if (wcc[bothIds[j] as Integer] < minVal) {
        minVal = wcc[bothIds[j] as Integer];
      }
    }
    if (minVal != wcc[allIds[i]]) {
      changeMade = true;
      newWcc[allIds[i]] = minVal;
    }
  }
  wcc = newWcc;
}
exec_time = System.nanoTime() - t;
println("WCC finished in ${exec_time} ns");