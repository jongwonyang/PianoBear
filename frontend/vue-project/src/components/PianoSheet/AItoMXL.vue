<template>
    <!--AI로 mxl만드는 로직 사용-->        
    <div class="container">
        <!-- <div class="box"> 
            <div class="icon">
                <v-icon>mdi-book-plus-outline</v-icon>
            </div>
            <div class="text">
                <h4>파일을 선택하거나 끌어다 놓으세요.</h4>
            </div>
        </div> -->
        <div class="box">
            <v-file-input
                label="파일을 선택하세요."
                bg-color="#FFFFF8"
                base-color="#947650"
                width="250px"
            ></v-file-input>
        </div>
    </div>
</template>

<script setup>

</script>

<style scoped>
.container{
    border: 2px solid #F5E5D1;
    color: #947650;
    width: 350px;
    height: 550px;
    padding-left: 50px;
    padding-right: 100px;
    padding-top: 200px;
    padding-bottom: 200px;
    font-size: large;
}

</style>