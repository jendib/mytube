<template>
  <div class="grid grid-cols-2 md:grid-cols-4 xl:grid-cols-6 gap-4">
    <Video v-for="video in videos" :video="video" :key="video.id"/>
  </div>
</template>

<script setup>
import {onBeforeMount, ref} from "vue";
import axios from "axios";
import Video from "@/components/Video.vue";

const videos = ref([])

onBeforeMount(async () => {
  const response = await axios.get('/video')
  videos.value = response.data
})
</script>