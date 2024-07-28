<template>
    <div>
        <div>
            <v-container>
                <v-row>
                <!-- 첫 번째 div: v-menu -->
                <v-col cols="auto">
                    <v-menu location="start">
                    <template v-slot:activator="{ props }">
                        <div class="a">
                            <v-btn color="" dark v-bind="props" height="40px">정렬</v-btn>
                        </div>
                    </template>
                    <v-list>
                        <v-list-item v-for="(item, index) in items" :key="index">
                        <v-list-item-title>{{ item.title }}</v-list-item-title>
                        </v-list-item>
                    </v-list>
                    </v-menu>
                </v-col>

                <!-- 두 번째 div: v-text-field -->
                <v-col cols="auto">
                    <v-card class="mx-auto" color="#FFFFF8" width="400px" elevation="0">
                    <v-card-text>
                        <v-text-field
                            :loading="loading"
                            append-inner-icon="mdi-magnify"
                            density="compact"
                            label="Search templates"
                            variant="solo"
                            hide-details
                            single-line
                            @click:append-inner="onClick"
                        ></v-text-field>
                    </v-card-text>
                    </v-card>
                </v-col>
                </v-row>
            </v-container>

            <div class="card">
                <v-card color="#FFFFF8" elevation="0">
                    <v-tabs v-model="tab" align-tabs="start" color="#D2B48C" hide-slider height="40px">
                        <v-tab :value="1" @click="setCurrentTab('UserSheet')">
                            <span><img src="@/assets/characters/토니/토니머리.png" alt=""></span>
                            <span>ㅇㅇ이의 악보</span>
                        </v-tab>
                        <v-tab :value="2" @click="setCurrentTab('BasicSheet')">
                            <span><img src="@/assets/characters/피치/피치머리.png" alt=""></span>
                            <span>기본 제공 악보</span>
                        </v-tab>
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
        </div>
    </div>
    <div class="upload">
        <router-link to="/main/piano-sheet/upload">
            <v-btn variant="outlined" height="60px">
                <v-icon prepend>mdi-book-plus-outline</v-icon>
                악보 업로드
            </v-btn>
        </router-link> 
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
            tab: 1,
            items: [
                { title: '연습량 순' },
                { title: '가나다 순' },
                { title: '악보등록 순' },
                { title: '즐겨찾기 순' },
            ],
            loaded: false,
            loading: false
        };
    },
    methods: {
        setCurrentTab(tab) {
            this.currentTab = tab;
        },
        onClick() {
            this.loading = true

            setTimeout(() => {
                this.loading = false
                this.loaded = true
            }, 2000)
        }
    }
}
</script>

<style scoped>
img {
    width: 40px;
    height: 40px;
}

.a{
    margin-top: 17px;
}

.upload{
    margin-top: 10px;
    float: right;
}

a{
    text-decoration:none; 
    color: #D2B48C;
}
</style>