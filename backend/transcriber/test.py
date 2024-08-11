# test.py

import os
import music21

from service import load_audio, ns_to_pretty_midi, predict, save_pretty_midi, split_piano_other_ns

AUDIO_PATH = "temp_files/Chopsticks.wav"
CP_PATH = "checkpoints/mt3/"
MIDI_PATH = "temp_files/Chopsticks.mid"

def midi_to_musicxml_small_utf(midi_path, out_path):
    midi = music21.converter.parse(midi_path)
    midi.write("musicxml", out_path)

def midi_to_musicxml_capital_utf(midi_path, out_path):
    midi = music21.converter.parse(midi_path)
    midi.write("musicxml", out_path)

    with open(out_path, "r", encoding="utf-8") as file:
        lines = file.readlines()
    
    lines[0] = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"

    with open(out_path, "w", encoding="utf-8") as file:
        file.writelines(lines)

def midi_to_mxl_music21(midi_path, out_path):
    midi = music21.converter.parse(midi_path)
    midi.write("mxl", out_path)

if __name__ == "__main__":
    if not os.path.isfile(MIDI_PATH):
        ns = predict(AUDIO_PATH, CP_PATH)
        pinao_ns, _ = split_piano_other_ns(ns)
        piano_midi = ns_to_pretty_midi(pinao_ns)
        save_pretty_midi(piano_midi, MIDI_PATH)

    midi_to_musicxml_small_utf(MIDI_PATH, "temp_files/small_utf.musicxml")
    midi_to_musicxml_capital_utf(MIDI_PATH, "temp_files/capital_utf.musicxml")
    midi_to_mxl_music21(MIDI_PATH, "temp_files/music21_mxl.mxl")

