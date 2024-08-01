<template>
    <!--pdf로 mxl만드는 로직 사용-->
    <div class="container">
        <!-- <div class="box"> 
            <div class="icon">
                <v-icon>mdi-book-plus-outline</v-icon>
            </div>
            <div class="text">
                <h4>파일을 선택하거나 끌어다 놓으세요.</h4>
            </div>
        </div> -->
        <div class="filebox">
            <label for="file"><v-icon>mdi-file-plus-outline</v-icon></label> 
            <h4>파일을 선택하거나
                끌어다 놓으세요.</h4>
            <input class="upload-name" value="첨부파일" placeholder="첨부파일">
            <input type="file" id="file" @change="onFileChange">
        </div>
        <!-- <div class="box">
            <v-file-input
                label="악보를 선택하세요."
                bg-color="#FFFFF8"
                base-color="#947650"
                width="250px"
            ></v-file-input>
        </div> -->
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { usePianoSheetStore } from "@/stores/pianosheet";

const store = usePianoSheetStore();

const onFileChange = (event: Event): void => {
    const target = event.target as HTMLInputElement;
    if (target.files) {
        store.setSelectedFile(target.files[0]);
        console.log("파일 업로드 됨!")
    }
    else {
        store.setSelectedFile(null);
    }
};
</script>

<style scoped>
.container{
    border: 2px solid #F5E5D1;
    color: #947650;
    width: 350px;
    height: 550px;
    padding-left: 70px;
    padding-right: 70px;
    padding-top: 200px;
    padding-bottom: 200px;
    font-size: large;
}

.filebox .upload-name {
    display: inline-block;
    height: 40px;
    /* padding: 0 10px; */
    vertical-align: middle;
    /* border: 1px solid #dddddd; */
    width: 78%;
    color: #999999;
}

.filebox label {
    display: inline-block;
    /* padding: 10px 20px; */
    color: #947650;
    vertical-align: middle;
    background-color: #FFFFF8;
    cursor: pointer;
    height: 40px;
    margin-left: 10px;
}

.filebox input[type="file"] {
    position: absolute;
    width: 0;
    height: 0;
    padding: 0;
    overflow: hidden;
    border: 0;
}
</style>