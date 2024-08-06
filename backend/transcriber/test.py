# test.py

import warnings
warnings.filterwarnings("ignore")

from service import predict, split_piano_other_ns, ns_to_pretty_midi, save_pretty_midi
from service import midi_to_musicxml

def main():
    audio_path = './input/Let It Go (Instrumental Karaoke) - Frozen (OST).wav'
    checkpoint_path = 'checkpoints/mt3/'
    est_ns = predict(audio_path, checkpoint_path)
    piano_ns, other_ns = split_piano_other_ns(est_ns)

    all_midi = ns_to_pretty_midi(est_ns)
    piano_midi = ns_to_pretty_midi(piano_ns)
    other_midi = ns_to_pretty_midi(other_ns)

    save_pretty_midi(all_midi, 'output/all.mid')
    save_pretty_midi(piano_midi, 'output/piano.mid')
    save_pretty_midi(other_midi, 'output/other.mid')
    midi_to_musicxml('output/all.mid', 'output/all.mxl')
    midi_to_musicxml('output/piano.mid', 'output/piano.mxl')
    midi_to_musicxml('output/other.mid', 'output/other.mxl')

if __name__ == "__main__":
    main()
