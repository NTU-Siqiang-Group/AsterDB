#!/bin/bash
datasets=('com-dblp.ungraph.json3')
vnum=3072627
algms=('basics.groovy')

export LD_PRELOAD="$(
  ( /sbin/ldconfig -p 2>/dev/null || /usr/sbin/ldconfig -p 2>/dev/null || ldconfig -p ) \
  | awk '/libjemalloc\.so(\.|$)/{print $4; exit}'
)"

for ds in "${datasets[@]}"
do
  if [[ $ds == *dblp.ungraph.json3 ]]; then
    vnum=425957
    undirect=1
  elif [[ $ds == *orkut.ungraph.json3 ]]; then
    vnum=3072627
    undirect=1
  elif [[ $ds == *twitter-2010.json3 ]]; then
    vnum=41652230
    undirect=0
  elif [[ $ds == *twitch.json3 ]]; then
    vnum=168115
    undirect=0
  fi
  for algm in "${algms[@]}"
  do
    rm -rf /tmp/demo
    ./bulkload --dataset=../graph-baselines/runtime/data/$ds --is_undirected=$undirect
    bin/gremlin.sh -e scripts/basics.groovy
  done
done