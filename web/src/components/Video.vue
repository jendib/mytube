<template>
  <div class="max-w-sm bg-white border border-gray-200 rounded-lg shadow dark:bg-gray-800 dark:border-gray-700">
    <a href="#">
      <img class="rounded-t-lg w-full" :src="video.thumbnailUrl" />
    </a>
    <div class="p-5">
      <a href="#">
        <h5 class="mb-2 text-md font-bold tracking-tight text-gray-900 dark:text-white">{{ video.title }}</h5>
      </a>
      <p class="mb-3 overflow-hidden h-14 text-sm font-normal text-gray-700 dark:text-gray-400">{{ video.description }}</p>
    </div>
  </div>
</template>

<script setup>
  defineProps({
    video: { type: Object, required: true },
  })
</script>
