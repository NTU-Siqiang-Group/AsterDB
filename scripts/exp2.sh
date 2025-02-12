#!/bin/bash
# datasets=('twitch.json3')
# datasets=('com-orkut.ungraph.json3')
datasets=('wikipedia.json3' 'com-orkut.ungraph.json3')
ratios=(0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9)
# ratios=(0.9)
# ratios=(0.4 0.5 0.6)
# updates=(1 0 3)
updates=(3)

for dataset in "${datasets[@]}" 
do
  for update in "${updates[@]}"
  do
    datset_path=/home/junfeng/db_backup/${dataset}_0
    echo $datset_path
    warmup_path=/tmp/warmup_${dataset}_${update}
    if [[ ! -d $warmup_path && $update != 2 ]]; then
      echo "Need to warm up: ${warmup_path}..."
      rm -rf /tmp/demo
      cp -r $datset_path /tmp/demo
      cp warmup.groovy tmp.groovy
      sed -i "s/conf.setProperty(\"updatePolicy\", 2)/conf.setProperty(\"updatePolicy\", $update)/g" tmp.groovy
      bin/gremlin.sh -e tmp.groovy
      cp -r /tmp/demo $warmup_path
    fi
    if [[ $update == 2 ]]; then
      datset_path=/home/junfeng/db_backup/${dataset}_0
      warmup_path=$datset_path
    fi
    for ratio in "${ratios[@]}"
    do
      rm -rf /tmp/demo
      cp -r $warmup_path /tmp/demo
      cp exp2.groovy tmp.groovy
      sed -i "s/rratio = 0.1/rratio = $ratio/g" tmp.groovy
      sed -i "s/conf.setProperty(\"updatePolicy\", 2)/conf.setProperty(\"updatePolicy\", $update)/g" tmp.groovy
      bin/gremlin.sh -e tmp.groovy
      rm tmp.groovy
    done
    echo "---------------"
  done
done