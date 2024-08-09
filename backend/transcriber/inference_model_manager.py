from inference_model import InferenceModel
from service import load_audio

class InferenceModelManager:
    def __init__(self, checkpoint_path, model_type='mt3'):
        self.checkpoint_path = checkpoint_path
        self.model_type = model_type
        self.model = None

    def load_model(self):
        self.model = InferenceModel(self.checkpoint_path, self.model_type)

    def predict(self, audio_path):
        audio = load_audio(audio_path)
        return self.model(audio)