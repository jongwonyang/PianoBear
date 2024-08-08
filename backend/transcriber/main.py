from fastapi import FastAPI, File, UploadFile
from fastapi.responses import FileResponse, JSONResponse
import os
import shutil
from service import predict, ns_to_pretty_midi, save_pretty_midi, split_piano_other_ns, midi_to_musicxml
from tempfile import NamedTemporaryFile
from fastapi.middleware.cors import CORSMiddleware
import uuid
import threading
import time

app = FastAPI()

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
        
        checkpoint_path = 'checkpoints/mt3/'  # 체크포인트 경로 설정
        original_ns = predict(temp_audio_file, checkpoint_path)

        # 원본 MIDI 파일 저장
        original_midi_path = os.path.join(UPLOAD_DIR, unique_filename + "_original.mid")
        original_midi_data = ns_to_pretty_midi(original_ns)
        save_pretty_midi(original_midi_data, original_midi_path)

        # 피아노와 다른 악기들로 분리
        piano_ns, other_ns = split_piano_other_ns(original_ns)

        # 피아노 MIDI 파일 저장
        piano_midi_path = os.path.join(UPLOAD_DIR, unique_filename + "_piano.mid")
        piano_midi_data = ns_to_pretty_midi(piano_ns)
        save_pretty_midi(piano_midi_data, piano_midi_path)

        # 다른 악기들 MIDI 파일 저장
        other_midi_path = os.path.join(UPLOAD_DIR, unique_filename + "_other_instruments.mid")
        other_midi_data = ns_to_pretty_midi(other_ns)
        save_pretty_midi(other_midi_data, other_midi_path)

        # 피아노 MusicXML 파일 저장
        piano_musicxml_path = os.path.join(UPLOAD_DIR, unique_filename + "_piano.musicxml")
        midi_to_musicxml(piano_midi_path, piano_musicxml_path)

        return JSONResponse(content={
            "original_midi": original_midi_path,
            "piano_midi": piano_midi_path,
            "piano_musicxml": piano_musicxml_path,
            "other_instruments_midi": other_midi_path
        })
    finally:
        os.remove(temp_audio_file)

@app.get("/download/")
async def download_file(file_path: str):
    return FileResponse(file_path)
