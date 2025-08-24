#!/bin/bash
# datasets=('twitch.json3')
# datasets=('com-orkut.ungraph.json3')
datasets=('com-dblp.ungraph.json3')

export LD_PRELOAD=/usr/local/lib/libjemalloc.so

cp scripts/inmemory.groovy tmp.groovy

for ds in "${datasets[@]}"
do
  rm -rf /tmp/demo
  ./bulkload --dataset=../graph-baselines/runtime/data/$ds --is_undirected=1
  bin/gremlin.sh -e tmp.groovy
done