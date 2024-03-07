package org.apache.tinkerpop;

import org.apache.tinkerpop.gremlin.language.grammar.GremlinAntlrToJava;
import org.apache.tinkerpop.gremlin.language.grammar.VariableResolver;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.apache.tinkerpop.gremlin.language.grammar.GremlinQueryParser;
import java.util.concurrent.TimeUnit;

// @BenchmarkMode(Mode.AverageTime)
// @Measurement(iterations = 5)
// @State(value = Scope.Benchmark)
// @OutputTimeUnit(TimeUnit.MICROSECONDS)
// @Fork(value = 1)
// @Warmup(iterations = 0)
// public class GremlinParserBenchmark {
//     private static final GraphTraversalSource g = EmptyGraph.instance().traversal();
//     private final GremlinAntlrToJava gremlinAntlrToJava = new GremlinAntlrToJava(g,
//             VariableResolver.NullVariableResolver.instance());
//     @Benchmark
//     public void simpleParserBenchmark(Blackhole blackhole) {
//         GremlinQueryParser.parse("g.V(100)", gremlinAntlrToJava);
//     }
//     @Benchmark
//     public void mediumParserBenchmark(Blackhole blackhole) {
//         GremlinQueryParser.parse("g.V().out().out().path().by('name').by('age')", gremlinAntlrToJava);
//     }

//     @Benchmark
//     public void complexParserBenchmark(Blackhole blackhole) {
//         GremlinQueryParser.parse("g.V().hasLabel('software').as('a','b','c').\n" +
//                 "            select('a','b','c').\n" +
//                 "              by('name').\n" +
//                 "              by('lang').\n" +
//                 "              by(__.in('created').values('name').fold())", gremlinAntlrToJava);
//     }
//     public static void main(String[] args) throws Exception {
//         Options opt = new OptionsBuilder()
//                 .include(GremlinParserBenchmark.class.getSimpleName())
//                 .result("result.json")
//                 .resultFormat(ResultFormatType.JSON).build();
//         new Runner(opt).run();
//     }
// }
