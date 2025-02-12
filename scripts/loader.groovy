dataset = "/home/junfeng/graph-baselines/runtime/data/dataset_placeholder";

System.out.println("preparing to load: ${dataset}");

total_vertex_num = 425957;
is_undirected = true;

if (dataset.contains("twitter")) {
  total_vertex_num = 41652230;
  is_undirected = false;
} else if (dataset.contains("orkut")) {
  total_vertex_num = 3072627;
} else if (dataset.contains("twitch")) {
  total_vertex_num = 168115;
  is_undirected = true;
} else if (dataset.contains('pokec')) {
  total_vertex_num = 1632804;
  is_undirected = false;
} else if (dataset.contains('journal')) {
  total_vertex_num = 4847572;
  is_undirected = false;
} else if (dataset.contains('dbpedia')) {
  total_vertex_num = 18268993;
  is_undirected = false;
} else if (dataset.contains('wikipedia')) {
  total_vertex_num = 3333398;
  is_undirected = false;
}

def loadTxtGraph(filename, vertexNum, isUndirected, graphToWrite) {
    nsToS = 1000000000;
    System.out.println("loading txt: ${filename}");
    def vs = [];
    t1 = System.nanoTime();
    totalVertices = vertexNum;
    for (int i = 0; i < totalVertices; i++) {
        vs.add(graphToWrite.addVertex("vertex") as Vertex);
        if (i % 1000000 == 0) {
            t2 = System.nanoTime();
            System.out.println("loaded ${i} vertices, spend time: ${(t2 - t1) / nsToS} s");
        }
    }
    System.out.println("loaded all vertices, spend time: ${(t2 - t1) / nsToS} s");
    is = new FileReader(filename);
    reader = new BufferedReader(is);
    idx = 0;
    try {
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] evs = line.split(" ");
            idx1 = evs[0].toInteger();
            idx2 = evs[1].toInteger();
            vs[idx1].addEdge("edg", vs[idx2]);
            if (isUndirected) {
                vs[idx2].addEdge("edg", vs[idx1]);
            }
            idx += 1;
            if (idx % 1000000 == 0) {
                t2 = System.nanoTime();
                System.out.println("loaded ${idx} edges, spend time: ${(t2 - t1) / nsToS} s");
            }
        }
        t2 = System.nanoTime();
        System.out.println("loaded all edges, spend time: ${(t2 - t1) / nsToS} s")
    } finally {
        is.close();
    }
}

conf = new BaseConfiguration();
conf.setProperty("updatePolicy", 3);
graph = TinkerGraph.open(conf);
graph.setWorkload(1); // 100% read

loadTxtGraph(dataset, total_vertex_num, is_undirected, graph);


// try to get the total number of vertices and edges
g = graph.traversal();
System.out.println("Stats: a total of ${g.V().count().next()} vertices");
total_edges = 0;
ids = g.V().id().fold().next();
for (int i = 0; i < ids.size(); i++) {
  total_edges += g.V(ids[i]).outE().count().next();
}
System.out.println("Stats: a total of ${total_edges} edges");
graph.closeDB();