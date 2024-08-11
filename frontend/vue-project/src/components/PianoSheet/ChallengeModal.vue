<template>
    <div>
        <div class="text-center pa-4">
            <v-dialog v-model="props.dialog" width="400" persistent>
                <v-card max-width="400" v-show="props.playChallenge && cardCheck" color="#FFF9E0">
                    <v-card-title class="text" prepend-icon="mdi-update">도전하기!</v-card-title>
                    <v-divider :thickness="2" class="border-opacity-25" color="#947650"></v-divider>
                    <v-card-text class="text" :style="{ whiteSpace: 'pre-line' }">
                        {{ props.text }}
                    </v-card-text>
                    <template v-slot:actions>
                        <v-btn v-if="props.audioCon" class="text" text="도전하기" @click="start()" variant="tonal"
                            color="#947650"></v-btn>
                        <v-btn class="text" text="취소"
                            @click="router.push({ name: 'pianoDetail', params: { id: route.params['id'] as string } })"
                            variant="tonal" color="#947650"></v-btn>
                    </template>
                </v-card>
                <div id="countdown" v-show="countdown">
                    <div class="countdown-number" id="number-3">3</div>
                    <div class="countdown-number" id="number-2">2</div>
                    <div class="countdown-number" id="number-1">1</div>
                    <div class="countdown-number" id="start">시작!</div>
                </div>
            </v-dialog>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { ref, defineProps, defineEmits } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const route = useRoute();
const router = useRouter();
const props = defineProps({
    playChallenge: Boolean,
    audioCon: Boolean,
    dialog: Boolean,
    text: String,
})
const emits = defineEmits([
    'changeDialog',
    'startRecord',
    'changeChallenge'
])

const countdown = ref(false);
const cardCheck = ref(true);

const start = async function () {
    cardCheck.value = false
    countdown.value = true;
    document.querySelectorAll('.countdown-number').forEach((e, i) => {
        setTimeout(() => {
            e.classList.add('show')
        }, i * 1000)
        setTimeout(() => {
            e.classList.remove('show')
        }, i * 1000 + 1000)
    })
    setTimeout(() => {
        emits('changeDialog');
        countdown.value = false;
    }, 3700)
    setTimeout(() => {
        emits('startRecord');
        cardCheck.value = true;
        countdown.value = false;
        emits('changeChallenge');
    }, 4000)
}


</script>
<style scoped>
.text {
    font-weight: bold;
    color: #947650;
}

#countdown {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 30vh;
    width: 30vw;
    font-size: 8em;
    font-weight: bold;
    color: white;
}

.countdown-number {
    position: absolute;
    opacity: 0;
    transition: opacity 0.5s ease-in-out;
    background: linear-gradient(to right, white, white);
    color: transparent;
    -webkit-background-clip: text;
    /*#FFF9E0, #947650, #FFF9E0 */
}

#number-3.show {
    animation: showNumber 1s forwards;
}

#number-2.show {
    animation: showNumber 1s forwards;
}

#number-1.show {
    animation: showNumber 1s forwards;
}

#start.show {
    animation: showNumber 1s forwards;
}

@keyframes showNumber {
    0% {
        opacity: 0;
    }

    50% {
        opacity: 1;
    }

    100% {
        opacity: 0;
    }
}
</style>