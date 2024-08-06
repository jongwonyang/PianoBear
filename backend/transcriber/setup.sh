#!/bin/bash

VENV_PATH="./venv"

# 가상환경 생성
python3.10 -m venv venv

# 가상환경 활성화
source "$VENV_PATH/bin/activate"

# MT3 다운로드
git clone https://github.com/magenta/mt3
rm -rf ./mt3/.git

# 라이브러리 설치
pip install gsutil jax[cuda12] nest-asyncio music21 pyfluidsynth==1.3.0
pip install uvicorn fastapi python-multipart
pip install -e mt3

# 모델 다운로드
gsutil -q -m cp -r gs://mt3/checkpoints .

# Sound font 다운로드
gsutil -q -m cp gs://magentadata/soundfonts/SGM-v2.01-Sal-Guit-Bass-V1.3.sf2 .

# 디렉터리 생성
mkdir temp_files

# 가상환경 해제
deactivate