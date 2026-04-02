<template>
  <div
    class="mb-6 rounded p-2 text-center text-white font-bold flex items-center justify-center gap-4"
  >
    <div class="flex gap-2">
      <input
        v-model="videoUrl"
        placeholder="Youtube URL"
        class="rounded px-2 py-1 text-black font-normal outline-none"
        :disabled="videoStore.loading"
        @keyup.enter="addVideo"
      />
      <button
        :disabled="videoStore.loading"
        class="bg-blue-600 hover:bg-blue-700 px-4 py-1 rounded transition-colors disabled:opacity-50"
        @click="addVideo"
      >
        Add
      </button>
    </div>
    <div><ClockIcon class="h-6 w-6 inline" /> {{ formatDuration(totalTime) }}</div>
    <div class="flex gap-2 items-center">
      <select v-model="sortBy" class="rounded px-2 py-1 text-black font-normal outline-none">
        <option value="publishedDate">Date</option>
        <option value="duration">Length</option>
        <option value="title">Title</option>
        <option value="channelTitle">Channel</option>
      </select>
      <label class="flex items-center gap-1 cursor-pointer select-none">
        <input v-model="sortAsc" type="checkbox" />
        Asc
      </label>
    </div>
  </div>

  <div
    v-if="videoStore.loading && videoStore.videos.length === 0"
    class="flex justify-center py-10"
  >
    <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-white"></div>
  </div>
  <div v-else-if="videoStore.error" class="bg-red-500 text-white p-4 rounded mb-4 text-center">
    {{ videoStore.error }}
  </div>
  <div v-else class="grid grid-cols-2 md:grid-cols-4 xl:grid-cols-6 gap-4">
    <Video
      v-for="(video, index) in videoStore.videos"
      :key="video.id"
      v-model="videoStore.videos[index]"
      @watch-later-changed="(value) => watchLaterChanged(index, value)"
    />
  </div>
</template>

<script setup>
import { onBeforeMount, ref, watch, computed } from 'vue'
import Video from '@/components/Video.vue'
import { formatDuration } from '../utils'
import { ClockIcon } from '@heroicons/vue/24/solid'
import { useVideoStore } from '@/stores/video'

const videoStore = useVideoStore()
const videoUrl = ref('')
const sortBy = ref('publishedDate')
const sortAsc = ref(false)

const totalTime = computed(() => {
  return videoStore.videos.reduce((sum, video) => sum + video.duration, 0)
})

const fetchVideos = async () => {
  await videoStore.fetchVideos({
    watchLaterOnly: true,
    sortBy: sortBy.value,
    sortOrder: sortAsc.value ? 'ASC' : 'DESC'
  })
}

onBeforeMount(async () => {
  await fetchVideos()
})

watch([sortBy, sortAsc], async () => {
  await fetchVideos()
})

const addVideo = async () => {
  if (!videoUrl.value) return
  await videoStore.addVideo(videoUrl.value)
  videoUrl.value = ''
  await fetchVideos()
}

const watchLaterChanged = (index, value) => {
  if (!value) {
    videoStore.videos.splice(index, 1)
  }
}
</script>
