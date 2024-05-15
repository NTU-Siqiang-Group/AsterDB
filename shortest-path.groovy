graph = TinkerGraph.open();
g = graph.traversal();
graph.currentVertexId = 3072627;

rand = new Random();

for (int i = 0; i < 10; i++) {
  srcId = rand.nextInt(graph.currentVertexId as Integer);
  dstId = rand.nextInt(graph.currentVertexId as Integer);
  srcV = g.V(srcId);
  t = System.nanoTime();
  l = srcV.repeat(both().where(without("x")).aggregate("x")).until(hasId(dstId)).limit(1).path().count(local);
  if (l.hasNext()) {
    x = l.next();
  } else {
    x = -1;
  }
  exec_time = System.nanoTime() - t;
  println("Shortest Path from ${srcId} to ${dstId} having ${x} path in ${exec_time} ns");
}