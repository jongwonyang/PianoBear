<template>
    <div class="back">
        <div class="sheetback">
            <div class="preBack">
                <button class="cursor-pointer duration-200 hover:scale-125 active:scale-100" title="Go Back"
                    @click="router.push(`/main/piano-sheet/${nowSheet}`)">
                    <svg xmlns="http://www.w3.org/2000/svg" width="40px" height="40px" viewBox="0 0 24 24"
                        class="goBack">
                        <path stroke-linejoin="round" stroke-linecap="round" stroke-width="1.5"
                            d="M11 6L5 12M5 12L11 18M5 12H19">
                        </path>
                    </svg>
                </button>
            </div>
            <div class="practice-options" :style="{ visibility: props.challenge ? 'hidden' : 'visible' }">
                <v-sheet :elevation="1" color="#D9F6D9" :height="40" :width="135" class="setOption">
                    <v-btn prepend-icon="mdi-reload" :width="27" :height="27" class="changeSheet" variant="text"
                        @click="changeSheet()">{{ isBaby ? "원본악보" : "변환악보" }}</v-btn>
                </v-sheet>
                <v-sheet :elevation="1" color="#D9F6D9" :height="40" :width="135" class="setOption">
                    <label for="velocity"> 속도 </label>
                    <input type="number" id="velocity" name="velocity" :value="velo.toFixed(1)" min="0.5" max="2"
                        step="0.1" readonly style="width: 50px; position: relative; left: 5px;" />

                    <div class="btns">
                        <v-btn icon="mdi-triangle-small-up" class="velo" @click="velo < 2 ? velo += 0.1 : null"
                            size="small" :width="15" :height="15" variant="tonal"></v-btn>
                        <v-btn icon="mdi-triangle-small-down" class="velo" @click="velo > 0.51 ? velo -= 0.1 : null"
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
                    <v-btn icon="mdi-rewind" :width="27" :height="27" class="player" id="rewind" variant="text"
                        @click="stateChange('rewind')"></v-btn>
                    <v-btn icon="mdi-pause" :width="27" :height="27" class="player" id="pause" variant="text"
                        @click="stateChange('pause')"></v-btn>
                    <v-btn icon="mdi-play" :width="27" :height="27" class="player" id="play" variant="text"
                        @click="stateChange('play')"></v-btn>
                </v-btn-toggle>
            </v-sheet>
            <v-sheet v-if="props.challenge" id="player" :elevation="1" color="#D9F6D9" :height="40" :width="140">
                <v-btn v-show="isRecording" prepend-icon="mdi-circle" :width="125" :height="32" class="player record"
                    id="rewind" variant="text" @click="stopRecording(false)">
                    <template v-slot:prepend>
                        <v-icon color="red"></v-icon>
                    </template>
                    녹음중..
                </v-btn>
                <v-btn v-show="!isRecording" prepend-icon="mdi-play" :width="125" :height="32" class="player" id="play"
                    variant="text">도전하기</v-btn>
            </v-sheet>
            <div v-if="!props.challenge && status" class="practiceLodingBack">
                <div class="practiceLoading">
                    <v-progress-linear v-model="knowledge" height="31" color="#D9F6D9" class="practiceLoading">
                        <strong>연습량 {{ Math.ceil(knowledge) }}%</strong>
                    </v-progress-linear>
                </div>
                <v-sheet :elevation="1" color="#D9F6D9" :height="36" class="strewbery">
                    <img v-for="n in 5" :key="n"
                        :src="(practiceToday && n <= practiceToday.practiceCount) ? '/가득찬딸기.png' : '/빈딸기.png'" :alt="n"
                        :class="(!practiceToday && n === 1) || (practiceToday && n === practiceToday.practiceCount + 1) ? 'practice-image curr-image' : 'practice-image'" />
                    <v-btn icon="mdi-check-bold" :width="25" size="small" :height="25"
                        :style="[{ 'margin-left': '10px' }, isPractice ? {} : { visibility: 'hidden' }]" color="success"
                        @click="doPractice"></v-btn>
                </v-sheet>
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
import { pageLoad, sheetSelect, createPlayer, num, reset, chaingingChallenge, stateChange, isMidiStop, currPitch, playingPiano } from '@/mxlplayer/demo.mjs';
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
const velo = ref(1.0);
const status = ref(false);
const toggle_one = ref(1);
const musicXml = ref();
const nowSheet = ref(route.params['id']);
const playChallenge = ref();
const isBaby = ref(false);
const isPractice = ref(false);

