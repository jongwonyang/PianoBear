<template>
  <div class="d-flex root">
    <glass-pane class="w-2/5">
      <div class="flex flex-row mb-8 items-center justify-center">
        <div class="font-bold text-2xl text-stone-950 mr-2">
          음악으로 악보 만들기
        </div>
        <div
          class="bg-blue pl-2 pr-3 py-1 rounded-full inline-flex items-center text-lg font-bold"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="currentColor"
            class="size-6"
          >
            <path
              fill-rule="evenodd"
              d="M9 4.5a.75.75 0 0 1 .721.544l.813 2.846a3.75 3.75 0 0 0 2.576 2.576l2.846.813a.75.75 0 0 1 0 1.442l-2.846.813a3.75 3.75 0 0 0-2.576 2.576l-.813 2.846a.75.75 0 0 1-1.442 0l-.813-2.846a3.75 3.75 0 0 0-2.576-2.576l-2.846-.813a.75.75 0 0 1 0-1.442l2.846-.813A3.75 3.75 0 0 0 7.466 7.89l.813-2.846A.75.75 0 0 1 9 4.5ZM18 1.5a.75.75 0 0 1 .728.568l.258 1.036c.236.94.97 1.674 1.91 1.91l1.036.258a.75.75 0 0 1 0 1.456l-1.036.258c-.94.236-1.674.97-1.91 1.91l-.258 1.036a.75.75 0 0 1-1.456 0l-.258-1.036a2.625 2.625 0 0 0-1.91-1.91l-1.036-.258a.75.75 0 0 1 0-1.456l1.036-.258a2.625 2.625 0 0 0 1.91-1.91l.258-1.036A.75.75 0 0 1 18 1.5ZM16.5 15a.75.75 0 0 1 .712.513l.394 1.183c.15.447.5.799.948.948l1.183.395a.75.75 0 0 1 0 1.422l-1.183.395c-.447.15-.799.5-.948.948l-.395 1.183a.75.75 0 0 1-1.422 0l-.395-1.183a1.5 1.5 0 0 0-.948-.948l-1.183-.395a.75.75 0 0 1 0-1.422l1.183-.395c.447-.15.799-.5.948-.948l.395-1.183A.75.75 0 0 1 16.5 15Z"
              clip-rule="evenodd"
            />
          </svg>
          AI
        </div>
      </div>
      <div
        class="grow overflow-hidden rounded-lg bg-white/50 !border-2 !border-white/50 p-2 flex flex-row"
        @click="triggerFileInput"
        @dragover.prevent="handleDragOver"
        @drop.prevent="handleFileDrop"
      >
        <div
          class="rounded-lg border-dashed !border-2 !border-white/75 p-2 grow flex flex-col justify-center items-center"
        >
          <div class="mb-4 text-stone-500">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="currentColor"
              class="size-8"
            >
              <path
                fill-rule="evenodd"
                d="M19.952 1.651a.75.75 0 0 1 .298.599V16.303a3 3 0 0 1-2.176 2.884l-1.32.377a2.553 2.553 0 1 1-1.403-4.909l2.311-.66a1.5 1.5 0 0 0 1.088-1.442V6.994l-9 2.572v9.737a3 3 0 0 1-2.176 2.884l-1.32.377a2.553 2.553 0 1 1-1.402-4.909l2.31-.66a1.5 1.5 0 0 0 1.088-1.442V5.25a.75.75 0 0 1 .544-.721l10.5-3a.75.75 0 0 1 .658.122Z"
                clip-rule="evenodd"
              />
            </svg>
          </div>
          <div v-if="!fileName" class="text-center text-lg font-medium">
            여기로 파일을 드래그하거나<br />클릭하여 업로드하세요.
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
    </glass-pane>
    <div class="w-1/5 flex items-center justify-center">
      <button
        class="transcribe-button bg-zinc-800 text-white text-xl font-bold relative"
        :disabled="!file"
        @click="uploadFile"
      >
        <div class="flex items-center justify-center mb-1">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="currentColor"
            class="size-6"
          >
            <path
              fill-rule="evenodd"
              d="M12.97 3.97a.75.75 0 0 1 1.06 0l7.5 7.5a.75.75 0 0 1 0 1.06l-7.5 7.5a.75.75 0 1 1-1.06-1.06l6.22-6.22H3a.75.75 0 0 1 0-1.5h16.19l-6.22-6.22a.75.75 0 0 1 0-1.06Z"
              clip-rule="evenodd"
            />
          </svg>
        </div>
        <div>만들기</div>
      </button>
    </div>
    <glass-pane class="w-2/5">
      <div>악보 미리보기</div>
      <div ref="osmdContainer"></div>
      <div>악기 분리</div>
      <div>
        <midi-player ref="midiPlayer" />
      </div>
    </glass-pane>
  </div>
</template>

<script setup>
import "html-midi-player";
import GlassPane from "@/components/PianoSheet/GlassPane.vue";

import { onMounted, ref } from "vue";
import { uploadAudio, downloadFile } from "@/api/transcriber";
import { OpenSheetMusicDisplay } from "opensheetmusicdisplay";
import JSZip from "jszip";

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
    drawingParameters: "compacttight", // don't display title, composer etc., smaller margins
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
    .then((res) => JSZip.loadAsync(res.data))
    .then((zip) => {
      const musicXmlFile = Object.keys(zip.files).find((filename) => {
        return filename.endsWith(".xml") || filename.endsWith(".musicxml");
      });
      if (musicXmlFile) {
        return zip.files[musicXmlFile].async("text");
      } else {
        throw new Error("No MusicXML file found in MXL archive.");
      }
    })
    .then((content) => {
      osmd.load(content).then(() => osmd.render());
    })
    .catch((error) => {
      console.log("Error loading MXL file: ", error);
    });

  downloadFile(allMidiPath).then((res) => {
    const blob = new Blob([res.data]);
    const url = URL.createObjectURL(blob);
    midiPlayer.value.src = url;
  });
};
</script>

<style scoped>
.root {
  width: 1280px;
  height: 720px;
}

.transcribe-button {
  width: 150px;
  height: 150px;
  border-radius: 9999px;
}

@property --angle {
  syntax: "<angle>";
  initial-value: 0deg;
  inherits: false;
}

.transcribe-button::after, .transcribe-button::before {
  content: '';
  position: absolute;
  height: 100%;
  width: 100%;
  background-image: conic-gradient(from var(--angle), #ff4545, #00ff99, #006aff, #ff0095, #ff4545);
  top: 50%;
  left: 50%;
  translate: -50% -50%;
  z-index: -1;
  padding: 80px;
  border-radius: 9999px;
  animation: 3s spin linear infinite;
}

.transcribe-button::before {
  filter: blur(1.5rem);
  opacity: 0.5;
}

@keyframes spin {
  from {
    --angle: 0deg;
  }
  to {
    --angle: 360deg;
  }
}
</style>
