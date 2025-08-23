conf = new BaseConfiguration();
conf.setProperty("updatePolicy", 2);
graph = TinkerGraph.open(conf);
g = graph.traversal();
graph.currentVertexId = GRAPH_VERTEX_NUM;

allIds = [];
for (int i = 0; i < graph.currentVertexId; i++) {
  allIds.add(i);
}

labels = [:];
for (int i = 0; i < allIds.size(); i++) {
  labels[allIds[i]] = i;
}
rng = new Random();
maxIters = 10;
t = System.nanoTime();
for (int i = 0; i < maxIters; i++) {
  println("current Iter: ${i}");
  changeMade = false;
  for (int j = 0; j < allIds.size(); j++) {
    idx = rng.nextInt(allIds.size());
    tmp = allIds[j];
    allIds[j] = allIds[idx];
    allIds[idx] = tmp;
  }
  for (int j = 0; j < allIds.size(); j++) {
    Map<Integer, Integer> labelCounts = new HashMap<>();
    bothIds = g.V(allIds[j]).both().id().fold().next();
    for (int k = 0; k < bothIds.size(); k++) {
      curLabel = labels[bothIds[k] as Integer];
      labelCounts.put(curLabel, labelCounts.getOrDefault(curLabel, 0) + 1);
    }
    int maxCount = -1;
    int newLabel = labels[allIds[j]];
     for (Map.Entry<Integer, Integer> entry : labelCounts.entrySet()) {
      if (entry.getValue() > maxCount) {
        maxCount = entry.getValue();
        newLabel = entry.getKey();
      }
    }
    if (newLabel != labels[allIds[j]]) {
      labels[allIds[j]] = newLabel;
      changeMade = true;
    }
  }
  if (!changeMade) {
    break;
  }
}
exec_time = System.nanoTime() - t;
println("CDLP finished in ${exec_time} ns");