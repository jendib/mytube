<template>
  <div class="max-w-sm border rounded-lg shadow"
       :class="{ 'bg-gray-800 border-gray-900': modelValue.seen, 'bg-amber-800 border-amber-900': !modelValue.seen, 'opacity-80': modelValue.duration <= 60 }">
    <a :href="'https://www.youtube.com/watch?v=' + modelValue.youtubeId">
      <img class="rounded-t-lg w-full" :src="modelValue.thumbnailUrl" />
    </a>
    <div class="p-5">
      <div class="flex flex-row text-sm font-bold text-white items-center pb-4">
        <div class="basis-1/2">
          <span class="py-2 px-2 bg-black rounded-full">{{ formatDuration(modelValue.duration) }}</span>
          <span class="ml-2 py-2 px-3 bg-black rounded-full">{{ abbreviateNumber(modelValue.viewCount) }} views</span>
        </div>
        <div class="basis-1/2 text-right flex items-center justify-end">
          <div class="mr-2">{{ timeAgo(modelValue.publishedDate) }}</div>

          <button class="bg-white text-black font-bold py-2 px-2 rounded-full text-center"
                  v-on:click="markWatchLater(!modelValue.watchLater)">
            <HandThumbUpIconSolid v-if="modelValue.watchLater"  class="h-6 w-6" />
            <HandThumbUpIconOutline v-if="!modelValue.watchLater" class="h-6 w-6" />
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
import {HandThumbUpIcon as HandThumbUpIconSolid} from "@heroicons/vue/24/solid"
import {HandThumbUpIcon as HandThumbUpIconOutline} from "@heroicons/vue/24/outline"
import axios from "axios";
import formatDuration from "../utils"

const model = defineModel()
const emit = defineEmits(['watch-later-changed'])

const markWatchLater = async (watchLater) => {
  const response = await axios.post('/video/watch-later', {}, {
    params: {
      id: model.value.id,
      watchLater: watchLater
    }
  })
  model.value = response.data;
  emit('watch-later-changed', watchLater);
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
