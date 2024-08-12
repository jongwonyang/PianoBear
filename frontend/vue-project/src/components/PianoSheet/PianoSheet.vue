<template>
  <div id="osmd-sheet">
    <button class="prev" @click="prev()" v-if="pageNum"></button>
    <button class="next" @click="next()" v-if="pageNum < (pages ? pages.length - 1 : 0)"></button>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { ref, onMounted, computed } from "vue";
import { usePianoSheetStore } from "@/stores/pianosheet";
// @ts-ignore
import { osmd } from '@/mxlplayer/demo.mjs';

const store = usePianoSheetStore();
const route = useRoute();
const musicXml = ref<ArrayBuffer>();
const nowSheet = ref<number>(Number(route.params['id']));
const pages = ref<NodeListOf<Element>>();
const pageNum = ref<number>(0);

const loadMxl = async function (id: number) {
  musicXml.value = await store.mxlLoadfun(id);
};

const pageLoad = function () {
  pages.value?.forEach((e, i) => {
    if (i !== pageNum.value) {
      e.setAttribute('hidden', 'true');
    } else {
      e.removeAttribute('hidden');
    }
  });
};

const prev = function () {
  if (pageNum.value > 0) {
    pageNum.value -= 1;
    pageLoad();
  }
}
const next = function () {
  if (pages.value && pageNum.value < pages.value.length - 1) {
    pageNum.value += 1;
    pageLoad();
  };
}

onMounted(() => {
  loadMxl(nowSheet.value).then(() => {
    osmd('osmd-sheet', musicXml.value).then(() => {
      pages.value = document.querySelectorAll('#osmd-sheet > div');
      pageLoad();
    })
  });

});
</script>

<style scoped>
.prev {
  position: absolute;
  top: 52%;
  left: -3%;
  border-bottom: 1.5vh solid transparent;
  border-top: 1.5vh solid transparent;
  border-left: 1vw solid transparent;
  border-right: 1vw solid #a48253;
  z-index: 2;
}

.next {
  position: absolute;
  top: 52%;
  left: 96%;
  border-bottom: 1.5vh solid transparent;
  border-top: 1.5vh solid transparent;
  border-left: 1vw solid #a48253;
  border-right: 1vw solid transparent;
  z-index: 2;
}
</style>
