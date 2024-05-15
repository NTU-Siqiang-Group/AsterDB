graph = TinkerGraph.open();
g = graph.traversal();
graph.currentVertexId = 3072627;

rand = new Random();

depth = 3;
bfs_time = 0;
for (int i = 0; i < 10; i++) {
  startId = rand.nextInt(graph.currentVertexId as Integer);
  v = g.V(startId);
  t = System.nanoTime();
  count = v.repeat(both().where(without("x")).aggregate("x")).times(depth).cap("x").next().size();
  exec_time = System.nanoTime() - t;
  println("BFS start from " + startId + " finished in " + exec_time + " ns");
  bfs_time += exec_time;
}
println("avg bfs: ${(double)bfs_time / 10 / 1000}");