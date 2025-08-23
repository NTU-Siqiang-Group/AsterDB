conf = new BaseConfiguration();
conf.setProperty("updatePolicy", 2);
graph = TinkerGraph.open(conf);
g = graph.traversal();
graph.currentVertexId = GRAPH_VERTEX_NUM;

rand = new Random();

depth = 5;

for (int i = 0; i < 1; i++) {
  startId = BFS_SOURCE;
  v = g.V(startId);
  t = System.nanoTime();
  count = v.repeat(out()).emit().times(depth).dedup().toList();
  exec_time = System.nanoTime() - t;
  println("BFS start from " + startId + " finished in " + exec_time + " ns, count: " + count.size());
}