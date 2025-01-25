rand = new Random();
get_time = 0;
add_time = 0;

op_arr = [];
warmup_arr = [];
rratio = 0.1
total_ops = 100000;
warmup_ops = 50000000;
rops = total_ops * rratio as Integer;
wops = total_ops -  rops;
println("rops: " + rops + " wops: " + wops);

warmuprops = 0;
warmupwops = warmup_ops;

for (int i = 0; i < rops; i++) {
  op_arr.add(1);
}

for (int i =  0; i < warmuprops; i++) {
  warmup_arr.add(1);
}

for (int i = 0; i < wops; i++) {
  op_arr.add(0);
}

for (int i = 0; i < warmupwops; i++) {
  warmup_arr.add(0);
}

op_arr.shuffle();
warmup_arr.shuffle();

conf = new BaseConfiguration();
conf.setProperty("updatePolicy", 2); // adaptive
graph = TinkerGraph.open(conf);
graph.setWorkload(rops / (wops + rops));
g = graph.traversal();
// graph.currentVertexId = 3072627;
println("currentVertex: ${graph.currentVertexId}")
tl = "new-edge";
cnts = 0
warmup_num = 0;
for (int i = 0; i < warmup_arr.size(); i++) {
  if (warmup_arr[i] > 0) {
    vid = rand.nextInt(graph.currentVertexId as Integer);
    v1 = g.V(vid);
    cnt = v1.out().count().next();
    // warmup_num++;
  } else {
    vid1 = rand.nextInt(graph.currentVertexId as Integer);
    vid2 = rand.nextInt(graph.currentVertexId as Integer);
    v1 = g.V(vid1).next();
    v2 = g.V(vid2).next();
    v1.addEdge(tl, v2);
    warmup_num++;
  }
}

sleep(60);
println("warm up finished: " + warmup_num);

