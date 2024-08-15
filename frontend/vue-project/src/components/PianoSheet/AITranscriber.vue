<template>
  <div class="flex root">
    <glass-pane class="w-2/5">
      <div class="flex flex-row mb-8 items-center justify-center">
        <div class="font-bold text-2xl text-black mr-2">음악으로 악보 만들기</div>
        <div
          class="bg-blue-500 text-white pl-2 pr-3 py-1 rounded-full inline-flex items-center text-lg font-bold"
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
        :class="{
          'file-input-nofile': !fileName,
          'file-input-ready': fileName,
        }"
        @click="triggerFileInput"
        @dragover.prevent="handleDragOver"
        @drop.prevent="handleFileDrop"
      >
        <div
          class="rounded-lg border-dashed !border-2 !border-white/75 p-2 grow flex flex-col justify-center items-center"
        >
          <div class="mb-4 text-gray-500">
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
          <div v-else class="flex flex-col items-center">
            <div class="mb-1">{{ fileName }}</div>
            <div class="mb-2">{{ (file.size / (1024 * 1024)).toFixed(2) }} MB</div>
            <div>
              <button class="p-2 text-red-400 hover:text-red-300" @click="removeFile">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="1.5"
                  stroke="currentColor"
                  class="size-6"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0"
                  />
                </svg>
              </button>
            </div>
          </div>
          <input type="file" class="hidden" ref="fileInput" @change="handleFileUpload" />
        </div>
      </div>
    </glass-pane>
    <div class="w-1/5 flex items-center justify-center">
      <button
        class="transcribe-button text-white text-xl font-bold relative"
        :class="{
          'transcribe-button-nofile': !fileName,
          'transcribe-button-ready': fileName && !isProcessing,
          'transcribe-button-processing': isProcessing,
        }"
        :disabled="!file || isProcessing"
        @click="uploadFile"
      >
        <div v-if="!isProcessing" class="flex items-center justify-center mb-1">
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
        <div v-else class="flex items-center justify-center mb-1">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke-width="1.5"
            stroke="currentColor"
            class="size-6"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M8.25 3v1.5M4.5 8.25H3m18 0h-1.5M4.5 12H3m18 0h-1.5m-15 3.75H3m18 0h-1.5M8.25 19.5V21M12 3v1.5m0 15V21m3.75-18v1.5m0 15V21m-9-1.5h10.5a2.25 2.25 0 0 0 2.25-2.25V6.75a2.25 2.25 0 0 0-2.25-2.25H6.75A2.25 2.25 0 0 0 4.5 6.75v10.5a2.25 2.25 0 0 0 2.25 2.25Zm.75-12h9v9h-9v-9Z"
            />
          </svg>
        </div>
        <div v-if="!isProcessing">만들기</div>
        <div v-else>만드는중</div>
      </button>
    </div>
    <glass-pane class="w-2/5">
      <div class="font-bold text-2xl mb-4 flex items-center">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          class="size-6 mr-2"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M19.5 14.25v-2.625a3.375 3.375 0 0 0-3.375-3.375h-1.5A1.125 1.125 0 0 1 13.5 7.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H8.25m2.25 0H5.625c-.621 0-1.125.504-1.125 1.125v17.25c0 .621.504 1.125 1.125 1.125h12.75c.621 0 1.125-.504 1.125-1.125V11.25a9 9 0 0 0-9-9Z"
          />
        </svg>

        악보 미리보기
      </div>
      <div
        ref="osmdContainer"
        class="bg-white/50 rounded-lg py-2 h-64 overflow-y-auto !border-2 !border-white/50 mb-3"
      ></div>
      <button
        class="add-to-me-button bg-blue-500 rounded-full p-3 font-medium hover:bg-blue-400 text-white font-bold flex items-center justify-center disabled:bg-zinc-400 mb-4"
        :disabled="!isCompleted || isAdding"
        @click="handleAddToMe"
      >
        <svg
          v-if="!isAdding"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          class="size-6 mr-2"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M12 9v6m3-3H9m12 0a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z"
          />
        </svg>
        <svg
          v-else
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          class="size-6 mr-2"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M6.75 12a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0ZM12.75 12a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0ZM18.75 12a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Z"
          />
        </svg>

        내 악보에 추가
      </button>
      <div class="font-bold text-2xl mb-2 flex items-center">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          stroke-width="1.5"
          stroke="currentColor"
          class="size-6 mr-2"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M10.5 6h9.75M10.5 6a1.5 1.5 0 1 1-3 0m3 0a1.5 1.5 0 1 0-3 0M3.75 6H7.5m3 12h9.75m-9.75 0a1.5 1.5 0 0 1-3 0m3 0a1.5 1.5 0 0 0-3 0m-3.75 0H7.5m9-6h3.75m-3.75 0a1.5 1.5 0 0 1-3 0m3 0a1.5 1.5 0 0 0-3 0m-9.75 0h9.75"
          />
        </svg>
        악기 분리
      </div>
      <div>
        <div class="flex items-center justify-between mb-3">
          <button
            class="!border-2 p-3 rounded-full flex-1 mr-3 my-border"
            :class="{
              'bg-white/50': selectedInst !== 1,
              'bg-blue-500': selectedInst === 1,
              'text-white': selectedInst === 1,
            }"
            @click="changeInst(1)"
          >
            모든 악기
          </button>
          <button
            class="!border-2 p-3 rounded-full flex-1 mr-3 my-border"
            :class="{
              'bg-white/50': selectedInst !== 2,
              'bg-blue-500': selectedInst === 2,
              'text-white': selectedInst === 2,
            }"
            @click="changeInst(2)"
          >
            피아노만
          </button>
          <button
            class="!border-2 p-3 rounded-full flex-1 my-border"
            :class="{
              'bg-white/50': selectedInst !== 3,
              'bg-blue-500': selectedInst === 3,
              'text-white': selectedInst === 3,
            }"
            @click="changeInst(3)"
          >
            다른 악기만
          </button>
        </div>
        <midi-visualizer
          type="piano-roll"
          ref="midiVisualizer"
          class="h-32 overflow-scroll bg-white/50 rounded-lg my-border mb-2"
        />
        <div class="flex flex-row items-center">
          <midi-player sound-font ref="midiPlayer" class="w-100 mr-2" />
          <button
            class="rounded-full bg-white/50 !border-2 my-border p-4 hover:bg-white/60"
            @click="downloadMidi"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
              class="size-5"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M3 16.5v2.25A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75V16.5M16.5 12 12 16.5m0 0L7.5 12m4.5 4.5V3"
              />
            </svg>
          </button>
        </div>
      </div>
    </glass-pane>
  </div>
