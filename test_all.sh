#!/bin/bash
# datasets=('com-orkut.ungraph.json3')
# datasets=('twitter-2010.json3')
# datasets=('wikipedia.json3' 'com-orkut.ungraph.json3')
datasets=('com-dblp.ungraph.json3')
vnum=3072627
algms=('get-and-add.groovy')

ratios=(0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9)
# ratios=(0.9)
total_ops=2000000

for ds in "${datasets[@]}"
do
  if [[ $ds == *dblp.ungraph.json3 ]]; then
    vnum=425957
  elif [[ $ds == *orkut.ungraph.json3 ]]; then
    vnum=3072627
  elif [[ $ds == *twitter-2010.json3 ]]; then
    vnum=41652230
  elif [[ $ds == *twitch.json3 ]]; then
    vnum=168115
  fi
  echo "testing ${ds}...."
  echo /home/junfeng/db_backup/${ds}_0
  for algm in "${algms[@]}"
  do
    # echo "    testing $algm..."
    if [[ $algm == get-and-add.groovy ]]; then
      for ratio in "${ratios[@]}"
      do
        rm -rf /tmp/demo
        cp -r /home/junfeng/db_backup/${ds}_0 /tmp/demo
        rop=$(python3 -c "print(int($total_ops*$ratio))")
        wop=$(python3 -c "print(int($total_ops*(1-$ratio)))")
        echo "rop: $rop, wop: $wop"
        cp $algm tmp.groovy
        sed -i "s/graph.currentVertexId = 3072627/graph.currentVertexId = ${vnum}/g" tmp.groovy
        sed -i "s/rops = 100000/rops = $rop/g" tmp.groovy
        sed -i "s/wops = 100000/wops = $wop/g" tmp.groovy
        bin/gremlin.sh -e tmp.groovy
        rm tmp.groovy
        # echo "--------------------"
      done
    fi
  done
done