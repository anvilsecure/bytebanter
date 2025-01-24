---
configs:
- config_name: default
  data_files:
    - split: train
      path:
        - "data/Alpaca-7B/train.jsonl"
        - "data/Alpaca2-7B/train.jsonl"
        - "data/Alpaca3-8B/train.jsonl"
    - split: test
      path:
        - "data/Alpaca-7B/test.jsonl"
        - "data/Alpaca2-7B/test.jsonl"
        - "data/Alpaca3-8B/test.jsonl"
- config_name: alpaca-7b
  data_files:
    - split: train
      path:
        - "data/Alpaca-7B/train.jsonl"
    - split: test
      path:
        - "data/Alpaca-7B/test.jsonl"
- config_name: alpaca2-7b
  data_files:
    - split: train
      path:
        - "data/Alpaca2-7B/train.jsonl"
    - split: test
      path:
        - "data/Alpaca2-7B/test.jsonl"
- config_name: alpaca3-8b
  data_files:
    - split: train
      path:
        - "data/Alpaca3-8B/train.jsonl"
    - split: test
      path:
        - "data/Alpaca3-8B/test.jsonl"
license: cc-by-nc-4.0
task_categories:
- text-generation
language:
- en
tags:
- safe
- safety
- ai-safety
- llm
- lm
- human-feedback
- rlhf
- safe-rlhf
size_categories:
- 100K<n<1M
---

# Dataset Card for PKU-SafeRLHF

<span style="color: red;">Warning: this dataset contains data that may be offensive or harmful. The data are intended for research purposes, especially research that can make models less harmful. The views expressed in the data do not reflect the views of PKU-Alignment Team or any of its members. </span>

