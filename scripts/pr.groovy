conf = new BaseConfiguration();
conf.setProperty("updatePolicy", 2);
graph = TinkerGraph.open(conf);
g = graph.traversal();
graph.currentVertexId = GRAPH_VERTEX_NUM;

allIds = [];
for (int i = 0; i < graph.currentVertexId; i++) {
  allIds.add(i);
}

dampingFactor = 0.85;
maxIters = 10;

prs = [:];
initialRank = 1.0 / allIds.size();

for (int i = 0; i < allIds.size(); i++) {
  prs[allIds[i]] = initialRank; // initialize
}

t = System.nanoTime();
for (int i = 0; i < maxIters; i++) {
  newPrs = [:];
  for (int j = 0; j < allIds.size(); j++) {
    inIds = g.V(allIds[j]).in().id().fold().next();
    def double rankSum = 0.0;
    for (int k = 0; k < inIds.size(); k++) {
      rankSum += (double)prs[inIds[k] as Integer];
    }
    newRank = initialRank + dampingFactor * rankSum;
    newPrs[allIds[j]] = newRank;
  }
  prs = newPrs;
}
exec_time = System.nanoTime() - t;
println("PR finished in ${exec_time} ns");
