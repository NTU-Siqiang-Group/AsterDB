// dataset = './dataset/ldbc.json2'
dataset = PATH_TO_DATASET
conf = new BaseConfiguration();
conf.setProperty("updatePolicy", 0); // eager
graph = TinkerGraph.open(conf);
g = graph.traversal();

// load vertex
vertexDataset  = dataset + '.vertex'
edgeDataset  = dataset + '.edge'
is = new FileReader(vertexDataset);
reader = new BufferedReader(is);
vs = [];
String line = null;
vertexKey = null;
vertexCnt = 0;
while ((line = reader.readLine()) != null) {
  String[] evs = line.split(" ");
  v = graph.addVertex("vertex") as Vertex;
  propStr = evs[1];
  String[] ps = propStr.split(":");
  v.property(ps[0], ps[1]);
  vertexKey = ps[0];
  vs.add(v);
  vertexCnt += 1;
  // if (vertexCnt % 10000 == 0) {
  //   println("processed vertex ${vertexCnt}");
  // }
}
graph.createIndex(vertexKey, Vertex.class);

is = new FileReader(edgeDataset);
reader = new BufferedReader(is);
edgePropKey = null;
edgeCnt = 0;
while ((line = reader.readLine()) != null) {
  String[] evs = line.split(" ");
  idx1 = evs[0].toInteger() - 1;
  idx2 = evs[1].toInteger() - 1;
  e = vs[idx1].addEdge("edg", vs[idx2]);
  String pStr = evs[2];
  String[] props = pStr.split(":");
  e.property(props[0], props[1]);
  edgePropKey = props[0];
  edgeCnt += 1;
  // if (edgeCnt % 10000 == 0) {
  //   println("processed edge ${edgeCnt}");
  // }
}
println("finished adding edges");
println("add property index: ${edgePropKey}");
graph.createIndex(edgePropKey, Edge.class);

// if (dataset.contains('freebase')) {
//   println("${g.V().has('freebaseid', '484848485248').count().next()}");
//   println("${g.E().has('label', '/american_football/football_coach/coaching_history').count().next()}");
// } else {
//   println("${g.V().has('xlabel', 'post').count().next()}");
//   println("${g.E().has('label', 'hasCreator').count().next()}");
// }
// graph.closeDB();
if (dataset.contains('freebase')) {
  vertexKey = "freebaseid"
  edgeKey = "label"
  vertexTarget = '484848485248'
  edgeTarget = '/american_football/football_coach/coaching_history'
} else {
  vertexKey = "xlabel"
  edgeKey = "label"
  vertexTarget = "post"
  edgeTarget = "hasCreator"
}

println("cur max id: ${graph.currentVertexId}")
maxId = graph.currentVertexId;
ops = 10;
// graph.createIndex(vertexKey, Vertex.class);
// graph.createIndex(edgeKey, Edge.class);

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