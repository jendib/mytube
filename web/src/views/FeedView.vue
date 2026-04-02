<template>
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
    />
  </div>
</template>

<script setup>
import { onBeforeMount } from 'vue'
import Video from '@/components/Video.vue'
import { useVideoStore } from '@/stores/video'

const videoStore = useVideoStore()

onBeforeMount(async () => {
  await videoStore.fetchVideos({
    markAllAsSeen: true
  })
})
</script>
