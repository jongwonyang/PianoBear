import pandas as pd
import os
import shutil

# 원본 데이터셋 메타데이터 로드
metadata_path = 'maestro_data/maestro-v3.0.0/maestro-v3.0.0.csv'
metadata = pd.read_csv(metadata_path)

# 테스트용 데이터셋 크기 설정 (예: 100개의 파일만 선택)
num_samples = 100

# 데이터셋 종류의 비율 유지
train_metadata = metadata[metadata['split'] == 'train']
validation_metadata = metadata[metadata['split'] == 'validation']
test_metadata = metadata[metadata['split'] == 'test']

# 비율 계산
total_train = len(train_metadata)
total_validation = len(validation_metadata)
total_test = len(test_metadata)

train_ratio = total_train / len(metadata)
validation_ratio = total_validation / len(metadata)
test_ratio = total_test / len(metadata)

# 샘플 수 설정
num_train_samples = int(num_samples * train_ratio)
num_validation_samples = int(num_samples * validation_ratio)
num_test_samples = int(num_samples * test_ratio)

# 샘플링
sampled_train_metadata = train_metadata.sample(n=num_train_samples, random_state=1)
sampled_validation_metadata = validation_metadata.sample(n=num_validation_samples, random_state=1)
sampled_test_metadata = test_metadata.sample(n=num_test_samples, random_state=1)

# 테스트용 메타데이터 구성
sampled_metadata = pd.concat([sampled_train_metadata, sampled_validation_metadata, sampled_test_metadata])

# 출력 경로 설정
output_dir = 'sampled_data'
os.makedirs(output_dir, exist_ok=True)

# 필요한 폴더 생성
for year in sampled_metadata['year'].unique():
    os.makedirs(os.path.join(output_dir, str(year), 'midi'), exist_ok=True)
    os.makedirs(os.path.join(output_dir, str(year), 'audio'), exist_ok=True)

# 파일 복사 및 새로운 메타데이터 저장
new_metadata = []
for _, row in sampled_metadata.iterrows():
    midi_src = os.path.join('maestro_data/maestro-v3.0.0', row['midi_filename'])
    audio_src = os.path.join('maestro_data/maestro-v3.0.0', row['audio_filename'])
    midi_dst = os.path.join(output_dir, row['midi_filename'])
    audio_dst = os.path.join(output_dir, row['audio_filename'])
    
    os.makedirs(os.path.dirname(midi_dst), exist_ok=True)
    os.makedirs(os.path.dirname(audio_dst), exist_ok=True)
    
    shutil.copy(midi_src, midi_dst)
    shutil.copy(audio_src, audio_dst)
    
    new_metadata.append(row)

# 새로운 메타데이터 저장
new_metadata_df = pd.DataFrame(new_metadata)
new_metadata_df.to_csv(os.path.join(output_dir, 'maestro-v3.0.0.csv'), index=False)

print("테스트용 데이터셋 생성 완료.")
