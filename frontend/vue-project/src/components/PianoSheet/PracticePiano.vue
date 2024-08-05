<template>
    <div style="width: 200vh; height: 92vh;">
        <div class="practice-options">
            <div>
                {{ num[0] ? num : "loading.." }}
            </div>
            <div>
                <label for="option-mute">음소거 </label>
                <input type="checkbox" class="player-option" id="option-mute" value="mute" />
            </div>
            <div>
                <label for="velocity">속도 : </label>
                <input type="number" id="velocity" name="velocity" :value="velo" min="0.25" max="2" step="0.25" readonly
                    style="width: 6vh;" />
                <v-btn class="velo" @click="velo <= 1.75 ? velo += 0.25 : null" width="50"> up</v-btn>
                <v-btn class="velo" @click="velo >= 0.5 ? velo -= 0.25 : null" width="50"> down</v-btn>
            </div>
        </div>

        <div id="player">
            <button class="player" id="rewind">⏮</button>
            <button class="player" id="pause">⏸</button>
            <button class="player" id="play">▶</button>
        </div>

        <div id="sheet-container"></div>
        <Piano :curr-pitch="num" style="margin:auto;" />
    </div>

</template>

<script setup>
import { onMounted, ref } from 'vue';
// import { pageLoad, sheetSelect, createPlayer, num } from '@/mxlplayer/demo.mjs';
import Piano from './Piano.vue';
// const musicXml = "https://public.sgr.cspark.kr/SSAFY/let-it-go.musicxml"
const musicXml = "https://public.sgr.cspark.kr/SSAFY/musicxml/SchbAvMaSample.musicxml"
const velo = ref(1);

onMounted(async () => {
    await pageLoad();
    await sheetSelect(musicXml);
    await createPlayer();
});

</script>

<style scoped>
#sheet-container {
    height: 60vh;
    padding-bottom: 130px;
    margin: 0 0 5vh 5vh;
    border: 1px solid black;
}

#player {
    position: fixed;
    bottom: 22vh;
    width: 100%;
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
    width: 51vh;
    left: 147vh;
    top: 14vh;
    z-index: 0;
}
</style>