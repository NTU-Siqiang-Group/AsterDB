#!/bin/bash
DATASET_ALIAS="${1:-null}"
resolve_dataset() {
  case "$1" in
    dblp)       echo "com-dblp.ungraph.json3" ;;
    wikipedia)
                echo "wikipedia.json3" ;;
    orkut)      echo "com-orkut.ungraph.json3" ;;
    twitter)
                echo "twitter-2010.json3" ;;
    *)
      echo "Unknown dataset alias: $1" >&2
      return 1
      ;;
  esac
}
dataset="$(resolve_dataset "$DATASET_ALIAS")"
datasets=($dataset)

vnum=3072627
algms=('get-and-add.groovy')

ratios=(0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9)
# ratios=(0.5)
total_ops=1000000

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
    for ratio in "${ratios[@]}"
    do
      rm -rf /tmp/demo
      ./bulkload --dataset=../graph-baselines/runtime/data/$ds --is_undirected=$undirect
      rop=$(python3 -c "print(int($total_ops*$ratio))")
      wop=$(python3 -c "print(int($total_ops*(1-$ratio)))")
      echo "rop: $rop, wop: $wop"
      cp scripts/$algm tmp.groovy
      sed -i "s/rops = 100000/rops = $rop/g" tmp.groovy
      sed -i "s/wops = 100000/wops = $wop/g" tmp.groovy
      bin/gremlin.sh -e tmp.groovy
      rm tmp.groovy
    done
  done
done