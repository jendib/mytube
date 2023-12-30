<template>
  <div class="mb-6 rounded p-2 text-center text-white font-bold">
    <ClockIcon class="h-6 w-6 inline" /> {{formatDuration(totalTime)}}
  </div>
  <div class="grid grid-cols-2 md:grid-cols-4 xl:grid-cols-6 gap-4">
    <Video v-for="(video, index) in videos"
           v-model="videos[index]"
           @watch-later-changed="(value) => watchLaterChanged(index, value)"
           :key="video.id"/>
  </div>
</template>

<script setup>
import {onBeforeMount, ref, watch} from "vue";
import axios from "axios";
import Video from "@/components/Video.vue";
import formatDuration from "../utils";
import {ClockIcon} from "@heroicons/vue/24/solid";

const videos = ref([])
const totalTime = ref(0)

onBeforeMount(async () => {
  const response = await axios.get('/video', {
    params: {
      watchLaterOnly: true
    }
  })
  videos.value = response.data
})

watch(videos, async (newVideos) => {
  totalTime.value = newVideos.reduce((sum, video) => sum + video.duration, 0)
}, {
  deep: true
})

const watchLaterChanged = (index, value) => {
  if (!value) {
    videos.value.splice(index, 1);
  }
}
</script>