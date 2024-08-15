<template>
  <div class="slider">
    <div id="osmd-sheet" class="slides"></div>
    <button class="prev" @click="prev()" v-if="pageNum"></button>
    <button class="next" @click="next()" v-if="pageNum < (pages ? pages.length - 1 : 0)"></button>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { ref, onMounted, computed } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";
// @ts-ignore
import { osmd } from "@/mxlplayer/demo.mjs";

const store = usePianoSheetStore();
const route = useRoute();
const musicXml = ref<ArrayBuffer>();
const nowSheet = ref<number>(Number(route.params["id"]));
const pages = ref<NodeListOf<Element>>();
const pageNum = ref<number>(0);
const slidesContainer = ref<HTMLDivElement | null>();

const loadMxl = async function (id: number) {
  musicXml.value = await store.mxlLoadfun(id);
};

const prev = function () {
  if (pageNum.value > 0) {
    pageNum.value -= 1;
    updateSlidePosition();
  }
};
const next = function () {
  if (pages.value && pageNum.value < pages.value.length - 1) {
    pageNum.value += 1;
    updateSlidePosition();
  }
};
const updateSlidePosition = () => {
  const offset = -pageNum.value * 100;
  if (slidesContainer.value) {
    slidesContainer.value.style.transform = `translateX(${offset}%)`;
  }
};
onMounted(() => {
  loadMxl(nowSheet.value).then(() => {
    osmd("osmd-sheet", musicXml.value)
      .then(() => {
        pages.value = document.querySelectorAll("#osmd-sheet > div");
        pages.value.forEach((e) => {
          e.setAttribute("class", "slide");
        });
      })
      .then(() => {
        slidesContainer.value = document.querySelector(".slides") as HTMLDivElement;
      });
  });
});
</script>

<style scoped>
.prev {
  position: absolute;
  left: 0px;
  border-bottom: 1.5vh solid transparent;
  border-top: 1.5vh solid transparent;
  border-left: 1vw solid transparent;
  border-right: 1vw solid #947650;
  z-index: 2;
}

.next {
  position: absolute;
  right: 0px;
  border-bottom: 1.5vh solid transparent;
  border-top: 1.5vh solid transparent;
  border-left: 1vw solid #947650;
  border-right: 1vw solid transparent;
  z-index: 2;
}

.slider {
  position: relative;
  width: 100%;
  overflow: hidden;
  background-color: white;
}

.slides {
  display: flex;
  transition: transform 0.3s ease;
}

button {
  position: absolute;
  top: 50%;
  cursor: pointer;
  z-index: 10;
}
</style>
