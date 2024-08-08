<template>
    <div class="back">
        <div class="sheetback">
            <div class="practice-options">
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

            <v-sheet id="player" :elevation="1" color="#D9F6D9" :height="36" :width="140">
                <v-btn-toggle v-model="toggle_one" mandatory shaped>
                    <v-btn icon="mdi-reload" :width="27" :height="27" class="player" id="rewind" variant="text"></v-btn>
                    <v-btn icon="mdi-pause" :width="27" :height="27" class="player" id="pause" variant="text"></v-btn>
                    <v-btn icon="mdi-play" :width="27" :height="27" class="player" id="play" variant="text"></v-btn>
                </v-btn-toggle>
            </v-sheet>

            <div v-show="status" id="sheet-container" :value="status"></div>
            <div v-if="!status" class="loading">
                <img src="@/assets/characters/토니/토니악보변환.png" width="270px" />
                <h1>Now Loading...
                    <v-progress-linear color="dark-blue" indeterminate></v-progress-linear>
                </h1>
            </div>
            <Piano :curr-pitch="num" style="position: relative; margin: auto; margin-top: 2vh;" />
            <button @click="osmd('osmd-sheet', musicXml)">check</button>
            <div id="osmd-sheet"></div>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { pageLoad, sheetSelect, createPlayer, num, reset, osmd } from '@/mxlplayer/demo.mjs';
import { usePianoSheetStore } from '@/stores/pianosheet';
import Piano from './Piano.vue';
// osmdLoadCheck
const store = usePianoSheetStore();
const route = useRoute();
const router = useRouter();

const velo = ref(1);
const status = ref(false);
const toggle_one = ref(1);
const musicXml = ref();
const nowSheet = ref(route.params['id']);

window.addEventListener("keydown", (e) => {
    if (e.key === " ") {
        if (toggle_one.value == 2) {
            toggle_one.value = 1;
        } else {
            toggle_one.value = 2;
        }
    }
});

const loadMxl = async function (id) {
    musicXml.value = await store.mxlLoadfun(id);
}

onMounted(async () => {
    await pageLoad();
    await loadMxl(nowSheet.value);
    await sheetSelect(musicXml.value);
    await createPlayer();
    status.value = true;
});

router.beforeEach((to, from) => {
    if (from.name === 'pianoPractice') {
        reset();
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