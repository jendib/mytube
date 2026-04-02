<template>
  <div class="mb-6 rounded p-2 text-center text-white font-bold flex items-center justify-center gap-4">
    <div class="flex gap-2">
      <input v-model="videoUrl"
             placeholder="Youtube URL"
             class="rounded px-2 py-1 text-black font-normal outline-none"
             @keyup.enter="addVideo"/>
      <button @click="addVideo"
              class="bg-blue-600 hover:bg-blue-700 px-4 py-1 rounded transition-colors">
        Add
      </button>
    </div>
    <div>
      <ClockIcon class="h-6 w-6 inline" /> {{formatDuration(totalTime)}}
    </div>
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
const videoUrl = ref('')

const fetchVideos = async () => {
  const response = await axios.get('/video', {
    params: {
      watchLaterOnly: true
    }
  })
  videos.value = response.data
}

onBeforeMount(async () => {
  await fetchVideos()
})

const addVideo = async () => {
  if (!videoUrl.value) return
  await axios.post('/video', null, {
    params: {
      url: videoUrl.value
    }
  })
  videoUrl.value = ''
  await fetchVideos()
}

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
