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
        <div>
          <html-midi-player ref="midiPlayer"></html-midi-player>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { uploadAudio, downloadFile } from "@/api/transcriber";
import { OpenSheetMusicDisplay } from "opensheetmusicdisplay";
import JSZip from "jszip";
import "html-midi-player";

const fileName = ref("");
const file = ref(null);
const fileInput = ref(null);
const osmdContainer = ref(null);
const midiPlayer = ref(null);

let osmd = null;

onMounted(() => {
  osmd = new OpenSheetMusicDisplay(osmdContainer.value);

  osmd.setOptions({
    backend: "svg",
    drawingParameters: "compacttight" // don't display title, composer etc., smaller margins
  });
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
  
  const pianoXmlPath = uploadResp.data["piano_mxl"];
  const allMidiPath = uploadResp.data["all_midi"];
  const pianoMidiPath = uploadResp.data["piano_midi"];
  const otherMidiPath = uploadResp.data["other_inst_midi"];

  downloadFile(pianoXmlPath)
    .then(res => JSZip.loadAsync(res.data))
    .then(zip => {
      const musicXmlFile = Object.keys(zip.files).find((filename) => {
        return filename.endsWith(".xml") || filename.endsWith(".musicxml");
      });
      if (musicXmlFile) {
        return zip.files[musicXmlFile].async("text");
      } else {
        throw new Error("No MusicXML file found in MXL archive.");
      }
    })
    .then(content => {
      osmd.load(content).then(() => osmd.render());
    })
    .catch(error => {
      console.log("Error loading MXL file: ", error);
    });

  downloadFile(allMidiPath)
    .then(res => console.log(res));
};
</script>

<style scoped>
.temp {
  background-color: white;
}
</style>
