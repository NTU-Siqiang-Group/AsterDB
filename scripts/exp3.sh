#!/bin/bash
# datasets=('wikipedia.json3' 'com-orkut.ungraph.json3' 'twitch.json3')
datasets=('twitter-2010.json3')
# datasets=('com-dblp.ungraph.json3')
# algms=('ppr.groovy' 'random-walk.groovy')
algms=('ppr.groovy')

for ds in "${datasets[@]}"
do
  echo "testing $ds...."
  rm -rf /tmp/demo
  cp -r /home/junfeng/db_backup/${ds}_0 /tmp/demo
  for algm in "${algms[@]}"
  do
    bin/gremlin.sh -e $algm
  done
  echo "----------------------"
done