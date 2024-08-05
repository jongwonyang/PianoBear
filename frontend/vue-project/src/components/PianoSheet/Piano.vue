<template>
    <div class="piano">
        <div class="octave" v-for="oct in octaves" :key="oct">
            <div class="blacks">
                <div class="black-note" v-for="(key, index) in blakcs.filter((key, index) =>
                    ((oct !== 0 && oct !== 8) || (oct === 0 && index > 4)))" @mousedown="keyPush(oct, key)" :key="key"
                    :class="{ hide: (key === 'N'), on: (props.currPitch?.filter((i) => ((oct + key) === i))[0]) }">
                </div>
            </div>
            <div class="whites">
                <div class="white-note" v-for="(key, index) in whites.filter((key, index) =>
                    ((oct !== 0 && oct !== 8) || (oct === 0 && index > 4) || (oct === 8 && key === 'C')))"
                    @mousedown="keyPush(oct, key)" :key="key"
                    :class="{ on: (props.currPitch?.filter((i) => ((oct + key) === i))[0]) }">
                </div>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { ref, defineProps } from 'vue';

const props = defineProps({
    currPitch: Array<String>
})
const octaves = ref([0, 1, 2, 3, 4, 5, 6, 7, 8]);
const whites = ref(['C', 'D', 'E', 'F', 'G', 'A', 'B']);
const blakcs = ref(['C#', 'D#', 'N', 'F#', 'G#', 'A#', 'N']);

const keyPush = function (oct: Number, key: String) {
    console.log(oct + " 옥타브 " + key);
}

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
    box-shadow: inset -1px -1px 2px hsla(0, 0%, 100%, .2), inset 0 -5px 2px rgba(0, 0, 0, .5), 0 2px 4px rgba(0, 0, 0, .5);
}

.octave {
    display: flex;
    position: relative;
    height: 100%;
}

.blacks {
    position: absolute;
    display: flex;
    justify-content: space-around;
    width: 100%;
    height: 60%;
    margin: auto;
    left: 8.5px;
    z-index: 1;
}

.black-note {
    background-color: black;
    width: 12px;
    height: 100%;
    cursor: pointer;
    border: 1px solid black;
    border-radius: 0 0 3px 3px;
    box-shadow: inset -1px -1px 2px hsla(0, 0%, 100%, .2), inset 0 -5px 2px rgba(0, 0, 0, .5), 0 2px 4px rgba(0, 0, 0, .5);
}

.whites {
    display: flex;
    position: relative;
    height: 80px;
    margin: auto;
}

.white-note {
    background: white;
    width: 17px;
    height: 100%;
    margin: auto;
    cursor: pointer;
    border: 1px solid black;
    border-radius: 0 0 2px 2px;
    box-shadow: inset 0 0 0 hsla(0, 0%, 100%, .8), inset -2px -5px 3px #ccc, 0 0 3px rgba(0, 0, 0, .5);
}

.hide {
    visibility: hidden;
}

.white-note:hover,
.black-note:hover {
    background-color: rgb(176, 176, 176);
}

.white-note:active,
.black-note:active {
    background-color: rgb(112, 112, 112);
}

.on {
    background-color: rgb(112, 112, 112);
}
</style>