#!/bin/bash
mkdir dataset
python3 scripts/process_property_ldbc.py
python3 scripts/process_property_freebase.py
# datasets=(ldbc.json2 freebase_large.json2)

DATASET_ALIAS="${1:-null}"
resolve_dataset() {
  case "$1" in
    ldbc)       echo "ldbc.json2"      
    ;;
    freebase)
                echo "freebase_large.json2" 
                ;;
    *)
      echo "Unknown dataset alias: $1" >&2
      return 1
      ;;
  esac
}
dataset="$(resolve_dataset "$DATASET_ALIAS")"
datasets=($dataset)

export LD_PRELOAD="$(
  ( /sbin/ldconfig -p 2>/dev/null || /usr/sbin/ldconfig -p 2>/dev/null || ldconfig -p ) \
  | awk '/libjemalloc\.so(\.|$)/{print $4; exit}'
)"

for ds in "${datasets[@]}"
do
  echo $ds
  rm -rf /tmp/demo
  path=$(pwd)/dataset/$ds
  cp scripts/load_with_properties.groovy ./tmp.groovy
  sed -i "s|dataset = PATH_TO_DATASET|dataset='$path'|g" tmp.groovy
  bin/gremlin.sh -e tmp.groovy
  rm tmp.groovy
done