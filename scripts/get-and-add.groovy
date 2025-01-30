rand = new Random();
// println("currentVertexId: ${graph.currentVertexId}");
get_time = 0;
add_time = 0;

rops = 100000;
wops = 100000;

op_arr = [];

onehop = (rops * 0.75) as Integer;
twohop = rops - onehop;

for (int i = 0; i < rops; i++) {
  if (i <= onehop) {
    op_arr.add(1);
  } else {
    op_arr.add(2);
  }
}

for (int i = 0; i < wops; i++) {
  op_arr.add(0);
}

op_arr.shuffle();


conf = new BaseConfiguration();
conf.setProperty("updatePolicy", 2); // adaptive
graph = TinkerGraph.open(conf);
graph.setWorkload(rops / (wops + rops));
g = graph.traversal();
println("currentVertex: ${graph.currentVertexId}")
tl = "new-edge";


cnts = 0

for (int i = 0; i < op_arr.size(); i++) {
  if (op_arr[i] > 0) {
    vid = rand.nextInt(graph.currentVertexId as Integer);
    v1 = g.V(vid);
    t = System.nanoTime();
    if (op_arr[i] == 1) {
      cnt = v1.out().count().next();
    } else {
      cnt = v1.out().count().next();
    }
    exec_time = System.nanoTime() - t;
    cnts += cnt;
    get_time += exec_time;
  } else {
    vid1 = rand.nextInt(graph.currentVertexId as Integer);
    vid2 = rand.nextInt(graph.currentVertexId as Integer);
    v1 = g.V(vid1).next();
    v2 = g.V(vid2).next();
    t = System.nanoTime();
    v1.addEdge(tl, v2);
    exec_time = System.nanoTime() - t;
    add_time += exec_time;
  }
}
get_avg_us = (double)get_time / rops / 1000;
add_avg_us = (double)add_time / wops / 1000;
println("get: ${get_avg_us}, add: ${add_avg_us}")
println((get_time + add_time) / 1000000);
println("avg out count: " + (double)cnts / rops)
