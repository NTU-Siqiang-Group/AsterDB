#!/bin/bash
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
cd GraphKV
sudo DEBUG_LEVEL=0 DISABLE_WARNING_AS_ERROR=1 JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 EXTRA_CXXFLAGS=-fPIC make -j10 install-static
sudo DEBUG_LEVEL=0 DISABLE_WARNING_AS_ERROR=1 JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 EXTRA_CXXFLAGS=-fPIC make -j10 install-shared
sudo DEBUG_LEVEL=0 DISABLE_WARNING_AS_ERROR=1 JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 EXTRA_CXXFLAGS=-fPIC make -j10 rocksdbjava

cd tools
g++ bulkload.cc -lrocksdb -lgflags -o ../../bulkload -std=c++17 -g

cd ../..
mvn install:install-file \
    -Dfile=GraphKV/java/target/rocksdbjni-8.9.0-linux64.jar -DgroupId=org.rocksdb -DartifactId=rocksdbjni -Dversion=8.9.0 \
    -Dpackaging=jar -DlocalRepositoryPath=$HOME/.m2/repository/
mvn clean install -pl tinkergraph-gremlin,gremlin-console -Dmaven.test.skip=true