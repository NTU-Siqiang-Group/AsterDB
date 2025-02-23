conf = new BaseConfiguration();
conf.setProperty("updatePolicy", 2); // adaptive
graph = TinkerGraph.open(conf);
g = graph.traversal();

// query before openning transaction
println("=== query before openning transaction ===");
println("number of vertices: " + g.V().count().next())
println("number of edges: " + g.E().count().next())

graph.tx().open()
println("=== trasaction open ===");

// write vertex
alice = g.addV('person').property('name', 'Alice').property('age', 30).next()
bob = g.addV('person').property('name', 'Bob').property('age', 35).next()

// write edge
alice.addEdge('knows', bob, 'since', 2020)

// commit transaction
graph.tx().commit()
println("=== transaction has been committed ===")

println("=== query after committing transaction ===");
println("number of vertices: " + g.V().count().next())
println("number of edges: " + g.E().count().next())

graph.close()
