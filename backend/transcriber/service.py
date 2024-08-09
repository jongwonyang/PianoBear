# utils.py

import os
import zipfile
import shutil
import note_seq
from inference_model import InferenceModel
from note_seq.protobuf import music_pb2
import music21

SAMPLE_RATE = 16000

def load_audio(audio_path, sample_rate=SAMPLE_RATE):
    with open(audio_path, 'rb') as f:
        audio_file = f.read()
    return note_seq.audio_io.wav_data_to_samples_librosa(audio_file, sample_rate=sample_rate)

def predict(audio_path, checkpoint_path, model_type='mt3'):
    audio = load_audio(audio_path)
    inference_model = InferenceModel(checkpoint_path, model_type)
    est_ns = inference_model(audio)
    return est_ns

def split_piano_other_ns(note_sequence):
    """
    주어진 NoteSequence 객체를 피아노 노트와 나머지 악기 노트로 분리합니다.

    Args:
        note_sequence (music_pb2.NoteSequence): 원본 NoteSequence 객체.

    Returns:
        (music_pb2.NoteSequence, music_pb2.NoteSequence): 피아노 노트 시퀀스와 나머지 악기 노트 시퀀스.
    """
    piano_ns = music_pb2.NoteSequence()
    other_ns = music_pb2.NoteSequence()

    # 메타데이터와 다른 반복되지 않는 필드를 복사
    piano_ns.CopyFrom(note_sequence)
    other_ns.CopyFrom(note_sequence)

    del piano_ns.notes[:]
    del other_ns.notes[:]

    for note in note_sequence.notes:
        if note.program == 0 and not note.is_drum:
            piano_ns.notes.add().CopyFrom(note)
        else:
            other_ns.notes.add().CopyFrom(note)

    return piano_ns, other_ns


def ns_to_pretty_midi(note_sequence):
    """
    주어진 NoteSequence를 PrettyMIDI 객체로 변환합니다.

    Args:
        note_sequence (note_seq.protobuf.music_pb2.NoteSequence): 변환할 NoteSequence 객체.

    Returns:
        pretty_midi.PrettyMIDI: 변환된 PrettyMIDI 객체.
    """
    midi_data = note_seq.sequence_proto_to_pretty_midi(note_sequence)
    return midi_data

def save_pretty_midi(pretty_midi_obj, output_path):
    """
    PrettyMIDI 객체를 MIDI 파일로 저장합니다.

    Args:
        pretty_midi_obj (pretty_midi.PrettyMIDI): 저장할 PrettyMIDI 객체.
        output_path (str): 저장할 MIDI 파일 경로.
    """
    pretty_midi_obj.write(output_path)

def midi_to_mxl(midi_path, mxl_output_path):
    """
    MIDI 파일을 압축된 MusicXML (.mxl) 형식으로 변환하여 저장합니다.
    
    Args:
        midi_path (str): 입력 MIDI 파일 경로.
        mxl_output_path (str): 출력 MXL 파일 경로.
    """
    # MIDI 파일을 읽어서 Music21 스트림으로 변환
    midi = music21.converter.parse(midi_path)
    
    # MXL 파일로 저장
    midi.write('mxl', fp=mxl_output_path)
    
    # MXL 파일을 압축 해제할 임시 디렉토리 생성
    temp_dir = os.path.join(os.path.dirname(mxl_output_path), 'temp_mxl')
    os.makedirs(temp_dir, exist_ok=True)

    # MXL 파일 압축 해제
    with zipfile.ZipFile(mxl_output_path, 'r') as zip_ref:
        zip_ref.extractall(temp_dir)

    # MusicXML 파일 찾기 (보통 .xml 확장자)
    xml_file_path = None
    for root, dirs, files in os.walk(temp_dir):
        for file in files:
            if file.endswith('.xml'):
                xml_file_path = os.path.join(root, file)
                break

    # XML 파일의 첫 번째 줄 수정
    if xml_file_path:
        with open(xml_file_path, 'r', encoding='utf-8') as file:
            lines = file.readlines()

        # 첫 줄이 "<?xml version="1.0" encoding="utf-8"?>"라면 수정
        if lines[0].strip() == '<?xml version="1.0" encoding="utf-8"?>':
            lines[0] = '<?xml version="1.0" encoding="UTF-8"?>\n'

        # 수정된 내용을 다시 저장
        with open(xml_file_path, 'w', encoding='utf-8') as file:
            file.writelines(lines)

    # 수정된 XML 파일을 포함하여 MXL 파일로 다시 압축
    with zipfile.ZipFile(mxl_output_path, 'w', zipfile.ZIP_DEFLATED) as zip_ref:
        for foldername, subfolders, filenames in os.walk(temp_dir):
            for filename in filenames:
                file_path = os.path.join(foldername, filename)
                zip_ref.write(file_path, os.path.relpath(file_path, temp_dir))

    # 임시 디렉토리 삭제
    shutil.rmtree(temp_dir)
