/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tinkerpop;

import org.apache.commons.io.FileUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.rocksdb.*;


import java.io.BufferedReader;
import java.io.File;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Measurement(iterations = 1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1)
@Warmup(iterations = 0)
public class RocksDBBenchmark {
    final int keySize = 24;
    final int valueSize = 1000;
    final int opNum = 5000000;
    String getKey(int i) {
        return String.format("%0" + keySize + "d", i);
    }
    String getValue(int i) {
        return String.format("%0" + valueSize + "d", i);
    }
    @Benchmark
    @OperationsPerInvocation(opNum)
    public void testRocksDB(Blackhole blackhole) {
        RocksDB.loadLibrary();
        RocksDB db;
        String path = "/tmp/db";
        org.rocksdb.Options options = new org.rocksdb.Options();

        try {
            FileUtils.deleteDirectory(new File(path));
            options.setCreateIfMissing(true);
            options.setStatistics(new Statistics());
            db = RocksDB.open(options, path);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        for (int i = 0; i < opNum; i++) {
            String key = getKey(i);
            String value = getValue(i);
            try {
                db.put(key.getBytes(), value.getBytes());
            } catch (RocksDBException e) {
                e.printStackTrace();
                return;
            }
        }
        System.out.println(options.statistics().toString());
        db.close();
    }
    @Benchmark
    @OperationsPerInvocation(opNum)
    public void testRocksDBGet(Blackhole blackhole) {
        RocksDB.loadLibrary();
        RocksDB db;
        String path = "/tmp/db";
        org.rocksdb.Options options = new org.rocksdb.Options();
        try {
            FileUtils.deleteDirectory(new File(path));
            FileUtils.copyDirectory(new File("/tmp/db1"), new File("/tmp/db"));
            options.setCreateIfMissing(false);
            options.setStatistics(new Statistics());
            db = RocksDB.open(options, path);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try (BufferedReader br = new BufferedReader(new java.io.FileReader("/home/junfeng/Desktop/rand.txt"))) {
            String line = br.readLine();
            while (line != null) {
//                System.out.println(Integer.valueOf(line));
                String key = getKey(Integer.parseInt(line));
                byte[] value = db.get(key.getBytes());
//                String valStr = new String(value);
//                System.out.println(key);
//                System.out.println(valStr);
                blackhole.consume(value);
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
            return;
        }
        System.out.println(options.statistics().toString());
        db.close();
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(RocksDBBenchmark.class.getSimpleName())
                .result("result.json")
                .resultFormat(ResultFormatType.JSON).build();
        new Runner(opt).run();
    }
}
