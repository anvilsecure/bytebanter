import logging
from typing import Optional
import boto3

from pyrit.chat_message_normalizer import ChatMessageNop, ChatMessageNormalizer
from botocore.exceptions import NoCredentialsError
from pyrit.memory import MemoryInterface
from pyrit.models import ChatMessage
from pyrit.prompt_target import OllamaChatTarget, PromptChatTarget
from json import JSONEncoder


logger = logging.getLogger(__name__)


class MatomeChatTarget(OllamaChatTarget):

    def __init__(
            self,
            *,
            model_id: str = "meta.llama3-70b-instruct-v1:0",
            region: str = 'us-east-1',
            chat_message_normalizer: ChatMessageNormalizer = ChatMessageNop(),
            max_requests_per_minute: Optional[int] = None,
    ):
        PromptChatTarget.__init__(self, max_requests_per_minute=max_requests_per_minute)
        self.model_id = model_id
        self.bedrock_client = boto3.client('bedrock-runtime', region_name=region)
        self.chat_message_normalizer = chat_message_normalizer

    async def _complete_chat_async(
            self,
            messages: list[ChatMessage],
    ) -> str:
        squashed_messages = self.chat_message_normalizer.normalize(messages)
        messages_list = [message.model_dump(exclude_none=True) for message in squashed_messages]
        prompt = JSONEncoder().encode(messages_list)
        conversation = [
            {
                "role": "user",
                "content": [{"text": prompt}],
            }
        ]

        model_id = "meta.llama3-70b-instruct-v1:0"

        try:
            response = self.bedrock_client.converse(
                modelId=model_id,
                messages=conversation,
                inferenceConfig={
                    "maxTokens": 2048,
                    "temperature": 0.5,
                    "topP": 0.9
                },
            )
            return response["output"]["message"]["content"][0]["text"]
        except NoCredentialsError:
            print("To see the LLM-suggested summary, export Admin credentials for one of your aliases.")
        except Exception as e:
            if hasattr(e, 'response') and e.response['Error']['Code'] == 'UnrecognizedClientException':
                print("To see the LLM-suggested summary, export Admin credentials for one of your aliases.")
            elif (hasattr(e, 'response') and e.response['Error']['Message'] ==
                  "You don't have access to the model with the specified model ID."):
                print(
                    "Enable model Llama 3 70B Instruct in your account: "
                    "https://us-east-1.console.aws.amazon.com/bedrock/home?region=us-east-1#/modelaccess")
            else:
                print(f"Error: {e}")
