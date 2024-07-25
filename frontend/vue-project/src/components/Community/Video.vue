<template>
    <div>
        <video ref="videoEl" autoplay height="500px"></video>
        <div class="surface">
            <md-elevation></md-elevation>
            <div class="controller">
                <md-filled-icon-button toggle @click="turnOnVideo">
                    <md-icon v-if="videoCheck">videocam</md-icon>
                    <md-icon v-else>videocam_off</md-icon>
                </md-filled-icon-button>
                <md-filled-icon-button toggle @click="turnOnAudio">
                    <md-icon v-if="audioCheck">mic</md-icon>
                    <md-icon v-else>mic_off</md-icon>
                </md-filled-icon-button>
                <md-filled-tonal-button toggle @click="playing">
                    <md-icon v-if="play">play_arrow</md-icon>
                    <md-icon v-else>pause</md-icon>
                    테스트
                </md-filled-tonal-button>
            </div>
        </div>
    </div>

</template>

<script lang="ts" setup>
    import {onMounted, ref} from "vue";

    const videoEl = ref<HTMLVideoElement | null>(null);
    const videoCheck = ref(false);
    const audioCheck = ref(false);
    const play = ref(false);
    const constraints = ref({
        video: videoCheck,
        audio: audioCheck
    });


    const turnOnVideo = function() {
        if(!videoCheck.value) {
            videoCheck.value = true;
            navigator.mediaDevices.getUserMedia(constraints.value).then((stream) => {
                console.log(stream)
                videoEl.value.srcObject = stream;
            });
        }else {
            videoCheck.value = false;
            navigator.mediaDevices.getUserMedia(constraints.value).then((stream) => {
            console.log(stream)
            videoEl.value.srcObject = stream;
        });
        }
    }

    const turnOnAudio = function() {
        if (!audioCheck.value) {
            audioCheck.value = true;
            navigator.mediaDevices.getUserMedia(constraints.value).then((stream) => {
                videoEl.value.srcObject = stream;
            });
        }else {
            audioCheck.value = false;
            navigator.mediaDevices.getUserMedia(constraints.value).then((stream) => {
            videoEl.value.srcObject = stream;
        });
        }
    }

    const playing = function() {
        play.value = !play.value;
    }



    onMounted(() => {
        if (!videoEl.value) return;
        navigator.mediaDevices.getUserMedia(constraints.value).then((stream) => {
            videoEl.value.srcObject = stream;
        });
    });
    
</script>

<style scoped>
video {
    position: relative;
    border: 1px solid black;
    width: 409px;
    height: 307px;
    transform: rotateY(180deg);
    -webkit-transform: rotateY(180deg);
    -moz-transform: rotateY(180deg);
    border-radius: 50px;
    margin-bottom: 50px;
}
.surface {
    position: relative;
    border-radius: 16px;
    height: 64px;
    width: 300px;
    --md-elevation-level: 5;
    margin: auto;
    background-color: #FFF8D8;
}
md-filled-tonal-button {
    align-content: center;
}
.controller {
    display : flex;
    justify-content: space-evenly;
    align-items: center;
    transform: translate(0, 15%);
}

</style>
  