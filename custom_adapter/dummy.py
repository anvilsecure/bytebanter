from pyrit.prompt_target import PromptTarget
from pyrit.models import PromptRequestResponse, PromptRequestPiece, Score, ScoreType
from typing import Optional
from pyrit.score.scorer import Scorer


class DummyScorer(Scorer):
    async def score_async(self, request_response: PromptRequestPiece, *, task: Optional[str] = None) -> list[Score]:
        return [Score(score_value="str", score_value_description="str", score_type=ScoreType(), score_category="str",
                      score_rationale="str", score_metadata="str", prompt_request_response_id="str")]

    def validate(self, request_response: PromptRequestPiece, *, task: Optional[str] = None):
        pass


class DummyTarget(PromptTarget):
    async def send_prompt_async(self, *, prompt_request: PromptRequestResponse) -> PromptRequestResponse:
        request = prompt_request.request_pieces[0]
        return PromptRequestResponse(
            request_pieces=[
                PromptRequestPiece(
                    role="assistant",
                    original_value="dummy",
                    conversation_id="1",
                    labels={"label1": "it's dummy"},
                    prompt_target_identifier=request.prompt_target_identifier,
                    orchestrator_identifier=request.orchestrator_identifier,
                    original_value_data_type="text",
                    converted_value_data_type="text",
                    prompt_metadata=None,
                    response_error="none",
                )
            ]
        )

    def _validate_request(self, *, prompt_request: PromptRequestResponse) -> None:
        pass
