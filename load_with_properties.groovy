// dataset = '/home/junfeng/AsterDB/dataset/MY_DATASET'
// dataset = '/home/junfeng/AsterDB/dataset/freebase_large.json2'
dataset = '/home/junfeng/AsterDB/dataset/ldbc.json2'
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
  if (vertexCnt % 10000 == 0) {
    println("processed vertex ${vertexCnt}");
  }
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
  if (edgeCnt % 10000 == 0) {
    println("processed edge ${edgeCnt}");
  }
}
graph.createIndex(edgePropKey, Edge.class);

// println("${g.V().has('freebaseid', '484848485248').count().next()}");
// println("${g.E().has('label', '/american_football/football_coach/coaching_history').count().next()}");
println("${g.V().has('xlabel', 'post').count().next()}");
println("${g.E().has('label', 'hasCreator').count().next()}");

graph.closeDB();