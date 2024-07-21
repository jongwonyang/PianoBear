import os
import csv
import numpy as np
import librosa
import note_seq
from multiprocessing import Process, current_process, cpu_count

DATA_DIR = '../data/maestro-v3.0.0'
SAVE_DIR = '../data/preprocessed'

def process_midi_audio(midi_path, audio_path, cqt_save_path, onset_save_path, offset_save_path):
    try:
        # 파일 로드 및 CQT 변환
        y, sr = librosa.load(audio_path, sr=16000)
        cqt = librosa.cqt(y, sr=sr, fmin=librosa.midi_to_hz(21), n_bins=264, hop_length=160, bins_per_octave=36)
        cqt = np.abs(cqt).T
        cqt = np.pad(cqt, ((0, 10), (0, 0)), mode='constant')

        # 빈 결과 배열 생성
        onset = np.zeros((cqt.shape[0], 88))
        offset = np.zeros((cqt.shape[0], 88))

        # 패딩
        one_seq = 100
        pad_size = one_seq - (cqt.shape[0] % one_seq)
        cqt = np.pad(cqt, ((0, pad_size), (0, 0)), mode='constant')
        onset = np.pad(onset, ((0, pad_size), (0, 0)), mode='constant')
        offset = np.pad(offset, ((0, pad_size), (0, 0)), mode='constant')

        # MIDI 노트 처리
        note_seq_data = note_seq.midi_file_to_note_sequence(midi_path)
        for note in note_seq_data.notes:
            pitch = note.pitch - 21
            start_frame = int(note.start_time * 100)
            end_frame = int(note.end_time * 100)
            onset[start_frame:start_frame + 4, pitch] = 1
            offset[start_frame:end_frame, pitch] = 1

        # 크기 변환
        cqt = cqt.reshape(cqt.shape[0] // one_seq, one_seq, 264)
        onset = onset.reshape(onset.shape[0] // one_seq, one_seq, 88)
        offset = offset.reshape(offset.shape[0] // one_seq, one_seq, 88)

        file_name = os.path.splitext(os.path.basename(audio_path))[0]
        np.save(os.path.join(cqt_save_path, file_name), cqt)
        np.save(os.path.join(onset_save_path, file_name), onset)
        np.save(os.path.join(offset_save_path, file_name), offset)
    except Exception as e:
        print(f"Error processing {midi_path} and {audio_path}: {e}")

def process_files(midi_paths, audio_paths, cqt_save_dir, onset_save_dir, offset_save_dir):
    process_name = current_process().name
    print(f"{process_name} started with {len(midi_paths)} files")
    for midi_file, audio_file in zip(midi_paths, audio_paths):
        midi_path = os.path.join(DATA_DIR, midi_file)
        audio_path = os.path.join(DATA_DIR, audio_file)
        process_midi_audio(midi_path, audio_path, cqt_save_dir, onset_save_dir, offset_save_dir)
    print(f"{process_name} finished")

def main(save_dir, use_multiprocessing=True, num_processes=None):
    if num_processes is None:
        num_processes = cpu_count()  # CPU 코어 수만큼 프로세스 설정

    os.makedirs(save_dir, exist_ok=True)
    os.makedirs(os.path.join(save_dir, 'trainX'), exist_ok=True)
    os.makedirs(os.path.join(save_dir, 'validX'), exist_ok=True)
    os.makedirs(os.path.join(save_dir, 'trainONSET'), exist_ok=True)
    os.makedirs(os.path.join(save_dir, 'validONSET'), exist_ok=True)
    os.makedirs(os.path.join(save_dir, 'trainOFFSET'), exist_ok=True)
    os.makedirs(os.path.join(save_dir, 'validOFFSET'), exist_ok=True)
    os.makedirs(os.path.join(save_dir, 'testX'), exist_ok=True)
    os.makedirs(os.path.join(save_dir, 'testONSET'), exist_ok=True)
    os.makedirs(os.path.join(save_dir, 'testOFFSET'), exist_ok=True)

    train_midi_paths, train_audio_paths = [], []
    val_midi_paths, val_audio_paths = [], []
    test_midi_paths, test_audio_paths = [], []

    with open(os.path.join(DATA_DIR, 'maestro-v3.0.0.csv'), 'r', encoding='utf-8') as csv_file:
        reader = csv.DictReader(csv_file)
        for row in reader:
            if row['split'] == 'train':
                train_midi_paths.append(row['midi_filename'])
                train_audio_paths.append(row['audio_filename'])
            elif row['split'] == 'validation':
                val_midi_paths.append(row['midi_filename'])
                val_audio_paths.append(row['audio_filename'])
            elif row['split'] == 'test':
                test_midi_paths.append(row['midi_filename'])
                test_audio_paths.append(row['audio_filename'])

    if use_multiprocessing:
        # Training data processing
        train_segment_length = len(train_midi_paths) // num_processes
        processes = []

        for i in range(num_processes):
            start_index = i * train_segment_length
            end_index = None if i == num_processes - 1 else (i + 1) * train_segment_length
            p = Process(target=process_files, args=(
                train_midi_paths[start_index:end_index], train_audio_paths[start_index:end_index],
                os.path.join(save_dir, 'trainX'), os.path.join(save_dir, 'trainONSET'), os.path.join(save_dir, 'trainOFFSET')))
            processes.append(p)
            p.start()

        for p in processes:
            p.join()

        # Validation data processing
        val_segment_length = len(val_midi_paths) // num_processes
        val_processes = []

        for i in range(num_processes):
            start_index = i * val_segment_length
            end_index = None if i == num_processes - 1 else (i + 1) * val_segment_length
            p = Process(target=process_files, args=(
                val_midi_paths[start_index:end_index], val_audio_paths[start_index:end_index],
                os.path.join(save_dir, 'validX'), os.path.join(save_dir, 'validONSET'), os.path.join(save_dir, 'validOFFSET')))
            val_processes.append(p)
            p.start()

        for p in val_processes:
            p.join()

        # Test data processing
        test_segment_length = len(test_midi_paths) // num_processes
        test_processes = []

        for i in range(num_processes):
            start_index = i * test_segment_length
            end_index = None if i == num_processes- 1 else (i + 1) * test_segment_length
            p = Process(target=process_files, args=(
                test_midi_paths[start_index:end_index], test_audio_paths[start_index:end_index],
                os.path.join(save_dir, 'testX'), os.path.join(save_dir, 'testONSET'), os.path.join(save_dir, 'testOFFSET')
            ))
            test_processes.append(p)
            p.start()

        for p in test_processes:
            p.join()
    else:
        # Training data processing
        process_files(train_midi_paths, train_audio_paths,
                      os.path.join(save_dir, 'trainX'),
                      os.path.join(save_dir, 'trainONSET'),
                      os.path.join(save_dir, 'trainOFFSET'))
        # Validation data processing
        process_files(val_midi_paths, val_audio_paths,
                      os.path.join(save_dir, 'validX'),
                      os.path.join(save_dir, 'validONSET'),
                      os.path.join(save_dir, 'validOFFSET'))
        # Test data processing
        process_files(test_midi_paths, test_audio_paths,
                      os.path.join(save_dir, 'testX'),
                      os.path.join(save_dir, 'testONSET'),
                      os.path.join(save_dir, 'testOFFSET'))

if __name__ == '__main__':
    main(SAVE_DIR, use_multiprocessing=True)
