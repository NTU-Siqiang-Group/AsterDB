#!/bin/bash
#!/bin/bash
declare -A bfs_start_vertex
declare -A sssp_start_vertex
declare -A sssp_dst_vertex
declare -A data_meta
trap '' SIGSEGV
export LD_PRELOAD=/usr/local/lib/libjemalloc.so

# cit-patents.json3' wikitalk.json3

data_meta[cit-patents.json3]=3774768
bfs_start_vertex[cit-patents.json3]=3494505
sssp_start_vertex[cit-patents.json3]=3494505
sssp_dst_vertex[cit-patents.json3]=754148

# bfs_start_vertex[wikitalk.json3]=6765
# sssp_start_vertex[wikitalk.json3]=32822
# sssp_dst_vertex[wikitalk.json3]=33
# data_meta[wikitalk.json3]=2394386

ALGMS=(pr.groovy cdlp.groovy wcc.groovy shortest-path.groovy bfs.groovy)
ALGMS=(wcc.groovy)

for key in "${!data_meta[@]}"; do
  rm -rf /tmp/demo
  ./bulkload --dataset=../graph-baselines/runtime/data/$key
  for algm in "${ALGMS[@]}"; do
    echo "start running $algm on $key..."
    cp scripts/$algm tmp.groovy
    sed -i "s/GRAPH_VERTEX_NUM/${data_meta[$key]}/g" tmp.groovy
    if [[ $algm == bfs.groovy ]]; then
      sed -i "s/BFS_SOURCE/${bfs_start_vertex[$key]}/g" tmp.groovy
    elif [[ $algm == shortest-path.groovy ]]; then
      sed -i "s/SSSP_SOURCE/${sssp_start_vertex[$key]}/g" tmp.groovy
      sed -i "s/SSSP_DST/${sssp_dst_vertex[$key]}/g" tmp.groovy
    fi
    bin/gremlin.sh -e tmp.groovy
    rm tmp.groovy
    echo "----------------"
  done  
done
