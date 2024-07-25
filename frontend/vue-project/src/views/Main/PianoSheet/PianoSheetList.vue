<template>
    <div>
        <h1>Piano Sheet List</h1>
        <v-card>
            <v-tabs v-model="tab" align-tabs="end" color="#D2B48C">
                <v-tab :value="1" @click="setCurrentTab('UserSheet')">
                    <span><img src="@/assets/images/tab1.png" alt=""></span>
                    <span>ㅇㅇ이의 악보</span>
                </v-tab>
                <v-tab :value="2" @click="setCurrentTab('BasicSheet')">기본 제공 악보</v-tab>
            </v-tabs>

            <v-tabs-window v-model="tab">
                <v-tabs-window-item :value="1">
                    <component :is="currentTab"></component>
                </v-tabs-window-item>
                <v-tabs-window-item :value="2">
                    <component :is="currentTab"></component>
                </v-tabs-window-item>
            </v-tabs-window>
        </v-card>
    </div>
</template>

<script>
import UserSheet from "@/components/PianoSheet/UserSheet.vue";
import BasicSheet from "@/components/PianoSheet/BasicSheet.vue";

export default {
    name: 'PianoSheetList',
    components: {
        BasicSheet,
        UserSheet
    },
    data() {
        return {
            currentTab: 'UserSheet',
            tab: 1 // 기본으로 1번째 탭 선택
        };
    },
    methods: {
        setCurrentTab(tab) {
            this.currentTab = tab;
        }
    }
}
</script>

<style scoped>
img {
    width: 30px;
    height: 40px;
}
</style>