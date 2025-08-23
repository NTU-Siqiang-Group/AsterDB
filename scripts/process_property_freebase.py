import json
datapath = '../graph-baselines/runtime/data/freebase_large.json2'

vertex_path = 'dataset/freebase_large.json2.vertex'
edge_path = 'dataset/freebase_large.json2.edge'


with open(datapath, 'r') as f:
  content = f.read()
  graph = json.loads(content)
  vertex_cnt = 0
  with open(vertex_path, 'w+') as f1:
    for vertex in graph["vertices"]:
      s = f'{vertex["_id"]} freebaseid:{vertex["freebaseid"]["value"]}\n'
      f1.write(s)
      vertex_cnt += 1

      if vertex_cnt % 10000 == 0:
        print(f'processed vertices: {vertex_cnt}')
  
  edge_cnt = 0
  with open(edge_path, 'w+') as f1:
    for edge in graph["edges"]:
      s = f'{edge["_outV"]} {edge["_inV"]} label:{edge["_label"]}\n'
      f1.write(s)
      edge_cnt += 1

      if edge_cnt % 10000 == 0:
        print(f'processed edge: {edge_cnt}')