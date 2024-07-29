<template>
    <div class="piano">
        <div class="octave" v-for="oct in octaves" :key="oct">
            <div class="blacks">
                <div class="black" v-for="(key, index) in blakcs.filter((key, index) => ((oct !== 0 && oct !== 8) || (oct === 0 && key === 'A#')))" :key="key" :class="{hide : (key === 'N')}">
                </div>
            </div>
            <div class="whites">
                <div class="white" v-for="(key, index) in whites.filter((key, index) => ((oct !== 0 && oct !== 8) || (oct === 0 && index > 4) || (oct === 8 && key === 'C')))" :key="key">
                </div>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
    import {ref} from 'vue';

    const octaves = ref([0, 1, 2, 3, 4, 5, 6, 7, 8]);
    const whites = ref(['C', 'D', 'E', 'F', 'G', 'A', 'B']);
    const blakcs = ref(['C#', 'D#','N', 'F#', 'G#', 'A#', 'N']);

</script>
<style scoped>
.piano {
    display: flex;
    justify-content: center;
    background-color: black;
    width: 915px;
    height: 95px;
    padding-top: 13px;
    border-radius: 5px;
}
.blacks {
    position: absolute;
    display: flex;
    justify-content: space-evenly;
    width: 100%;
    height: 60%;
    margin: auto;
    z-index: 1;
}
.black {
    width: 12px;
    height: 100%;
    background-color: black;
    border: 1px solid black;
    border-radius: 2px;
}
.whites {
    display: flex;
    position: relative;
    height: 80px;
    margin: auto;
}
.octave {
    display: flex;
    position: relative;
    height: 100%;
}
.white {
    background-color: white;
    width: 17px;
    height: 100%;
    margin: auto;
    border: 1px solid black;
}
.hide {
    visibility: hidden;
}
.white:hover, .black:hover {
    background-color: rgb(176, 176, 176);
}
.white:active, .black:active {
    background-color: rgb(112, 112, 112);
}
</style>