</template>

<script setup>
import "html-midi-player";
import GlassPane from "@/components/PianoSheet/GlassPane.vue";

import { onMounted, ref } from "vue";
import { uploadAudio, downloadFile, addToMe } from "@/api/transcriber";
import { OpenSheetMusicDisplay } from "opensheetmusicdisplay";
import JSZip from "jszip";

const fileName = ref("");
const file = ref(null);
const fileInput = ref(null);
const osmdContainer = ref(null);
const midiPlayer = ref(null);
const midiVisualizer = ref(null);
const isProcessing = ref(false);
const isCompleted = ref(false);
const selectedInst = ref(1);
const pianoMxlPathRef = ref("");
const allMidiUrl = ref("");
const pianoMidiUrl = ref("");
const otherMidiUrl = ref("");
const saveTitle = ref("");
const isAdding = ref(false);

let osmd = null;

onMounted(() => {
  osmd = new OpenSheetMusicDisplay(osmdContainer.value);

  osmd.setOptions({
    backend: "svg",
    drawingParameters: "compacttight", // don't display title, composer etc., smaller margins
  });

  midiPlayer.value.addVisualizer(midiVisualizer.value);
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

const removeFile = (event) => {
  event.stopPropagation();
  fileInput.value.value = "";
  fileName.value = "";
  file.value = null;
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

  isProcessing.value = true;
  saveTitle.value = file.value.name.split(".").slice(0, -1).join(".");

  const formData = new FormData();
  formData.append("audioFile", file.value);

  const uploadResp = await uploadAudio(formData);

  alert("변환 완료");

  const pianoMxlPath = uploadResp.data["piano_mxl"];
  const allMidiPath = uploadResp.data["all_midi"];
  const pianoMidiPath = uploadResp.data["piano_midi"];
  const otherMidiPath = uploadResp.data["other_inst_midi"];

  pianoMxlPathRef.value = pianoMxlPath;

  downloadFile(pianoMxlPath)
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
    allMidiUrl.value = url;
    midiPlayer.value.src = url;
    midiVisualizer.value.src = url;
  });

  downloadFile(pianoMidiPath).then((res) => {
    const blob = new Blob([res.data]);
    const url = URL.createObjectURL(blob);
    pianoMidiUrl.value = url;
  });

  downloadFile(otherMidiPath).then((res) => {
    const blob = new Blob([res.data]);
    const url = URL.createObjectURL(blob);
    otherMidiUrl.value = url;
  });

  isProcessing.value = false;
  isCompleted.value = true;
};

