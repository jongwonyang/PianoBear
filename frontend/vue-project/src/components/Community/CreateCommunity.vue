<template>
    <div class="back">
    </div>
    <div class="create-form">
        <v-sheet class="surface" :elevation="5" rounded="lg">
            <div class="create-input">
                <p>방 만들기</p>
                <md-outlined-text-field label="방 제목" v-model="roomSetting.title">
                </md-outlined-text-field>
                <md-outlined-text-field label="인원" value="6" disabled v-model="roomSetting.num">
                </md-outlined-text-field>
                <md-outlined-text-field label="초대 메시지" v-model="roomSetting.message">
                </md-outlined-text-field>
                <md-outlined-text-field label="설명" v-model="roomSetting.text">
                </md-outlined-text-field>
            </div>
            <div class="btns">
                <v-btn @click="router.push({name:'communiting', params:{id: join}})" :width="130" :height="45" rounded="lg">생성하기</v-btn>
                <v-btn @click="emit('exit')" :width="130" :height="45" rounded="lg">나가기</v-btn>
            </div>
        </v-sheet>
    </div>
</template>
<script lang="ts" setup>
import {useRouter} from 'vue-router';
import {ref, defineEmits} from 'vue';

const join = ref(1);
const router = useRouter();
const emit = defineEmits([
    'exit'
]);

interface RoomSetting {
    title: string;
    num: number;
    message: string;
    text: string;
}

const roomSetting = ref<RoomSetting>({
    title: '',
    num: 6,
    message: '',
    text: ''
});

</script>

<style scoped>
.back {
    position: fixed;
    background-color: gray;
    width: 100%;
    height: 100%;
    top: 0px;
    left: 0px;
    z-index:99;
    opacity: 0.7;
}

.surface {
    position: relative;
    border-radius: 16px;
    height: 700px;
    width: 700px;
    transition-duration: 250ms;
    transition-timing-function: ease-in-out;
    background-color: #FFF8D8;
    margin: auto;
    opacity: 1;

    --md-elevation-level: 3;
}

.create-input * {
    width: 80%;
    margin: 20px;
}

.create-input {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 50px 0px;
    text-align: center;
}

.btns {
    display: flex;
    justify-content: space-between;
    width: 270px;
    margin: auto;
}

.create-form{
    position:fixed;
    top: 20px;
    left: 0px;
    width: 100%;
    z-index: 100;
}


</style>