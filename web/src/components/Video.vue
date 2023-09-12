<template>
  <div class="max-w-sm border rounded-lg shadow"
       :class="{ 'bg-gray-800 border-gray-900': modelValue.seen, 'bg-amber-800 border-amber-900': !modelValue.seen, 'opacity-80': modelValue.duration <= 60 }">
    <a :href="'https://www.youtube.com/watch?v=' + modelValue.youtubeId">
      <img class="rounded-t-lg w-full" :src="modelValue.thumbnailUrl" />
    </a>
    <div class="p-5">
      <div class="flex flex-row text-sm font-bold text-white items-center pb-4">
        <div class="basis-1/2">
          <ClockIcon class="h-6 w-6 inline" />
          {{ formatDuration(modelValue.duration) }}
          <EyeIcon class="ml-2 h-6 w-6 inline" />
          {{ abbreviateNumber(modelValue.viewCount) }}
        </div>
        <div class="basis-1/2 text-right flex items-center justify-end">
          <div class="mr-2">{{ timeAgo(modelValue.publishedDate) }}</div>

          <button class="bg-white text-red-500 font-bold py-2 px-3 rounded-full text-center"
                  v-on:click="markWatchLater(!modelValue.watchLater)">
            <HeartIconSolid v-if="modelValue.watchLater"  class="h-6 w-6" />
            <HeartIconOutline v-if="!modelValue.watchLater" class="h-6 w-6" />
          </button>
        </div>
      </div>
      <h5 class="mb-2 text-md font-bold tracking-tight text-white">
        <a class="mr-2 text-blue-200" :href="'https://www.youtube.com/channel/' + modelValue.channelId">{{ modelValue.channelTitle }}</a>
        <a :href="'https://www.youtube.com/watch?v=' + modelValue.youtubeId">{{ modelValue.title }}</a>
      </h5>
      <p :title="modelValue.description" class="mb-3 overflow-hidden h-14 text-sm font-normal text-gray-200">{{ modelValue.description }}</p>
    </div>
  </div>
</template>

<script setup>
import {ClockIcon, EyeIcon, HeartIcon as HeartIconSolid} from "@heroicons/vue/24/solid"
import {HeartIcon as HeartIconOutline} from "@heroicons/vue/24/outline"
import axios from "axios";

const props = defineProps(['modelValue'])
const emit = defineEmits(['update:modelValue'])

const markWatchLater = async (watchLater) => {
  const response = await axios.post('/video/watch-later', {}, {
    params: {
      id: props.modelValue.id,
      watchLater: watchLater
    }
  })
  emit('update:modelValue', response.data)
}

const formatDuration = (duration) => {
  const formatted = new Date(duration * 1000).toISOString().slice(11, 19);
  if (duration < 3600) {
    return formatted.substring(3);
  }
  return formatted;
}

const abbreviateNumber = (number) => {
  return Intl.NumberFormat('en-US', {
    notation: "compact",
    maximumFractionDigits: 0
  }).format(number);
}

const timeAgo = (input) => {
  const date = (input instanceof Date) ? input : new Date(input);
  const formatter = new Intl.RelativeTimeFormat('en');
  const ranges = {
    years: 3600 * 24 * 365,
    months: 3600 * 24 * 30,
    weeks: 3600 * 24 * 7,
    days: 3600 * 24,
    hours: 3600,
    minutes: 60,
    seconds: 1
  };
  const secondsElapsed = (date.getTime() - Date.now()) / 1000;
  for (let key in ranges) {
    if (ranges[key] < Math.abs(secondsElapsed)) {
      const delta = secondsElapsed / ranges[key];
      return formatter.format(Math.round(delta), key);
    }
  }
}
</script>
