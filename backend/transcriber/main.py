from contextlib import asynccontextmanager
import os
import time
import uuid
import shutil
import mimetypes
import threading

from fastapi import FastAPI, File, HTTPException, UploadFile
from fastapi.responses import FileResponse, JSONResponse
from fastapi.middleware.cors import CORSMiddleware
from inference_model_manager import InferenceModelManager
from service import ns_to_pretty_midi, save_pretty_midi, split_piano_other_ns, midi_to_mxl

@asynccontextmanager
async def lifespan(app: FastAPI):
    # 서버 시작 시
    inference_model_manager = InferenceModelManager(checkpoint_path="checkpoints/mt3/")
    inference_model_manager.load_model()

    # 앱에 모델을 추가
    app.state.inference_model_manager = inference_model_manager

    yield

    # 서버 종료 시 (리소스 해제 등)
    del app.state.inference_model_manager

app = FastAPI(lifespan=lifespan)

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:7000", 
                   "http://localhost:8888", 
                   "http://localhost:5173", 
                   "https://pianobear.kr"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

UPLOAD_DIR = "temp_files/"

# 임시 파일 정리 함수
def remove_old_files(directory, age_limit_seconds):
    while True:
        now = time.time()
        for filename in os.listdir(directory):
            file_path = os.path.join(directory, filename)
            if os.path.isfile(file_path):
                file_age = now - os.path.getmtime(file_path)
                if file_age > age_limit_seconds:
                    os.remove(file_path)
        time.sleep(age_limit_seconds)

# 임시 파일 정리 스레드 시작
file_cleaner_thread = threading.Thread(target=remove_old_files, args=(UPLOAD_DIR, 3600), daemon=True)
file_cleaner_thread.start()

@app.post("/upload-audio/")
async def upload_audio(file: UploadFile = File(...)):
    unique_filename = str(uuid.uuid4())
    temp_audio_file = os.path.join(UPLOAD_DIR, unique_filename + ".wav")
    try:
        with open(temp_audio_file, "wb") as f:
            shutil.copyfileobj(file.file, f)
        
        original_ns = app.state.inference_model_manager.predict(temp_audio_file)

        # 원본 MIDI 파일 저장
        all_midi_path = os.path.join(UPLOAD_DIR, unique_filename + "_all.mid")
        original_midi_data = ns_to_pretty_midi(original_ns)
        save_pretty_midi(original_midi_data, all_midi_path)

        # 피아노와 다른 악기들로 분리
        piano_ns, other_ns = split_piano_other_ns(original_ns)

        # 피아노 MIDI 파일 저장
        piano_midi_path = os.path.join(UPLOAD_DIR, unique_filename + "_piano.mid")
        piano_midi_data = ns_to_pretty_midi(piano_ns)
        save_pretty_midi(piano_midi_data, piano_midi_path)

        # 다른 악기들 MIDI 파일 저장
        other_midi_path = os.path.join(UPLOAD_DIR, unique_filename + "_other_inst.mid")
        other_midi_data = ns_to_pretty_midi(other_ns)
        save_pretty_midi(other_midi_data, other_midi_path)

        # 피아노 MusicXML 파일 저장
        piano_mxl_path = os.path.join(UPLOAD_DIR, unique_filename + "_piano.mxl")
        midi_to_mxl(piano_midi_path, piano_mxl_path)

        return JSONResponse(content={
            "piano_mxl": piano_mxl_path,
            "all_midi": all_midi_path,
            "piano_midi": piano_midi_path,
            "other_inst_midi": other_midi_path
        })
    finally:
        os.remove(temp_audio_file)

@app.get("/download/")
async def download_file(file_path: str):
    # 파일이 존재하는지 확인
    if not os.path.exists(file_path):
        raise HTTPException(status_code=404, detail="File not found")

    # 확장자에 따른 media_type 추론
    media_type, _ = mimetypes.guess_type(file_path)
    if media_type is None:
        media_type = 'application/octet-stream'  # 기본값: 알 수 없는 파일 형식에 대한 일반적인 타입

    return FileResponse(file_path, media_type=media_type, filename=os.path.basename(file_path))
