#!/bin/bash
# datasets=('twitch.json3')
# datasets=('com-orkut.ungraph.json3')
datasets=('wikitalk.json3' 'com-dblp.ungraph.json3' )

export LD_PRELOAD="$(
  ( /sbin/ldconfig -p 2>/dev/null || /usr/sbin/ldconfig -p 2>/dev/null || ldconfig -p ) \
  | awk '/libjemalloc\.so(\.|$)/{print $4; exit}'
)"

cp scripts/inmemory.groovy tmp.groovy

for ds in "${datasets[@]}"
do
  echo $ds
  rm -rf /tmp/demo
  ./bulkload --dataset=../graph-baselines/runtime/data/$ds --is_undirected=1
  bin/gremlin.sh -e tmp.groovy
  echo "---------------------"
done