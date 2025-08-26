#!/bin/bash
# python3 scripts/process_property_dataset.py
# python3 scripts/process_property_freebase.py

# datasets=(ldbc.json2 freebase_large.json2)
datasets=(ldbc.json2)

export LD_PRELOAD="$(
  ( /sbin/ldconfig -p 2>/dev/null || /usr/sbin/ldconfig -p 2>/dev/null || ldconfig -p ) \
  | awk '/libjemalloc\.so(\.|$)/{print $4; exit}'
)"

for ds in "${datasets[@]}"
do
  rm -rf /tmp/demo
  path=$HOME/AsterDB/dataset/$ds
  cp scripts/load_with_properties.groovy ./tmp.groovy
  sed -i "s|dataset = PATH_TO_DATASET|dataset='$path'|g" tmp.groovy
  bin/gremlin.sh -e tmp.groovy
  rm tmp.groovy
done