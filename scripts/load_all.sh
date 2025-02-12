#!/bin/bash
# datasets=('wikipedia.json3' 'com-orkut.ungraph.json3' 'twitter-2010.json3')
datasets=('com-dblp.ungraph.json3')
# datasets=('wikipedia.json3')


for ds in "${datasets[@]}"
do
  rm -rf /tmp/demo
  cp loader.groovy tmp.groovy
  sed -i "s/dataset_placeholder/$ds/g" tmp.groovy
  bin/gremlin.sh -e tmp.groovy
  # rm -rf /home/junfeng/db_backup/${ds}_3
  # mv /tmp/demo /home/junfeng/db_backup/${ds}_3
  rm tmp.groovy
done

