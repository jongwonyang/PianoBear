<template>
    <div class="back">
        <div class="sheetback">
            <a v-if="wavBlobUrl" :href="wavBlobUrl" download="recording.wav">WAV 다운로드</a>
            <div class="practice-options" :style="{ visibility: props.challenge ? 'hidden' : 'visible' }">
                <v-sheet :elevation="1" color="#D9F6D9" :height="40" :width="135" class="setOption">
                    <label for="velocity"> 속도 </label>
                    <input type="number" id="velocity" name="velocity" :value="velo" min="0.25" max="2" step="0.25"
                        readonly style="width: 6.5vh; position: relative; left: 1.5vh;" />

                    <div class="btns">
                        <v-btn icon="mdi-triangle-small-up" class="velo" @click="velo <= 1.75 ? velo += 0.25 : null"
                            size="small" :width="15" :height="15" variant="tonal"></v-btn>
                        <v-btn icon="mdi-triangle-small-down" class="velo" @click="velo >= 0.5 ? velo -= 0.25 : null"
                            size="small" :width="15" :height="15" variant="tonal"></v-btn>
                    </div>
                </v-sheet>
                <v-sheet :elevation="1" color="#D9F6D9" :height="40" :width="135" class="setOption">
                    <v-switch color="#6c8056" label="음소거" type="checkbox" class="player-option" id="option-mute"
                        value="mute" hide-details></v-switch>
                </v-sheet>
            </div>
            <v-sheet v-if="!props.challenge" id="player" :elevation="1" color="#D9F6D9" :height="36" :width="140">
                <v-btn-toggle v-model="toggle_one" mandatory shaped>
                    <v-btn icon="mdi-reload" :width="27" :height="27" class="player" id="rewind" variant="text"
                        @click="stateChange('rewind')"></v-btn>
                    <v-btn icon="mdi-pause" :width="27" :height="27" class="player" id="pause" variant="text"
                        @click="stateChange('pause')"></v-btn>
                    <v-btn icon="mdi-play" :width="27" :height="27" class="player" id="play" variant="text"
                        @click="stateChange('play')"></v-btn>
                </v-btn-toggle>
            </v-sheet>
            <v-sheet v-if="props.challenge" id="player" :elevation="1" color="#D9F6D9" :height="40" :width="140">
                <v-btn v-show="isRecording" prepend-icon="mdi-reload" :width="125" :height="32" class="player"
                    id="rewind" variant="text" @click="stopRecording(false)">녹음중..</v-btn>
                <v-btn v-show="!isRecording" prepend-icon="mdi-play" :width="125" :height="32" class="player" id="play"
                    variant="text">도전하기</v-btn>
            </v-sheet>
            <div v-show="status" id="sheet-container" :value="status"></div>
            <div v-if="!status" class="loading">
                <img src="@/assets/characters/토니/토니악보변환.png" width="270px" />
                <h1>Now Loading...
                    <v-progress-linear color="dark-blue" indeterminate></v-progress-linear>
                </h1>
            </div>
            <Piano :curr-pitch="num" style="position: relative; margin: auto; margin-top: 2vh;" />
        </div>
        <ChallengeModal v-if="props.challenge" :play-challenge="playChallenge" :dialog="dialog"
            @change-dialog="changeDialog" @start-record="startRecording" />
    </div>
</template>

<script setup>
import { onMounted, ref, onUnmounted, defineProps } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { pageLoad, sheetSelect, createPlayer, num, reset, chaingingChallenge, stateChange } from '@/mxlplayer/demo.mjs';
import { usePianoSheetStore } from '@/stores/pianosheet';
import ChallengeModal from '@/components/PianoSheet/ChallengeModal.vue';
import Piano from './Piano.vue';

// osmdLoadCheck
// vue 변수
const store = usePianoSheetStore();
const route = useRoute();
const router = useRouter();
const props = defineProps({
    challenge: Boolean,
});

// mxl-player 변수
const velo = ref(1);
const status = ref(false);
const toggle_one = ref(1);
const musicXml = ref();
const nowSheet = ref(route.params['id']);
const playChallenge = ref(false);

// record 변수
const isRecording = ref(false);
const recorder = ref();
const audioChunks = ref([]);
const wavBlobUrl = ref();
const stream = ref();
const dialog = ref(true);

// space-bar 로 재생, 일시정지 설정
window.addEventListener("keydown", (e) => {
    if (e.key === " " && !props.challenge) {
        if (toggle_one.value == 2) {
            toggle_one.value = 1;
        } else {
            toggle_one.value = 2;
        }
    }
});

// mxl 불러오기
const loadMxl = async function (id) {
    musicXml.value = await store.mxlLoadfun(id);
}

// 녹음 재생
async function startRecording() {
    stateChange('play')
    recorder.value = new MediaRecorder(stream.value);
    recorder.value.ondataavailable = (e) => {
        audioChunks.value.push(e.data);
    };
    recorder.value.start();
    isRecording.value = true;
}

// 녹음 중지
function stopRecording(isSuccess) {
    if (!isSuccess) {
        dialog.value = true;
        stateChange('rewind')
        isRecording.value = false;
        recorder.value.reset();
        audioChunks.value = [];
        return;
    } else if (recorder.value) {
        recorder.value.stop();
        recorder.value.onstop = () => {
            const audioBlob = new Blob(audioChunks.value, { type: 'audio/wav' });
            console.log(audioBlob)
            wavBlobUrl.value = URL.createObjectURL(audioBlob);
            isRecording.value = false;
            audioChunks.value = [];
        };
    }
}

const changeDialog = function () {
    dialog.value = !dialog.value;
}


// 악보 불러오기
onMounted(async () => {
    await pageLoad();
    await loadMxl(nowSheet.value);
    await sheetSelect(musicXml.value);
    await createPlayer(props.challenge);
    if (props.challenge) {
        playChallenge.value = true;
        chaingingChallenge(true);
    } else {
        chaingingChallenge(false);
    }
    status.value = true;
    if (props.challenge && navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        stream.value = await navigator.mediaDevices.getUserMedia({ audio: true });
    }
});

// 떠날 때, reset
router.beforeEach((to, from) => {
    if (from.name === 'pianoPractice' || from.name === 'pianoChallenge') {
        reset();
        stopRecording(true);
    }
});
</script>

<style scoped>
.back {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
}

.sheetback {
    width: 150vh;
    position: relative;
    top: 0;
    left: 30vh;
}

.sheetback * {
    font-weight: bold;
}

#sheet-container {
    width: 150vh;
    height: 100%;
    border: 5px solid #bde7bd7d;
    border-radius: 5px;
    padding: 0 5vh;
    background: #FFFFF8;
}

#player {
    position: relative;
    margin: auto;
    display: flex;
    justify-content: center;
    z-index: 1000;
    border-radius: 20px;
    margin-bottom: 2vh;
}

.player {
    font-size: larger;
    margin: 5px;
}

.practice-options {
    position: relative;
    display: flex;
    justify-content: flex-end;
    width: 150vh;
    top: 5vh;
    z-index: 0;
}

.setOption {
    margin-left: 1.5vh;
    margin-right: 1.5vh;
    border-radius: 20px;
    display: flex;
    align-items: center;
    padding: 1.5vh;
    justify-content: space-between;
}

.btns {
    width: 15px;
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-content: center;
}

.loading {
    position: relative;
    display: flex;
    width: 150vh;
    height: 70vh;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

h1 {
    position: relative;
    left: 0;
    top: 3vh;
    width: 40vh;
    text-align: center;
}
</style>