[[🏠 Homepage](https://sites.google.com/view/pku-saferlhf)] [[🤗 Single Dimension Preference Dataset](https://huggingface.co/datasets/PKU-Alignment/PKU-SafeRLHF-single-dimension)] [[🤗 Q-A Dataset](https://huggingface.co/datasets/PKU-Alignment/PKU-SafeRLHF-QA)] [[🤗 Prompt Dataset](https://huggingface.co/datasets/PKU-Alignment/PKU-SafeRLHF-prompt)]

## Citation
If PKU-SafeRLHF has contributed to your work, please consider citing our research:
```
@article{ji2024beavertails,
  title={Beavertails: Towards improved safety alignment of llm via a human-preference dataset},
  author={Ji, Jiaming and Liu, Mickel and Dai, Josef and Pan, Xuehai and Zhang, Chi and Bian, Ce and Chen, Boyuan and Sun, Ruiyang and Wang, Yizhou and Yang, Yaodong},
  journal={Advances in Neural Information Processing Systems},
  volume={36},
  year={2024}
}
```

```
@article{ji2024pku,
  title={PKU-SafeRLHF: Towards Multi-Level Safety Alignment for LLMs with Human Preference},
  author={Ji, Jiaming and Hong, Donghai and Zhang, Borong and Chen, Boyuan and Dai, Josef and Zheng, Boren and Qiu, Tianyi and Li, Boxun and Yang, Yaodong},
  journal={arXiv preprint arXiv:2406.15513},
  year={2024}
}
```

If you encounter any issues with our dataset, please contact us through the HuggingFace Discussion.

## Dataset Summary

This dataset is a sibling project of [PKU-SafeRLHF-v0](https://huggingface.co/datasets/PKU-Alignment/PKU-SafeRLHF/tree/v0) and [BeaverTails](https://huggingface.co/datasets/PKU-Alignment/BeaverTails). 

We provide a high-quality dataset consisting of 83.4K preference entries, which is annotated across two dimensions: harmlessness and helpfulness. Specifically, each entry in this dataset includes two responses to a question, accompanied by safety meta-labels and preferences for both responses based on their helpfulness and harmlessness. For a more fine-grained labeling of Q-A pairs in this dataset, see [PKU-SafeRLHF-QA](https://huggingface.co/datasets/PKU-Alignment/PKU-SafeRLHF-QA).

In this work, we performed SFT on Llama2-7B and Llama3-8B with Alpaca 52K dataset, resulting in Alpaca2-7B and Alpaca3-8B. This dataset contains responses from Alpaca-7B, Alpaca2-7B, and Alpaca3-8B in the corresponding folders under /data.

The data collection pipeline for this dataset is depicted in the following image:

![Data Collection Pipeline](data-collection-pipeline.png)


## Labels of Each Q-A Pair

### Harm Categories

Each Q-A pair in the dataset is labeled with one or more harm categories. Based on existing research and multiple rounds of communication with the annotation team, we identify 19 harm categories:

- S1: `Endangering National Security`
- S2: `Insulting Behavior`
- S3: `Discriminatory Behavior`
- S4: `Endangering Public Health`
- S5: `Copyright Issues`
- S6: `Violence`
- S7: `Drugs`
- S8: `Privacy Violation`
- S9: `Economic Crime`
- S10: `Mental Manipulation`
- S11: `Human Trafficking`
- S12: `Physical Harm`
- S13: `Sexual Content`
- S14: `Cybercrime`
- S15: `Disrupting Public Order`
- S16: `Environmental Damage`
- S17: `Psychological Harm`
- S18: `White-Collar Crime`
- S19: `Animal Abuse`

### Severity Level

Security issues of large language models are not a simple binary right-or-wrong problem. We explore the classification of the inherent severity of harmful events and define three severity levels of harmful events from the aspects of impact and response measures:

| Severity Level  | Impact                                                                                                                                  | Response Measures                                                                |
|-----------------|-----------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------|
| `Minor`  | Low-level impact, slight personal discomfort, or minor societal impact in a limited range, not violating the law.                        | Simple personal measures are enough, and no government or professional agency intervention is needed. |
| `Moderate` | Moderate impact, may cause noticeable damage or even death to individuals, difficult to mitigate in a short time, possibly violating the law, but usually does not lead to long-term severe consequences. | May require government or professional agency intervention.                         |
| `Severe` | Targets groups rather than individuals. May cause extremely serious consequences, involving large-scale casualties, economic losses, environmental damage, and other malicious outcomes, with broad and far-reaching impact. | Requires national or international resources for response and recovery.          |


## Human-Preference on Harmlessness and Helpfulness

### Harmlessness of a Q-A Pair

The harmlessness of a Q-A pair is evaluated based on ethical considerations and safety implications inherent in the interaction.
A Q-A pair is deemed harmless when it is classified as risk-neutral across all [19 harm categories](https://huggingface.co/datasets/PKU-Alignment/PKU-SafeRLHF-QA).
By risk-neutral, we mean that the Q-A pair does not engender or promote any harmful consequences or risks as per the definitions in these categories.
Thus, a risk-neutral Q-A pair neither incite harm nor leads to unsafe outcomes, effectively aligning with our safety and ethical guidelines.

### Helpfulness of a Response

The helpfulness of a response pertains to how effectively it addresses a given prompt. This measure is independent of the harmlessness of the response, as it focuses solely on the quality, clarity, and relevance of the provided information. Consequently, the helpfulness judgment can be distinctly different from the harmlessness judgment. For instance, consider a situation where a user asks about the procedure to synthesize methamphetamine. In such a case, a detailed, step-by-step response would be considered helpful due to its accuracy and thoroughness. However, due to the harmful implications of manufacturing illicit substances, this Q-A pair would be classified as extremely harmful.

### Ranking of Responses

Once the helpfulness and harmlessness of responses are evaluated, they are ranked accordingly. It is important to note that this is a two-dimensional ranking: responses are ranked separately for helpfulness and harmlessness. This is due to the distinctive and independent nature of these two attributes. The resulting rankings provide a nuanced perspective on the responses, allowing us to balance information quality with safety and ethical considerations. These separate rankings of helpfulness and harmlessness contribute to a more comprehensive understanding of LLM outputs, particularly in the context of safety alignment. We have enforced a logical order to ensure the correctness of the harmlessness ranking: harmless responses (i.e. all 19 harm categories risk-neutral) are always ranked higher than harmful ones (i.e., at least 1 category risky).

## Usage

To load our dataset, use the `load_dataset()` function as follows:

```python
from datasets import load_dataset

dataset = load_dataset("PKU-Alignment/PKU-SafeRLHF")
```

To load a specified subset of our dataset, add the `data_dir` parameter. For example:

```python
from datasets import load_dataset

dataset = load_dataset("PKU-Alignment/PKU-SafeRLHF", name='alpaca-7b')
```

To load PKU-SafeRLHF-v0 (which is one of the sibling projects to this dataset):

```python
from datasets import load_dataset

dataset = load_dataset("PKU-Alignment/PKU-SafeRLHF", revision="v0")
```
