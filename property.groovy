conf = new BaseConfiguration();
conf.setProperty("updatePolicy", 1); // adaptive
graph = TinkerGraph.open(conf);
g = graph.traversal();
graph.currentVertexId = 28400000;

// freebase large
vertexKey = "freebaseid"
edgeKey = "label"
vertexTarget = '484848485248'
edgeTarget = '/american_football/football_coach/coaching_history'

// ldbc
// vertexKey = "xlabel"
// edgeKey = "label"
// vertexTarget = "post"
// edgeTarget = "hasCreator"

maxId = graph.currentVertexId;
ops = 10;
graph.createIndex(vertexKey, Vertex.class);
graph.createIndex(edgeKey, Edge.class);

// Vertex property search
start = System.nanoTime();
println("${g.V().has(vertexKey, vertexTarget).count().next()}");
end = System.nanoTime();
println("Time of vertex property search: ${end - start}ns");

// Edge property search
start = System.nanoTime();
println("${g.E().has(edgeKey, edgeTarget).count().next()}");
end = System.nanoTime();
println("Time of edge property search: ${end - start}ns");

// update vertex property
rng = new Random();
totalTime = 0;
for (int i = 0; i < ops; i++) {
  vid = rng.nextInt(maxId as Integer);
  v = g.V(vid).next();
  start = System.nanoTime();
  v.property(vertexKey, 'new-property');
  end = System.nanoTime();
  totalTime += end - start;
}
println("Time of update vertex property: ${totalTime / ops}ns");

// update edge property
totalTime = 0;
for (int i = 0; i < ops; i++) {
  vid = rng.nextInt(maxId as Integer);
  p = g.V(vid).outE();
  while (!p.hasNext()) {
    vid = rng.nextInt(maxId as Integer);
    p = g.V(vid).outE();
  }
  e = p.next();
  start = System.nanoTime();
  e.property(edgeKey, "new-label");
  end = System.nanoTime();
  totalTime += end - start;
}
println("Time of update edge property: ${totalTime / ops}ns");

// insert vertex property
rng = new Random();
totalTime = 0;
for (int i = 0; i < ops; i++) {
  vid = rng.nextInt(maxId as Integer);
  v = g.V(vid).next();
  start = System.nanoTime();
  v.property('new-key', 'new-property');
  end = System.nanoTime();
  totalTime += end - start;
}
println("Time of insert vertex property: ${totalTime / ops}ns");

// insert edge property
totalTime = 0;
for (int i = 0; i < ops; i++) {
  vid = rng.nextInt(maxId as Integer);
  p = g.V(vid).outE();
  while (!p.hasNext()) {
    vid = rng.nextInt(maxId as Integer);
    p = g.V(vid).outE();
  }
  e = p.next();
  start = System.nanoTime();
  e.property("new-label-key", "new-label");
  end = System.nanoTime();
  totalTime += end - start;
}
println("Time of insert edge property: ${totalTime / ops}ns");

// remove vertex property
rng = new Random();
totalTime = 0;
for (int i = 0; i < ops; i++) {
  vid = rng.nextInt(maxId as Integer);
  v = g.V(vid).next();
  start = System.nanoTime();
  v.property(vertexKey, '');
  end = System.nanoTime();
  totalTime += end - start;
}
println("Time of remove vertex property: ${totalTime / ops}ns");

// remove edge property
totalTime = 0;
for (int i = 0; i < ops; i++) {
  vid = rng.nextInt(maxId as Integer);
  p = g.V(vid).outE();
  while (!p.hasNext()) {
    vid = rng.nextInt(maxId as Integer);
    p = g.V(vid).outE();
  }
  e = p.next();
  start = System.nanoTime();
  e.property(edgeKey, "");
  end = System.nanoTime();
  totalTime += end - start;
}
println("Time of remove edge property: ${totalTime / ops}ns");