const changeInst = (to) => {
  selectedInst.value = to;

  if (to === 1) {
    midiPlayer.value.src = allMidiUrl.value;
    midiVisualizer.value.src = allMidiUrl.value;
  } else if (to === 2) {
    midiPlayer.value.src = pianoMidiUrl.value;
    midiVisualizer.value.src = pianoMidiUrl.value;
  } else if (to === 3) {
    midiPlayer.value.src = otherMidiUrl.value;
    midiVisualizer.value.src = otherMidiUrl.value;
  }
};

const handleAddToMe = () => {
  isAdding.value = true;

  const title = prompt("저장할 제목을 입력하세요.", saveTitle.value);

  addToMe(pianoMxlPathRef.value, title).then((res) => {
    if (res.status === 200) {
      alert("내 악보에 추가되었습니다.");
      isAdding.value = false;
    }
  });
};

const downloadMidi = () => {
  const a = document.createElement("a");

  if (selectedInst.value === 1) {
    a.href = allMidiUrl.value;
    a.download = "all.mid";
  } else if (selectedInst.value === 2) {
    a.href = pianoMidiUrl.value;
    a.download = "piano.mid";
  } else if (selectedInst.value === 3) {
    a.href = otherMidiUrl.value;
    a.download = "other.mid";
  }

  a.click();
};
</script>

<style scoped>
.root {
  width: 1080px;
  height: 600px;
}

.file-input-nofile {
  cursor: pointer;
}

.file-input-nofile:hover {
  background-color: rgba(255, 255, 255, 0.6);
}

.transcribe-button {
  width: 150px;
  height: 150px;
  border-radius: 9999px;
}

.transcribe-button-nofile {
  border: 4px solid rgba(255, 255, 255, 0.5);
  background-color: rgb(161 161 170);
  cursor: not-allowed;
}

.transcribe-button-ready {
  border: 4px solid rgba(255, 255, 255, 0.5);
  background-color: rgb(39, 39, 42);
  cursor: pointer;
}

.transcribe-button-ready:hover {
  background-color: rgba(39, 39, 42, 0.9);
}

.transcribe-button-processing {
  background-color: rgb(39, 39, 42);
  cursor: not-allowed;
}

@property --angle {
  syntax: "<angle>";
  initial-value: 0deg;
  inherits: false;
}

.transcribe-button-processing::after,
.transcribe-button-processing::before {
  content: "";
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

.transcribe-button-processing::before {
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

.add-to-me-button {
  border: 2px solid rgba(255, 255, 255, 0.5);
}

.my-border {
  border: 4px solid rgba(255, 255, 255, 0.5);
}
</style>
