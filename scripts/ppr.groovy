conf = new BaseConfiguration();
conf.setProperty("updatePolicy", 2); // adaptive
graph = TinkerGraph.open(conf);
g = graph.traversal();
// graph.currentVertexId = 3072627;

rand = new Random();

residuals = [:];
pprs = [:];

max_steps = 10;

alpha = 0.2;
bitmap = [:]

for (int i = 0; i <= graph.currentVertexId; i++) {
  residuals[i] = 0.0;
  pprs[i] = 0.0;
  bitmap[i] = 0;
}

empty_bitmap = bitmap;

println("init finished");

src = rand.nextInt(graph.currentVertexId as Integer);
residuals[src] = 1.0;
bitmap[src] = 1;
q = [src] as Queue;
// println("${g.V('public.vertex:::369').out().id().fold().next()}")
t = System.nanoTime();
for (int i = 0; i < max_steps; i++) {
  nxtq = [] as Queue;
  println("at step ${i}, queue size: ${q.size()}...")
  while (!q.isEmpty()) {
    j = q.poll();
    outIds = g.V(j).out().id().fold().next();
    for (int k = 0; k < outIds.size(); k++) {
      cur_id = outIds[k] as Integer;
      if (residuals[cur_id] == null) {
        println("Vertex ${cur_id} not exists.");
        System.exit(1);
      }
      residuals[cur_id] += (1 - alpha) * (double)residuals[j] / outIds.size();
      if (residuals[cur_id] != 0 && bitmap[cur_id] == 0) {
        nxtq.offer(cur_id);
        bitmap[cur_id] = 1;
      }
    }
    pprs[j] += alpha * (double)residuals[j];
    residuals[j] = 0;
  }
  bitmap = empty_bitmap;
  q = nxtq;
}
exec_time = System.nanoTime() - t;
println("PPR finished in ${exec_time} ns");