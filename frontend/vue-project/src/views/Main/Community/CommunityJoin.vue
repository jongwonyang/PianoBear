<template>
    <div>
        <div style="position:relative;">
            <div class="bear-frame">
                <Bear class="bear"/>
            </div>
            <div class="video-frame">
                <Video class="video"/>
            </div>
            <div class="btn">
                <md-elevated-button @click="join">참가</md-elevated-button>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import {ref} from 'vue';
import {useRouter, useRoute} from 'vue-router';
import Video from '@/components/Community/Video.vue';
import Bear from '@/components/Community/Bear.vue';

const router = useRouter();
const route = useRoute();
const createCheck = ref(false);

const join = function() {
    createCheck.value = true;
    console.log(route);
    router.push({name:'communiting', params:{id: route.query.value as string}})
}


</script>
<style scoped>
.btn {
    position: absolute;
    margin-left: 70%;
    width: 200px;
    height: 100px;
}
.bear-frame {
    width: 100%;
    height: 700px;
    margin-top: 5%;
    z-index: -1;
}
.bear {
    position: relative;
    width: 697px;
    height: 700px;
    margin: auto;
}
.video-frame {
    position: absolute;
    width: 100%;
    height: 430px;
    top : 170px;
    z-index: 1;
}
.video {
    position: relative;
    margin: auto;
    width: 410px;
}
</style>