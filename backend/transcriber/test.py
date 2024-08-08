# test.py

import warnings
warnings.filterwarnings("ignore")

from service import predict, split_piano_other_ns, ns_to_pretty_midi, save_pretty_midi
from service import midi_to_musicxml

def test():
    audio_path = './temp_files/Sparkle (Instrumental Only).wav'
    checkpoint_path = 'checkpoints/mt3/'
    est_ns = predict(audio_path, checkpoint_path)
    piano_ns, other_ns = split_piano_other_ns(est_ns)

    all_midi = ns_to_pretty_midi(est_ns)
    piano_midi = ns_to_pretty_midi(piano_ns)
    other_midi = ns_to_pretty_midi(other_ns)

    save_pretty_midi(all_midi, 'temp_files/all.mid')
    save_pretty_midi(piano_midi, 'temp_files/piano.mid')
    save_pretty_midi(other_midi, 'temp_files/other.mid')
    midi_to_musicxml('temp_files/all.mid', 'temp_files/all.musicxml')
    midi_to_musicxml('temp_files/piano.mid', 'temp_files/piano.musicxml')
    midi_to_musicxml('temp_files/other.mid', 'temp_files/other.musicxml')

if __name__ == "__main__":
    test()
