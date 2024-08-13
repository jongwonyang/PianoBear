from fastapi import FastAPI, File, UploadFile
from fastapi.responses import JSONResponse
from fastdtw import fastdtw
from scipy.spatial.distance import euclidean

from music21 import *
import subprocess
import os
import numpy
import fluidsynth
from midi2audio import FluidSynth
import uuid
import math

app = FastAPI()

# seconds to sample audio file for
sample_time = 500
# step size (in points) of cross correlation
step = 1
# minimum number of points that must overlap in cross correlation
# exception is raised if this cannot be met
min_overlap = 20
# report match when cross correlation has a peak exceeding threshold
threshold = 0.1


# calculate fingerprint
# Generate file.mp3.fpcalc by "fpcalc -raw -length 500 file.mp3"
def calculate_fingerprints(filename):
    if os.path.exists(filename + '.fpcalc'):
        print("Found precalculated fingerprint for %s" % (filename))
        f = open(filename + '.fpcalc', "r")
        fpcalc_out = ''.join(f.readlines())
        f.close()
    else:
        print("Calculating fingerprint by fpcalc for %s" % (filename))
        fpcalc_out = str(subprocess.check_output(['./fpcalc', '-raw', '-length', str(sample_time), filename])).strip().replace('\\n', '').replace("'", "")

    fingerprint_index = fpcalc_out.find('FINGERPRINT=') + 12
    # convert fingerprint to list of integers
    fingerprints = list(map(int, fpcalc_out[fingerprint_index:].split(',')))
    
    return fingerprints
  
# returns correlation between lists
def correlation(listx, listy):
    if len(listx) == 0 or len(listy) == 0:
        # Error checking in main program should prevent us from ever being
        # able to get here.
        raise Exception('Empty lists cannot be correlated.')
    if len(listx) > len(listy):
        listx = listx[:len(listy)]
    elif len(listx) < len(listy):
        listy = listy[:len(listx)]
    
    covariance = 0
    for i in range(len(listx)):
        covariance += 32 - bin(listx[i] ^ listy[i]).count("1")
    covariance = covariance / float(len(listx))
    
    return covariance/32
  
# return cross correlation, with listy offset from listx
def cross_correlation(listx, listy, offset):
    if offset > 0:
        listx = listx[offset:]
        listy = listy[:len(listx)]
    elif offset < 0:
        offset = -offset
        listy = listy[offset:]
        listx = listx[:len(listy)]
    if min(len(listx), len(listy)) < min_overlap:
        # Error checking in main program should prevent us from ever being
        # able to get here.
        return 
    #raise Exception('Overlap too small: %i' % min(len(listx), len(listy)))
    return correlation(listx, listy)
  
# cross correlate listx and listy with offsets from -span to span
def compare(listx, listy, step):
    # if span > min(len(listx), len(listy)):
    #     # Error checking in main program should prevent us from ever being
    #     # able to get here.
    #     raise Exception('span >= sample size: %i >= %i\n'
    #                     % (span, min(len(listx), len(listy)))
    #                     + 'Reduce span, reduce crop or increase sample_time.')
    span = math.floor(min(len(listx), len(listy)) / 2)
    corr_xy = []
    for offset in numpy.arange(-span, span + 1, step):
        corr_xy.append(cross_correlation(listx, listy, offset))
    return corr_xy
  
# return index of maximum value in list
def max_index(listx):
    max_index = 0
    max_value = listx[0]
    for i, value in enumerate(listx):
        if value > max_value:
            max_value = value
            max_index = i
    return max_index
  
def get_max_corr(corr, source, target):
    max_corr_index = max_index(corr)
    max_corr_offset = -math.floor(len(corr) / 2) / 2 + max_corr_index * step
    #print("max_corr_index = ", max_corr_index, "max_corr_offset = ", max_corr_offset)
    # report matches
    if corr[max_corr_index] > threshold:
        print("File A: %s" % (source))
        print("File B: %s" % (target))
        print('Match with correlation of %.2f%% at offset %i'
             % (corr[max_corr_index] * 100.0, max_corr_offset))
    return corr[max_corr_index] * 100.0

def correlate(source, target):
    fingerprint_source = calculate_fingerprints(source)
    fingerprint_target = calculate_fingerprints(target)
    # print(fingerprint_source)
    # print(fingerprint_target)
    corr = compare(fingerprint_source, fingerprint_target, step)
    # print(corr)
    max_corr = get_max_corr(corr, source, target)
    return max_corr

    
@app.post("/compare")
async def compare_music_files(
    musicxml_file: UploadFile = File(...), 
    wav_file: UploadFile = File(...)
):
    uuid1 = uuid.uuid1()
    # MusicXML 파일 저장
    musicxml_path = f"./musicxml/{uuid1}.mxl"
    with open(musicxml_path, "wb") as f:
        f.write(await musicxml_file.read())
    
    # WAV 파일 저장
    wav_path = f"./wav/{uuid1}.wav"
    with open(wav_path, "wb") as f:
        f.write(await wav_file.read())
    
    convert_musicxml_to_midi(uuid1)

    result = correlate(f"./wav/{uuid1}.wav", f"./wav_mid/{uuid1}.wav")
    
    return JSONResponse(result)

# musicxml파일을 midi파일로 변환하는 함수
def convert_musicxml_to_midi(uuid):
    score = converter.parse(f"./musicxml/{uuid}.mxl")
    mf = midi.translate.music21ObjectToMidiFile(score)
    mf.open(f"./midi/{uuid}.mid", 'wb')
    mf.write()
    mf.close()
    fs = FluidSynth()
    fs.midi_to_audio(f"./midi/{uuid}.mid", f"./wav_mid/{uuid}.wav")


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=7003)
