<template>
    <div>
        <video ref="videoEl" autoplay height="500px"></video>
        <v-sheet class="surface" :elevation="5" rounded="lg">
            <div class="controller">
                <v-btn icon="mdi-video-off" v-if="!videoCheck" @click="turnOnVideo" />
                <v-btn icon="mdi-video"  v-else @click="turnOnVideo" variant="tonal" />
                <v-btn icon="mdi-microphone-off" v-if="!audioCheck" @click="turnOnAudio" />
                <v-btn icon="mdi-microphone" v-else  @click="turnOnAudio" variant="tonal" />
                <v-btn prepend-icon="mdi-play" v-if="!play" @click="playing">테스트</v-btn>
                <v-btn prepend-icon="mdi-pause" v-else @click="playing" variant="tonal">테스트</v-btn>
            </div>
        </v-sheet>
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
        if (videoEl.value) {
            if(!videoCheck.value) {
                videoCheck.value = true;
                navigator.mediaDevices.getUserMedia(constraints.value).then((stream) => {
                    (videoEl.value as HTMLVideoElement).srcObject = stream;
                });
            }else {
                videoCheck.value = false;
                navigator.mediaDevices.getUserMedia(constraints.value).then((stream : MediaStream) => {
                    (videoEl.value as HTMLVideoElement).srcObject = stream;
                });
            }
        }
    }

    const turnOnAudio = function() {
        if (!audioCheck.value) {
            audioCheck.value = true;
            navigator.mediaDevices.getUserMedia(constraints.value).then((stream) => {
                (videoEl.value as HTMLVideoElement).srcObject = stream;
            });
        }else {
            audioCheck.value = false;
            navigator.mediaDevices.getUserMedia(constraints.value).then((stream) => {
                (videoEl.value as HTMLVideoElement).srcObject = stream;
        });
        }
    }

    const playing = function() {
        play.value = !play.value;
    }



    onMounted(() => {
        if (!videoEl.value) return;
        navigator.mediaDevices.getUserMedia(constraints.value).then((stream) => {
            (videoEl.value as HTMLVideoElement).srcObject = stream;
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
    height: 64px;
    width: 300px;
    margin: auto;
    background-color: #FFF8D8;
}
.controller {
    display : flex;
    justify-content: space-evenly;
    align-items: center;
    transform: translate(0, 15%);
}

</style>
  