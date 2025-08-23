conf = new BaseConfiguration();
conf.setProperty("updatePolicy", 2);
graph = TinkerGraph.open(conf);
g = graph.traversal();
graph.currentVertexId = GRAPH_VERTEX_NUM;

rand = new Random();

for (int i = 0; i < 1; i++) {
  srcId = SSSP_SOURCE;
  dstId = SSSP_DST;
  srcV = g.V(srcId);
  t = System.nanoTime();
  l = srcV.repeat(out().where(without("x")).aggregate("x")).until(hasId(dstId)).limit(1).path().count(local);
  if (l.hasNext()) {
    x = l.next();
  } else {
    x = -1;
  }
  exec_time = System.nanoTime() - t;
  println("Shortest Path from ${srcId} to ${dstId} having ${x} path in ${exec_time} ns");
}