// record 변수
const isRecording = ref(false);
const recorder = ref();
const audioChunks = ref([]);
const stream = ref();
const dialog = ref(true);
const today = new Date();
const date = (today.getFullYear().toString() + (today.getMonth() + 1).toString() + today.getDate().toString())

// 모달 변수
const text = ref("\n\n* 도전하기를 위해 마이크 권한을 허용해주세요.");
const audioCon = ref(false);
const afterChallenge = ref(false);
const interval1 = ref();
const interval2 = ref();
const practiceToday = computed(() => {
    return store.practiceData.filter((e) =>
        e.practiceDate[0].toString() + e.practiceDate[1].toString() + e.practiceDate[2].toString() === date
    )[0]
})


// 연습 변수
const practiceGet = function () {
    store.practiceDatafun(nowSheet.value)
};

// 악보변환
const changeSheet = async function () {
    if (isBaby.value) {
        status.value = false;
        isBaby.value = false;
    } else {
        status.value = false;
        isBaby.value = true;
    }
    await loadMxl(nowSheet.value);
    await sheetSelect(musicXml.value);
    await createPlayer(props.challenge);
    status.value = true;
}

// space-bar 로 재생, 일시정지 설정
window.addEventListener("keydown", (e) => {
    e.stopPropagation();
    if (status.value) {
        if (e.key === " " && !props.challenge) {
            if (toggle_one.value == 2) {
                toggle_one.value = 1;
            } else {
                toggle_one.value = 2;
            }
        } else if (e.key === "s" && !props.challenge) {
            playingPiano("C", 4);
        } else if (e.key === "d" && !props.challenge) {
            playingPiano("D", 4);
        } else if (e.key === "f" && !props.challenge) {
            playingPiano("E", 4);
        } else if (e.key === "g" && !props.challenge) {
            playingPiano("F", 4);
        } else if (e.key === "h" && !props.challenge) {
            playingPiano("G", 4);
        } else if (e.key === "j" && !props.challenge) {
            playingPiano("A", 4);
        } else if (e.key === "k" && !props.challenge) {
            playingPiano("B", 4);
        } else if (e.key === "l" && !props.challenge) {
            playingPiano("C", 5);
        }
    }
});

// mxl 불러오기
const loadMxl = async function (id) {
    if (isBaby.value) {
        musicXml.value = await store.mxlModifiedLoadfun(id);
    } else {
        musicXml.value = await store.mxlLoadfun(id);
    }
}

// 녹음 재생
async function startRecording() {
    interval2.value = setInterval(() => {
        if (isMidiStop()) {
            stopRecording(true);
            afterChallenge.value = true;
        }
    }, 500)
    stateChange('play')
    stream.value = await navigator.mediaDevices.getUserMedia({ audio: true });
    recorder.value = new MediaRecorder(stream.value);
    recorder.value.ondataavailable = (e) => {
        audioChunks.value.push(e.data);
    };
    recorder.value.start();
    isRecording.value = true;
}

// 녹음 중지
async function stopRecording(isSuccess) {

    // 도전 완료정보 초기화
    clearInterval(interval2.value);

    // 도전을 완료하기 전 수행.
    if (!isSuccess) {
        dialog.value = true;
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
            store.challengefun(nowSheet.value, audioBlob);
            // await stateChange('rewind')
            isRecording.value = false;
            audioChunks.value = [];
        };
    }
}

const changeDialog = function () {
    dialog.value = !dialog.value;
}

