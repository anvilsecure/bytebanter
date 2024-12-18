from pyrit.prompt_target import OpenAIChatTarget
from openai import AsyncOpenAI
from random import randint


class AnvilAIChatTarget(OpenAIChatTarget):
    def __init__(self):
        super().__init__(deployment_name="attacker",
                         endpoint="http://anvil-ai:1337/v1/", api_key="test",
                         is_azure_target=False, seed=randint(0, 100000), max_tokens=50)

    def _initialize_non_azure_vars(self, deployment_name: str, endpoint: str, api_key: str):
        """
        Initializes variables to communicate with the (non-Azure) OpenAI API
        """
        self._deployment_name = deployment_name
        # Ignoring mypy type error. The OpenAI client and Azure OpenAI client have the same private base class
        self._async_client = AsyncOpenAI(api_key=api_key, base_url=endpoint)
