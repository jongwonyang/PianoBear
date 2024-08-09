<template>
  <div class="temp">
    <div>노래로 악보 만들기<sup>AI</sup></div>
    <div>
      <div>
        <div
          @click="triggerFileInput"
          @dragover.prevent="handleDragOver"
          @drop.prevent="handleFileDrop"
        >
          <div v-if="!fileName">
            여기로 파일을 드래그하거나 클릭하여 업로드하세요.
          </div>
          <div v-else>{{ fileName }}</div>
          <input
            type="file"
            class="hidden"
            ref="fileInput"
            @change="handleFileUpload"
          />
        </div>
      </div>
      <div>
        <button :disabled="!file" @click="uploadFile">변환하기</button>
      </div>
      <div>
        <div>악보 미리보기</div>
        <div ref="osmdContainer"></div>
        <div>악기 분리</div>
        <div></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { uploadAudio, downloadFile } from "@/api/transcriber";
import { OpenSheetMusicDisplay } from "opensheetmusicdisplay";

const fileName = ref("");
const file = ref(null);
const fileInput = ref(null);
const osmdContainer = ref(null);

onMounted(() => {
  // const response = await apiClient.get("/transcriber/download?path=temp_files/a93dd3af-0e0d-4d1b-a6d4-8415a69f359a_piano.mxl", {
  //           responseType: 'blob', // 파일을 Blob으로 받음
  //       });

  //       console.log(response);

  //       const blob = new Blob([response.data], { type: response.data.type });
  //       const link = document.createElement('a');
  //       link.href = window.URL.createObjectURL(blob);
  //       link.download = "test.mxl";

  //       // 링크를 클릭하여 다운로드 실행
  //       link.click();

  //       // 메모리 해제
  //       window.URL.revokeObjectURL(link.href);



  // let osmd = new OpenSheetMusicDisplay(osmdContainer.value);
  // osmd.setOptions({
  //   backend: "svg",
  //   drawingParameters: "compacttight" // don't display title, composer etc., smaller margins
  // });

  // const response = await apiClient.get("/transcriber/download?path=temp_files/a93dd3af-0e0d-4d1b-a6d4-8415a69f359a_piano.mxl", {
  //           responseType: 'blob', // 파일을 Blob으로 받음
  //       });
  // console.log(response)
  // const blob = new Blob([response.data]);
  // console.log(blob);
  // const url = URL.createObjectURL(blob);
  // console.log(url);

  // osmd
  //   .load(
  //     url
  //   )
  //   .then(function () {
  //     osmd.render();
  //   });
});

const triggerFileInput = () => {
  if (fileInput.value) {
    fileInput.value.click();
  }
};

const handleFileUpload = (event) => {
  const selectedFile = event.target.files[0];
  if (selectedFile) {
    fileName.value = selectedFile.name;
    file.value = selectedFile;
  }
};

const handleDragOver = (event) => {
  event.preventDefault();
  // event.target.classList.add('drag-over');
};

const handleFileDrop = (event) => {
  const droppedFile = event.dataTransfer.files[0];
  if (droppedFile) {
    fileName.value = droppedFile.name;
    file.value = droppedFile;
  }
};

const uploadFile = async () => {
  if (!file.value) return;

  const formData = new FormData();
  formData.append("audioFile", file.value);

  const uploadResp = await uploadAudio(formData);

  alert("업로드 완료");
  
  const filePath = uploadResp.data["piano_mxl"];

  const downloadResp = await downloadFile(filePath);

  const blob = new Blob([downloadResp.data]);
  
  const url = URL.createObjectURL(blob);

  const osmd = new OpenSheetMusicDisplay(osmdContainer.value);

  osmd.setOptions({
    backend: "svg",
    drawingParameters: "compacttight" // don't display title, composer etc., smaller margins
  });

  osmd
    .load(url)
    .then(function () {
      osmd.render();
    });
};
</script>

<style scoped>
.temp {
  background-color: white;
}
</style>