// 연습 완료하기
const doPractice = async function (e) {
    createFirework(e);
    if (!practiceToday.value || practiceToday.value.practiceCount < 4) {
        await store.practicePostfun(nowSheet.value);
        practiceGet();
        isPractice.value = false;
        knowledge.value = 0;
        interval1.value = setInterval(async () => {
            if (knowledge.value < 100) {
                knowledge.value += 1;
            } else if (knowledge.value === 100) {
                isPractice.value = true;
                clearInterval(interval1.value);
            }
        }, 300)
    } else if (practiceToday.value.practiceCount === 4) {
        await store.practicePostfun(nowSheet.value);
        practiceGet();
        isPractice.value = false;
    }
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
            text.value = "\n* 도전하기를 눌러 시작하세요!\n * 노래가 끝나면 점수가 나와요!\n * '녹음중'을 누르면 다시 시작해요!";
        } catch {
            audioCon.value = false;
            text.value = "* 마이크 기능 없이 도전할 수 없어요.\n * 주소창의 왼쪽 버튼을 누르세요.\n * 마이크 권한을 허용 해주세요.\n * 새로고침 F5 를 눌러주세요."
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
        practiceGet();

        // 1분이 지나면 연습추가
        if (practiceToday.value && practiceToday.value.practiceCount >= 5) {
            knowledge.value = 100;
        } else {
            interval1.value = setInterval(async () => {
                if (knowledge.value < 100) {
                    knowledge.value += 1;
                } else if (knowledge.value === 100) {
                    isPractice.value = true;
                    clearInterval(interval1.value);
                }
            }, 300)
        }
    }
});

// 떠날 때, reset
router.beforeEach((to, from) => {
    if (from.name === 'pianoPractice' || from.name === 'pianoChallenge') {
        reset();
        stopRecording(false);
        status.value = false;
    }
});

onUnmounted(() => {
    status.value = false;
    reset();
    stopRecording(false);
    clearInterval(interval1.value);
});

// 완료 이펙트
const createFirework = () => {
    const currImg = document.querySelector('.curr-image');
    const numParticles = 60;
    const buttonRect = currImg.getBoundingClientRect();
    const buttonX = buttonRect.left + buttonRect.width / 2;
    const buttonY = buttonRect.top + buttonRect.height / 2;

    const bigStrew = document.createElement('img');
    bigStrew.classList.add('bigStrew');
    bigStrew.src = '/가득찬딸기.png';
    bigStrew.alt = '큰딸기';
    bigStrew.style.left = `${buttonX}px`;
    bigStrew.style.top = `${buttonY}px`;
    document.body.appendChild(bigStrew);
    bigStrew.addEventListener('animationend', () => {
        bigStrew.remove();
    });
    for (let i = 0; i < numParticles; i++) {
        const particle = document.createElement('div');
        particle.classList.add('particle');

        // 각 파티클의 방향과 거리
        const angle = Math.random() * 360;
        const radius = Math.random() * 300;

        // --x, --y 변수를 사용하여 방향과 거리 설정
        particle.style.setProperty('--x', `${radius * Math.cos(angle)}px`);
        particle.style.setProperty('--y', `${radius * Math.sin(angle)}px`);

        if (i < 14) {
            particle.style.setProperty('background-color', 'red');
        } else if (i < 28) {
            particle.style.setProperty('background-color', 'yellow');
        } else if (i < 42) {
            particle.style.setProperty('background-color', 'rgb(78, 131, 255)');
        }

        // 파티클 위치 설정
        particle.style.left = `${buttonX}px`;
        particle.style.top = `${buttonY}px`;

        document.body.appendChild(particle);

        // 애니메이션이 끝난 후 파티클 제거
        particle.addEventListener('animationend', () => {
            particle.remove();
        });
    }
};
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
    margin-top: 5%;
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
    height: 100%;
    border-radius: 5px;
}

.practiceLodingBack {
    position: absolute;
    left: 1vw;
    top: 5.5vh;
    width: 420px;
    display: flex;
    justify-content: space-between;
    align-items: center;

}

.strewbery {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 200px;
    border-radius: 20px;
}

.changeSheet {
    font-size: medium;
    font-weight: bold;
    margin-left: 25px;
}

.goBack {
    stroke: black;
}

.preBack {
    position: absolute;
    top: 7vh;
    left: -3vw;
}

.practice-image {
    width: 30px;
    height: 30px;
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