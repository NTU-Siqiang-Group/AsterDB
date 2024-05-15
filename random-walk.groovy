decayFactor = 0.2;
conf = new BaseConfiguration();
conf.setProperty("updatePolicy", 2); // adaptive
graph = TinkerGraph.open(conf);
g = graph.traversal();
// graph.currentVertexId = 3072627;

rand = new Random();

rw_time = 0;

for (int i = 0; i < 100; i++) {
  t = System.nanoTime();
  // vid1 = p.next();
  vid1 = rand.nextInt(graph.currentVertexId as Integer);
  step = 0;
  // start random walk
  // println("start from ${vid1}");
  outIds = g.V(vid1).out().id().fold().next();
  startOutIds = outIds;
  while (outIds.size()) {
    def random_out = Math.random();
    if (random_out <= decayFactor) {
      break;
    }
    step += 1;
    rand = new Random();
    idx = rand.nextInt() % outIds.size();
    next_vid = outIds[idx];
    outIds = g.V(next_vid).out().id().fold().next();
    if (outIds.size() == 0) {
      outIds = startOutIds;
    }
  }
  exec_time = System.nanoTime() - t;
  // println("Random Walk step: " + step + " finished in " + exec_time + " ns");
  rw_time += exec_time;
}

println("avg random walk: ${rw_time / 100 / 1000} us");