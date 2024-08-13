from music21 import *
import subprocess
import os
import fluidsynth
from midi2audio import FluidSynth

music_title = "let-it-go"

# musicxml파일을 midi파일로 변환하는 함수
def convert_musicxml_to_midi(musicxml_file, midi_file, wav_file):
    score = converter.parse(musicxml_file)
    mf = midi.translate.music21ObjectToMidiFile(score)

    mf.open(midi_file, 'wb')
    mf.write()
    mf.close()
    
    fs = FluidSynth()
    fs.midi_to_audio(midi_file_path, wav_file_path)


musicxml_file_path = './musicxml/'+music_title+'.mxl'
midi_file_path = './midi/'+music_title+'.mid'
wav_file_path = './wav/'+music_title+'.wav'

convert_musicxml_to_wav(musicxml_file_path, midi_file_path, wav_file_path)