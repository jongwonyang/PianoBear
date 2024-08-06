<template>
    <div style="width: 200vh; height: 92vh;">
        <div class="practice-options">
            <!-- <div>
                {{ num[0] ? num : "loading.." }}
            </div> -->
            <v-row align="center" justify="space-around">
                <v-col>
                    <v-sheet :elevation="1" color="#D9F6D9" :height="40" :width="135"
                        style="border-radius: 20px; display: flex; align-items: center; padding:1.5vh; justify-content: space-between;">
                        <label for="velocity"> 속도 </label>
                        <input type="number" id="velocity" name="velocity" :value="velo" min="0.25" max="2" step="0.25"
                            readonly style="width: 6.5vh; position: relative; left: 1.5vh;" />
                        <v-btn icon="mdi-triangle-small-down" class="velo" @click="velo >= 0.5 ? velo -= 0.25 : null"
                            size="small" :width="15" :height="15" variant="tonal"
                            style="position:relative; top: 1.1vh; left: 1.5vh;"></v-btn>
                        <v-btn icon="mdi-triangle-small-up" class="velo" @click="velo <= 1.75 ? velo += 0.25 : null"
                            size="small" :width="15" :height="15" variant="tonal"
                            style="position:relative; top: -1.1vh; left: -0.8vh;"></v-btn>
                    </v-sheet>
                </v-col>
                <v-col>
                    <v-sheet :elevation="1" color="#D9F6D9" :height="40" :width="135"
                        style="border-radius: 20px; display: flex; align-items: center; padding:1.5vh; justify-content: space-between;">
                        <v-switch color="#6c8056" label="음소거" type="checkbox" class="player-option" id="option-mute"
                            value="mute" hide-details></v-switch>
                    </v-sheet>
                </v-col>
            </v-row>
        </div>

        <div id="player">
            <v-btn-toggle v-model="toggle_one" mandatory shaped>
                <v-btn icon="mdi-reload" :width="27" :height="27" class="player" id="rewind" variant="tonal"></v-btn>
                <v-btn icon="mdi-pause" :width="27" :height="27" class="player" id="pause" variant="tonal"></v-btn>
                <v-btn icon="mdi-play" :width="27" :height="27" class="player" id="play" variant="tonal"></v-btn>
            </v-btn-toggle>
        </div>

        <div id="sheet-container" :value="status" ref="mxlSheet">
            <div v-if="!status">
                <img src="@/assets/characters/토니/토니악보변환.png" width="250vh"
                    style="position: relative; top: 14vh; left: 76vh;" />
                <h2 style="position: relative; top: 18vh; left: 82vh; text-align: center;">Now Loading...
                    <v-progress-linear color="dark-blue" indeterminate></v-progress-linear>
                </h2>
            </div>
        </div>
        <Piano :curr-pitch="num" style="margin:auto; margin-top: 7vh;" />
    </div>

</template>

<script setup>
import { onMounted, ref } from 'vue';
import { pageLoad, sheetSelect, createPlayer, num } from '@/mxlplayer/demo.mjs';
import Piano from './Piano.vue';
const musicXml = "https://public.sgr.cspark.kr/SSAFY/musicxml/let-it-go.musicxml"
// const musicXml = "https://public.sgr.cspark.kr/SSAFY/musicxml/SchbAvMaSample.musicxml"
const velo = ref(1);
const mxlSheet = ref(null);
const status = ref(false);
const toggle_one = ref(1);
window.addEventListener("keydown", (e) => {
    if (e.key === " ") {
        if (toggle_one.value == 2) {
            toggle_one.value = 1;
        } else {
            toggle_one.value = 2;
        }
    }
});

onMounted(async () => {
    await pageLoad();
    await sheetSelect(musicXml);
    await createPlayer();
    status.value = true;
    toggle_one.value = playStatus.value;
});

</script>

<style scoped>
#sheet-container {
    height: 70vh;
    margin: 0 0 0 5vh;
    border: 1px solid black;
}

#player {
    bottom: 95vh;
    width: 16vh;
    margin: auto;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.player {
    font-size: larger;
    margin: 5px;
    height: 35px;
    width: 35px;
}

.practice-options {
    position: relative;
    text-align: right;
    width: 42vh;
    left: 150vh;
    top: 5vh;
    z-index: 0;
    color: #6c8056
}
</style> -->