rand = new Random();
get_time = 0;
add_time = 0;

op_arr = [];
rratio = 0.1
total_ops = 2000000;
rops = total_ops * rratio as Integer;
wops = total_ops -  rops;
println("rops: " + rops + " wops: " + wops);


for (int i = 0; i < rops; i++) {
  op_arr.add(1);
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
// graph.currentVertexId = 3072627;
println("currentVertex: ${graph.currentVertexId}")
tl = "new-edge";
cnts = 0

if (conf.getInt("updatePolicy") == 2) {
  if (wops == 800000) {
    graph.setCacheMissRate(0.6);
  }// adaptive
  println("warming up for adaptive...");
  warmup_ops = 50000000;
  for (int i = 0; i < warmup_ops; i++) {
    vid1 = rand.nextInt(graph.currentVertexId as Integer);
    vid2 = rand.nextInt(graph.currentVertexId as Integer);
    v1 = g.V(vid1).next();
    v2 = g.V(vid2).next();
    v1.addEdge(tl, v2);
  }
  println("finished warmup for adaptive update: " + warmup_ops);
}

for (int i = 0; i < op_arr.size(); i++) {
  if (op_arr[i] > 0) {
    vid = rand.nextInt(graph.currentVertexId as Integer);
    v1 = g.V(vid);
    t = System.nanoTime();
    try {
      cnt = v1.out().count().next();
    } catch (Exception e) {
      // do nothing
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
    try {
      v1.addEdge(tl, v2);
    } catch (Exception e) {
      // do nothing
    }
    exec_time = System.nanoTime() - t;
    add_time += exec_time;
  }
}
get_avg_us = (double)get_time / rops / 1000;
add_avg_us = (double)add_time / wops / 1000;
println("get: ${get_avg_us}, add: ${add_avg_us}")
println((get_time + add_time) / 1000000);
println("avg out count: " + (double)cnts / rops)
