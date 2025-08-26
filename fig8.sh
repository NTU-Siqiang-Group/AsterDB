#!/bin/bash
# datasets=('twitch.json3')
# datasets=('com-orkut.ungraph.json3')
# datasets=('com-dblp.ungraph.json3')
datasets=('wikipedia.json3' 'com-orkut.ungraph.json3')
ratios=(0.5)
ratios=(0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9)
# updates=(0)
updates=(0 1 2)

export LD_PRELOAD="$(
  ( /sbin/ldconfig -p 2>/dev/null || /usr/sbin/ldconfig -p 2>/dev/null || ldconfig -p ) \
  | awk '/libjemalloc\.so(\.|$)/{print $4; exit}'
)"

for ds in "${datasets[@]}" 
do
  if [[ $ds == *orkut.ungraph.json3 ]]; then
    undirect=1
  else
    undirect=0
  fi
  for update in "${updates[@]}"
  do
    if [[ $update != 2 ]]; then
      rm -rf /tmp/demo
      ./bulkload --dataset=../graph-baselines/runtime/data/$ds --is_undirected=$undirect
      cp warmup.groovy tmp.groovy
      sed -i "s/conf.setProperty(\"updatePolicy\", 2)/conf.setProperty(\"updatePolicy\", $update)/g" tmp.groovy
      bin/gremlin.sh -e tmp.groovy
      cp -r /tmp/demo /tmp/warmup
    fi
    for ratio in "${ratios[@]}"
    do
      rm -rf /tmp/demo
      if [[ $update == 2 ]]; then
        # adaptive
        echo "warming up for adaptive update..."
        ./bulkload --dataset=../graph-baselines/runtime/data/$ds --is_undirected=$undirect
        cp scripts/exp2.groovy tmp.groovy
        sed -i "s/rratio = 0.1/rratio = $ratio/g" tmp.groovy
        sed -i "s/isWarmup = false/isWarmup = true/g" tmp.groovy
        sed -i "s/conf.setProperty(\"updatePolicy\", 2)/conf.setProperty(\"updatePolicy\", $update)/g" tmp.groovy
        bin/gremlin.sh -e tmp.groovy
        cp -r /tmp/demo /tmp/warmup
        rm -rf /tmp/demo
      fi
      cp -r /tmp/warmup /tmp/demo
      cp scripts/exp2.groovy tmp.groovy
      sed -i "s/rratio = 0.1/rratio = $ratio/g" tmp.groovy
      sed -i "s/conf.setProperty(\"updatePolicy\", 2)/conf.setProperty(\"updatePolicy\", $update)/g" tmp.groovy
      bin/gremlin.sh -e tmp.groovy
      rm tmp.groovy
    done
    echo "---------------"
  done
done