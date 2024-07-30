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
                <Create  @exit="exit"/>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import {RouterView} from 'vue-router';
import {ref} from 'vue';
import Video from '@/components/Community/Video.vue';
import Bear from '@/components/Community/Bear.vue';
import Create from '@/components/Community/CreateCommunity.vue';

const createCheck = ref(false);

const exit = function() {
    createCheck.value = false;
}

</script>
<style scoped>
.btn {
    position: absolute;
    margin-left: 100%;
    width: 250px;
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