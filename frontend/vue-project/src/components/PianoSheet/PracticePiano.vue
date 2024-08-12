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
                <v-btn v-show="isRecording" prepend-icon="mdi-circle" :width="125" :height="32" class="player record"
                    id="rewind" variant="text" @click="stopRecording(false)">녹음중..</v-btn>
                <v-btn v-show="!isRecording" prepend-icon="mdi-play" :width="125" :height="32" class="player" id="play"
                    variant="text">도전하기</v-btn>
            </v-sheet>
            <div v-if="!props.challenge && status" class="practiceLodingBack">
                <div class="practiceLoading">
                    <v-progress-linear v-model="knowledge" height="31" color="#D9F6D9" class="practiceLoading">
                        <strong>연습량 {{ Math.ceil(knowledge) }}%</strong>
                    </v-progress-linear>
                </div>
                <div v-if="practiceToday" class="strewbery">
                    <img v-for="n in practiceToday.practiceCount" :key="n" src="@/assets/images/산딸기.png"
                        alt="Practice Image" class="practice-image" width="25" />
                </div>
            </div>
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
            @change-dialog="changeDialog" @start-record="startRecording" :text="text" :audio-con="audioCon" />
    </div>
</template>

<script setup>
import { onMounted, ref, onUnmounted, defineProps, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { pageLoad, sheetSelect, createPlayer, num, reset, chaingingChallenge, stateChange, isMidiStop } from '@/mxlplayer/demo.mjs';
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
const knowledge = ref(0);
const velo = ref(1);
const status = ref(false);
const toggle_one = ref(1);
const musicXml = ref();
const nowSheet = ref(route.params['id']);
const playChallenge = ref();
const practiceSuccess = ref(false);

// record 변수
const isRecording = ref(false);
const recorder = ref();
const audioChunks = ref([]);
const wavBlobUrl = ref();
const stream = ref();
const dialog = ref(true);

// 모달 변수
const text = ref("* 도전하기를 위해 마이크 권한을 허용해주세요.");
const audioCon = ref(false);
const afterChallenge = ref(false);
const interval1 = ref();
const interval2 = ref();
const practiceToday = computed(() => {
    return store.practiceData.filter((e) =>
        e.practiceDate[0].toString() + e.practiceDate[1].toString() + e.practiceDate[2].toString() === '2024812'
    )[0]
})


// 연습 변수
const practiceGet = function () {
    store.practiceDatafun(nowSheet.value)
};


// space-bar 로 재생, 일시정지 설정
window.addEventListener("keydown", (e) => {
    if (!status.value) return;
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
    interval2.value = setInterval(() => {
        if (isMidiStop()) {
            stopRecording(true);
            afterChallenge.value = true;
        }
        console.log(isMidiStop());
    }, 500)
    stateChange('play')
    stream.value = await navigator.mediaDevices.getUserMedia({ audio: true });
    recorder.value = new MediaRecorder(stream.value);
    recorder.value.ondataavailable = (e) => {
        audioChunks.value.push(e.data);
    };
    recorder.value.start();
    isRecording.value = true;

    //test
    setTimeout(() => {
        stopRecording(true);
    }, 2000)

}

// 녹음 중지
async function stopRecording(isSuccess) {

    // 도전 완료정보 초기화
    clearInterval(interval2.value);

    // 도전을 완료하기 전 수행.
    if (!isSuccess) {
        dialog.value = true;
        stream.value.getTracks().forEach((track) => {
            track.stop();
            stream.value.removeTrack(track);
        })
        recorder.value.stop();
        recorder.value = null;
        stream.value = null;
        stateChange('rewind')
        isRecording.value = false;
        audioChunks.value = [];
        return;

        // 도전이 완료되면 수행
    } else if (recorder.value) {
        recorder.value.stop();
        recorder.value.onstop = async () => {
            const audioBlob = new Blob(audioChunks.value, { type: 'audio/wav' });
            wavBlobUrl.value = URL.createObjectURL(audioBlob);
            // await stateChange('rewind')
            reset();
            isRecording.value = false;
            audioChunks.value = [];
            store.challengefun(nowSheet.value, audioBlob);
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
    status.value = true;

    // 도전모드일때 실행되는 메서드
    if (props.challenge) {
        playChallenge.value = true;

        // 도전모드 모달 변경
        chaingingChallenge(true);
        try {
            stream.value = await navigator.mediaDevices.getUserMedia({ audio: true });
            audioCon.value = true;
            text.value = "* 도전하기를 눌러 시작하세요!\n * 노래가 끝나면 점수가 나와요!\n * '녹음중'을 누르면 다시 시작해요!";
        } catch {
            audioCon.value = false;
            text.value = "* 마이크 기능 없이 도전할 수 없어요.\n * 주소창 오른쪽의 마이크 모양을 누르세요.\n * 권한을 허용 해주세요.\n * 새로고침 F5 를 눌러주세요."
        }
        // 연습모드일때 실행되는 메서드
    } else {
        // 목록 불러오기
        await store.practiceDatafun(nowSheet.value);

        // 도전모드 모달 변경
        chaingingChallenge(false);
        playChallenge.value = false;
        audioCon.value = false;

        // 연습 갯수 불러오기
        await practiceGet();

        // 1분이 지나면 연습추가
        if (practiceToday.value.practiceCount >= 5) {
            knowledge.value = 100;
        } else {
            interval1.value = setInterval(async () => {
                if (knowledge.value < 100) {
                    knowledge.value += 1;
                } else if (knowledge.value === 100) {
                    await store.practicePostfun(nowSheet.value);
                    clearInterval(interval1.value);
                    practiceGet();
                    practiceSuccess.value = true;
                }
            }, 60)
        }
    }
});

// 떠날 때, reset
router.beforeEach((to, from) => {
    if (from.name === 'pianoPractice' || from.name === 'pianoChallenge') {
        reset();
        stopRecording(false);
    }
});

onUnmounted(() => {
    reset();
    stopRecording(false);
    clearInterval(interval1.value);
})

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

.record {
    animation: showNumber 1s forwards;
    animation-iteration-count: infinite;
}

.practiceLoading {
    background-color: #fffed28d;
    width: 200px;
}

.practiceLodingBack {
    position: absolute;
    left: 2vw;
    top: 7vh;
    width: 350px;
    display: flex;
    justify-content: space-between;

}

.strewbery {
    display: flex;
    width: 120px;
}

@keyframes showNumber {
    0% {
        opacity: 0;
    }

    50% {
        opacity: 1;
    }

    100% {
        opacity: 0;
    }
}
</style>