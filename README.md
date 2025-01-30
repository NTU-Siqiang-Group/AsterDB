<!---
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--->

AsterDB
===

<!-- A logo of Aster here --->

[![maven-central](https://img.shields.io/maven-central/v/org.apache.tinkerpop/gremlin-driver?color=brightgreen)]("https://mvnrepository.com/artifact/org.apache.tinkerpop/gremlin-driver")[![nuget](https://img.shields.io/nuget/v/Gremlin.Net?color=brightgreen)](link="https://www.nuget.org/packages/Gremlin.Net")[![pypi](https://img.shields.io/pypi/v/gremlinpython?color=brightgreen)]("https://pypi.org/project/gremlinpython/")[![npm](https://img.shields.io/npm/v/gremlin?color=brightgreen)]("https://www.npmjs.com/package/gremlin")[![go module](https://badge.fury.io/go/github.com%2Fapache%2Ftinkerpop%2Fgremlin-go%2Fv3.svg)]("https://pkg.go.dev/github.com/apache/tinkerpop/gremlin-go/v3")[![codecov](https://codecov.io/gh/apache/tinkerpop/branch/master/graph/badge.svg?token=TojD2nR5Qd)]("https://codecov.io/gh/apache/tinkerpop")

AsterDB is a robust and versatile graph database that supports Gremlin query language facilitating various graph applications.

AsterDB provides two high-level features:

- AsterDB utilizes a hybrid mechanism to handle edge insertions and deletions with optimized I/O efficiency..
- AsterDB exploits the skewness of graph data to encode the key-value entries concisely and effectivel.

------------------

- [Installation](#installation)
  - [Build AsterDB from Scratch](#build-asterdb-from-scratch)
    - [Install Dependencies](#install-dependencies)
    - [Build AsterDB from Source](#build-asterdb-from-source)
  - [Docker Image](#docker-image)
    - [Use Pre-built Images](#use-pre-built-images)
    - [Build the Image Yourself](#build-the-image-yourself)
- [Getting Started](#getting-started)
  - [Load Graph Dataset into AsterDB](#load-graph-dataset-into-asterdb)
  - [A Toy Example to Try AsterDB](#a-toy-example-to-try-asterdb)
  - [Deploy AsterDB in Docker Container](#deploy-asterdb-in-docker-container)

# Installation

## Build AsterDB from Scratch

### Install Dependencies

```bash
export USER_HOME_PATH=/path/to/user/directory

# install g++-10, make, libboost-all-dev
sudo apt-get update
sudo apt-get install g++-10 make libboost-all-dev -y
ln -s /usr/bin/g++-10 /usr/bin/g++

# install openjdk-11
sudo apt-get install openjdk-11-jdk
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

# install maven
mkdir /usr/local/maven/
mkdir $USER_HOME_PATH/.m2/ && mkdir $USER_HOME_PATH/.m2/repository
wget https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz
tar -xvzf apache-maven-3.9.9-bin.tar.gz -C /usr/local/maven
```

<!-- > Tip: For Chinese mainland user: 

```bash
echo "<settings> \
        <mirrors> \
            <mirror> \
                <id>aliyun</id> \
                <mirrorOf>central</mirrorOf> \
                <name>Aliyun Maven</name> \
                <url>https://maven.aliyun.com/repository/public</url> \
            </mirror> \
        </mirrors> \
</settings>" >> /root/.m2/settings.xml
``` -->

### Build AsterDB from Source

Clone this repo:

```bash
cd $USER_HOME_PATH
git clone --recurse-submodules https://github.com/NTU-Database-Group/AsterDB.git
```

Build LSM-Tree based graph storage engine for AsterDB:

```bash
cd $USER_HOME_PATH/AsterDB/GraphKV
sudo make -j8 install-static DEBUG_LEVEL=0 DISABLE_WARNING_AS_ERROR=1 EXTRA_CXXFLAGS=-fPIC
sudo make -j8 install-shared DEBUG_LEVEL=0 DISABLE_WARNING_AS_ERROR=1 EXTRA_CXXFLAGS=-fPIC
sudo make -j8 rocksdbjava DEBUG_LEVEL=0 DISABLE_WARNING_AS_ERROR=1 EXTRA_CXXFLAGS=-fPIC
```

Build gremlin console for AsterDB:

```bash
cd $USER_HOME_PATH/AsterDB/
mvn install:install-file \
    -Dfile=GraphKV/java/target/rocksdbjni-8.9.0-linux64.jar \
    -DgroupId=org.rocksdb \
    -DartifactId=rocksdbjni \
    -Dversion=8.9.0 \
    -Dpackaging=jar \
    -DlocalRepositoryPath=/root/.m2/repository
mvn clean install -pl tinkergraph-gremlin,gremlin-console -Dmaven.test.skip=true
```

## Docker Image

### Use Pre-built Images

TODO: upload pre-build image to docker hub

### Build the Image Yourself

Clone this repo:

```bash
cd $USER_HOME_PATH
git clone --recurse-submodules https://github.com/NTU-Database-Group/AsterDB.git
```

Build docker image:

```bash
docker build -t asterdb:v1.0 .
```

# Getting Started

## Load Graph Dataset into AsterDB 

Prepare your dataset for testing, e.g.

```bash
# cur dir: $USER_HOME_PATH/AsterDB/
mkdir datasets && cd datasets
wget https://snap.stanford.edu/data/bigdata/communities/com-orkut.ungraph.txt.gz
gzip -d com-orkut.ungraph.txt.gz
# delete graph info lines if necessary
sed '1,4d' com-orkut.ungraph.txt > temp_file && mv temp_file com-orkut.ungraph.txt
```

> tips: For each row in graph dataset file, the format should be: `<source_id> <dest_id>`

Load dataset into AsterDB instance:

```bash
# cur dir: $USER_HOME_PATH/AsterDB/
g++ ./GraphKV/tools/bulkload.cc -O3 -std=c++17 -lrocksdb -lgflags -o ./bin/bulkload
export WRITE_SST_PATH=/udf/sst/file/path
./bin/bulkload -dataset=./datasets/com-orkut.ungraph.txt -is_undirected=true -write_sst_path=$WRITE_SST_PATH
```

## A Toy Example to Try AsterDB

Try AsterDB with gremlin console in a interactive way:

```bash
bin/gremlin.sh

         \,,,/
         (o o)
-----oOOo-(3)-oOOo-----
plugin activated: tinkerpop.server
plugin activated: tinkerpop.utilities
plugin activated: tinkerpop.tinkergraph
gremlin> conf = new BaseConfiguration();
==>org.apache.commons.configuration2.BaseConfiguration@52454457
gremlin> conf.setProperty("updatePolicy", 2); # adaptive
==>null
gremlin> graph = TinkerGraph.open(conf);
using update policy: 2
==>tinkergraph[vertices:12290508 edges:937480664]
gremlin> g = graph.traversal();
==>graphtraversalsource[tinkergraph[vertices:12290508 edges:937480664], standard]
gremlin> g.V().id().fold().next();
...
```

You can also implement your own graph algorithm with groovy scripts, here is an example:

```bash
# cur dir: $USER_HOME_PATH/AsterDB/
./bin/gremlin.sh -e ./scripts/bfs.groovy
```

## Deploy AsterDB in Docker Container

mount directory `/tmp/demo/` (db path) for data persistence:

```bash
docker run -it --name asterdb_container -v /tmp/demo:/tmp/demo asterdb:v1.0
```