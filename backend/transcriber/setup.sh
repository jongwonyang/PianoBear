#!/bin/bash

# 가상환경 생성
VENV_PATH="./venv"
python3.10 -m venv venv

# 가상환경 활성화
source "$VENV_PATH/bin/activate"

# MT3 다운로드
MT3="./mt3"
if [ ! -e "$MT3" ]; then
    git clone https://github.com/magenta/mt3
    rm -rf ./mt3/.git
fi

# 라이브러리 설치
pip install -e mt3
pip install gsutil jax[cuda12] nest-asyncio music21 pyfluidsynth==1.3.0
pip install uvicorn fastapi python-multipart

# 모델 다운로드
CHECKPOINTS="./checkpoints"
if [ ! -e "$CHECKPOINTS" ]; then
    gsutil -q -m cp -r gs://mt3/checkpoints .
fi

# Sound font 다운로드
SOUND_FONT="./SGM-v2.01-Sal-Guit-Bass-V1.3.sf2"
if [ ! -e "$SOUND_FONT" ]; then
    gsutil -q -m cp gs://magentadata/soundfonts/SGM-v2.01-Sal-Guit-Bass-V1.3.sf2 .
fi

# 디렉터리 생성
TEMP_DIR="./temp_files"
if [ ! -e "$TEMP_DIR" ]; then
    mkdir temp_files
fi

# 가상환경 해제
deactivate