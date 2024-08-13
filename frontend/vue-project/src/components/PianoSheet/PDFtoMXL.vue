<template>
  <div class="container">
    <div class="filebox">
      <label for="file">
        <v-icon>{{
          store.selectedFile ? "mdi-file-restore-outline" : "mdi-file-plus-outline"
        }}</v-icon>
      </label>
      <div v-if="store.selectedFile">
        <h4>
          <textarea class="upload-name" :value="store.selectedFile.name" readonly></textarea>
        </h4>
      </div>
      <div v-else>
        <h4>파일을 선택하거나 끌어다 놓으세요.</h4>
      </div>
      <input type="file" id="file" @change="onFileChange" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { usePianoSheetStore } from "@/stores/pianosheet";

const store = usePianoSheetStore();

const onFileChange = (event: Event): void => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    store.setSelectedFile(target.files[0]);
    console.log("파일 업로드 됨!");
  }
};
</script>

<style scoped>
.container {
  border: 2px solid #f5e5d1;
  color: #947650;
  background-color: #fffff8;
  width: 350px;
  height: 550px;
  padding: 10px;
  font-size: large;
}

.filebox {
  border: 2px dashed #f5e5d1;
  width: 350;
  height: 530px;
  padding-left: 70px;
  padding-right: 70px;
  padding-top: 200px;
  padding-bottom: 200px;
}

.upload-name {
  display: block;
  width: 100%;
  height: auto;
  color: #947650;
  border: none;
  background-color: transparent;
  resize: none;
  overflow: hidden;
}

.filebox label {
  display: inline-block;
  color: #947650;
  vertical-align: middle;
  background-color: #fffff8;
  cursor: pointer;
  height: 40px;
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
