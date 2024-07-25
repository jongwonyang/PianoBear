<template>
    <div>
        <div class="surface">
            <md-elevation></md-elevation>
            <div class="controller">

                <md-filled-tonal-icon-button v-if="!videoCheck" @click="turnOnVideo">
                    <md-icon>videocam_off</md-icon>
                </md-filled-tonal-icon-button>
                <md-filled-icon-button v-else @click="turnOnVideo">
                    <md-icon>videocam</md-icon>
                </md-filled-icon-button>

                <md-filled-tonal-icon-button v-if="!audioCheck" @click="turnOnAudio">
                    <md-icon>mic_off</md-icon>
                </md-filled-tonal-icon-button>
                <md-filled-icon-button v-else @click="turnOnAudio">
                    <md-icon>mic</md-icon>
                </md-filled-icon-button>

                <md-filled-tonal-icon-button v-if="!emoteCheck" @click="switchEmote">
                    <md-icon>sentiment_satisfied</md-icon>
                </md-filled-tonal-icon-button>
                <md-filled-icon-button v-else @click="switchEmote">
                    <md-icon>sentiment_satisfied</md-icon>
                </md-filled-icon-button>

                <md-filled-tonal-icon-button v-if="!playCheck" @click="switchPlay">
                    <md-icon>music_note</md-icon>
                </md-filled-tonal-icon-button>
                <md-filled-icon-button v-else @click="switchPlay">
                    <md-icon>music_note</md-icon>
                </md-filled-icon-button>


                <md-filled-tonal-icon-button v-if="!inviteCheck" @click="switchInvite">
                    <md-icon>group_add</md-icon>
                </md-filled-tonal-icon-button>
                <md-filled-icon-button v-else @click="switchInvite">
                    <md-icon>group_add</md-icon>
                </md-filled-icon-button>

                <md-filled-tonal-button @click="exit">
                    나가기
                </md-filled-tonal-button>
            </div>
        </div>
        <div class="emote">

        </div>
    </div>
</template>
<script lang="ts" setup>
    import {onMounted, ref} from 'vue';
    import {useRouter, useRoute} from 'vue-router';

    const router = useRouter();
    const route = useRoute();
    const videoCheck = ref(false);
    const audioCheck = ref(false);
    const emoteCheck = ref(false);
    const playCheck = ref(false);
    const inviteCheck = ref(false);
    const master = ref(false);

    const turnOnVideo = function() {
        if(!videoCheck.value) {
            videoCheck.value = true;
        }else {
            videoCheck.value = false;
        }
    }

    const turnOnAudio = function() {
        if (!audioCheck.value) {
            audioCheck.value = true;
        }else {
            audioCheck.value = false;
        }
    }

    const switchEmote = function() {
        if (!emoteCheck.value) {
            emoteCheck.value = true;
        }else {
            emoteCheck.value = false;
        }
    }

    const switchPlay = function() {
        if (!playCheck.value) {
            playCheck.value = true;
        }else {
            playCheck.value = false;
        }
    }

    const switchInvite = function() {
        if (!inviteCheck.value) {
            inviteCheck.value = true;
        }else {
            inviteCheck.value = false;
        }
    }

    const exit = function() {
        if (master.value) {
            router.push({name: 'community'});
        }else {
            router.push({name: 'communityJoin', params: {id: route.query.value as string}});
        }
    }

    
</script>
<style scoped>
.surface {
    position: fixed;
    border-radius: 16px;
    height: 64px;
    width: 600px;
    --md-elevation-level: 5;
    margin: auto;
    background-color: #FFF8D8;
    bottom: 2%;
    left: 30%;
}
.controller {
    display : flex;
    justify-content: space-evenly;
    align-items: center;
    transform: translate(0, 30%);
}
</style>