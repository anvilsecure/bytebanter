---
language:
- en
dataset_info:
  features:
  - name: prompt
    dtype: string
  splits:
  - name: train
    num_bytes: 7416
    num_examples: 100
  download_size: 5919
  dataset_size: 7416
configs:
- config_name: default
  data_files:
  - split: train
    path: data/train-*
license: mit
---


# TDC 2023 (LLM Edition) - Red Teaming Track

This is the combined [dev](https://github.com/centerforaisafety/tdc2023-starter-kit/blob/main/red_teaming/data/dev/behaviors.json) and [test](https://github.com/centerforaisafety/tdc2023-starter-kit/blob/main/red_teaming/data/test/behaviors.json) set from the [Red Teaming Track](https://github.com/centerforaisafety/tdc2023-starter-kit/tree/main/red_teaming) of TDC 2023.

### Citation
If find this dataset useful, please cite the following work:
```
@inproceedings{tdc2023,
  title={TDC 2023 (LLM Edition): The Trojan Detection Challenge},
  author={Mantas Mazeika and Andy Zou and Norman Mu and Long Phan and Zifan Wang and Chunru Yu and Adam Khoja and Fengqing Jiang and Aidan O'Gara and Ellie Sakhaee and Zhen Xiang and Arezoo Rajabi and Dan Hendrycks and Radha Poovendran and Bo Li and David Forsyth},
  booktitle={NeurIPS Competition Track},
  year={2023}
}
```

The dataset from the TDC 2023 Red Teaming Track has been expanded and published in [HarmBench](https://arxiv.org/abs/2402.04249). Some of the data in HarmBench overlaps with the TDC 2023 data. If this data is useful in your research, please consider citing HarmBench as well:
```
@article{mazeika2024harmbench,
  title={HarmBench: A Standardized Evaluation Framework for Automated Red Teaming and Robust Refusal},
  author={Mazeika, Mantas and Phan, Long and Yin, Xuwang and Zou, Andy and Wang, Zifan and Mu, Norman and Sakhaee, Elham and Li, Nathaniel and Basart, Steven and Li, Bo and others},
  journal={arXiv preprint arXiv:2402.04249},
  year={2024}
}
```