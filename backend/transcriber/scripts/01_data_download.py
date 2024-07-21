import os

# 데이터 저장 디렉토리
data_dir = '../data'
os.makedirs(data_dir, exist_ok=True)

# 데이터 다운로드
import requests

# MAESTRO 데이터셋 다운로드 URL
maestro_url = 'https://storage.googleapis.com/magentadata/datasets/maestro/v3.0.0/maestro-v3.0.0.zip'
response = requests.get(maestro_url)

# 데이터 저장
with open(os.path.join(data_dir, 'maestro-v3.0.0.zip'), 'wb') as f:
    f.write(response.content)

# ZIP 파일 압축 해제
import zipfile

with zipfile.ZipFile(os.path.join(data_dir, 'maestro-v3.0.0.zip'), 'r') as zip_ref:
    zip_ref.extractall(data_dir)

print("데이터 다운로드 및 압축 해제 완료")