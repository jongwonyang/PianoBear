# service.py

import note_seq
from inference_model import InferenceModel
from note_seq.protobuf import music_pb2
import music21

SAMPLE_RATE = 16000

def load_audio(audio_path, sample_rate=SAMPLE_RATE):
    with open(audio_path, 'rb') as f:
        audio_file = f.read()
    return note_seq.audio_io.wav_data_to_samples_librosa(audio_file, sample_rate=sample_rate)

def _predict(audio_path, checkpoint_path, model_type='mt3'):
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
    midi = music21.converter.parse(midi_path)
    midi.write("mxl", mxl_output_path)
