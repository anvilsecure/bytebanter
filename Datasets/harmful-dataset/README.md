---
language:
- en
dataset_info:
  features:
  - name: prompt
    dtype: string
  - name: rejected
    dtype: string
  - name: chosen
    dtype: string
  splits:
  - name: train
    num_bytes: 4808598
    num_examples: 4948
  download_size: 2285687
  dataset_size: 4808598
configs:
- config_name: default
  data_files:
  - split: train
    path: data/train-*
---
