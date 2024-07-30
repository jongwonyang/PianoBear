<template>
    <div class="card">
        <v-tabs-window v-model="tab">
            <v-tabs-window-item :value="1">
                <div>
                    <div class="text">
                        <h2>PDF로 악보 만들기</h2>
                    </div>
                    <div class="container">
                        <div class="component">
                            <PDFtoMXL />
                        </div>
                        <div class="trans">
                            <ConvertPDF />
                        </div>
                        <div class="component">
                            <ConvertedFile />
                        </div>
                    </div>
                </div>
            </v-tabs-window-item>
            
            <v-tabs-window-item :value="2">
                <div>
                    <div class="text">
                        <h2>AI로 악보 만들기</h2>
                    </div>
                    <div class="container">
                        <div class="component">
                            <AItoMXL />
                        </div>
                        <div class="trans">
                            <ConvertAI />
                        </div>
                        <div class="component">
                            <ConvertedFile />
                        </div>
                    </div>
                </div>
            </v-tabs-window-item>
        </v-tabs-window>

        <div class="tab">
            <v-tabs v-model="tab" align-tabs="center" color="#D2B48C" hide-slider height="40px">
                <v-tab :value="1" @click="setCurrentTab('UserSheet')">
                    <h2>PDF -> MXL</h2>
                </v-tab>
                <v-tab :value="2" @click="setCurrentTab('BasicSheet')">
                    <h2>음성 파일 -> MXL</h2>
                </v-tab>
            </v-tabs>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import AItoMXL from "@/components/PianoSheet/AItoMXL.vue"
import PDFtoMXL from "@/components/PianoSheet/PDFtoMXL.vue"
import ConvertedFile from "@/components/PianoSheet/ConvertedFile.vue"
import ConvertAI from "@/components/PianoSheet/ConvertAI.vue"
import ConvertPDF from "@/components/PianoSheet/ConvertPDF.vue"

const tab = ref(1);
const currentTab = ref('UserSheet');

const setCurrentTab = (tabName) => {
  currentTab.value = tabName;
};
</script>

<style scoped>
.container {
  display: flex; /* Flexbox 활성화 */
  justify-content: space-between; /* 컴포넌트 사이의 간격을 자동으로 조절 */
  align-items: flex-start; /* 세로 방향으로 상단 정렬 */
}

.component{
    margin-left: 50px;
    margin-right: 50px;
}

.text{
    text-align: center;
    color: #947650;
    font-size: large;
    margin-bottom: 20px;
}

.card{
    margin-left: 100px;
}

.tab{
    margin-top: 30px;
}

.trans{
    margin-top: 180px;
}
</style>