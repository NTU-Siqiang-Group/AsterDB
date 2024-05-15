conf = new BaseConfiguration();
conf.setProperty("updatePolicy", 2); // adaptive
graph = TinkerGraph.open(conf);
g = graph.traversal();

total_ops = 100000;
println("currentVertexId: " + graph.currentVertexId);
rand = new Random();

// test get neighbors
total_exec = 0;
for (int i = 0; i < total_ops; i++) {
  vid = rand.nextInt(graph.currentVertexId as Integer);
  t = System.nanoTime();
  g.V(vid).out().count().next();
  exec_time = System.nanoTime() - t;
  // println("Get Vertex in " + exec_time + " ns");
  total_exec += exec_time;
}
println("get avg: ${total_exec / total_ops / 1000} us");

// test add vertex
total_exec = 0;
for (int i = 0; i < total_ops; i++) {
  t = System.nanoTime();
  graph.addVertex("");
  exec_time = System.nanoTime() - t;
  // println("Add Vertex in " + exec_time + " ns");
  total_exec += exec_time;
}
println("addv avg: ${total_exec / total_ops / 1000} us");

// test add edge
edge_to_del = [];
total_exec = 0;
for (int i = 0; i < total_ops; i++) {
  vid1 = rand.nextInt(graph.currentVertexId as Integer);
  vid2 = rand.nextInt(graph.currentVertexId as Integer);
  v1 = g.V(vid1).next();
  v2 = g.V(vid2).next();
  t = System.nanoTime();
  edge_to_del.add(v1.addEdge("", v2));
  exec_time = System.nanoTime() - t;
  // println("Edge added in " + exec_time + " ns");
  total_exec += exec_time;
}
println("adde avg: ${total_exec / total_ops / 1000} us");

// test delete edge
total_exec = 0;
for (int i = 0; i < total_ops; i++) {
  t = System.nanoTime();
  edge_to_del[i].remove();
  exec_time = System.nanoTime() - t;
  // println("Edge deleted in " + exec_time + " ns");
  total_exec += exec_time;
}
println("dele avg: ${total_exec / total_ops / 1000} us")
