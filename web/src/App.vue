<template>
  <div class="min-h-full">
    <Disclosure as="nav" class="bg-gray-800" v-slot="{ open }">
      <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div class="flex h-16 items-center justify-between">
          <div class="flex items-center">
            <div class="flex-shrink-0">
<!--              <img class="h-8 w-8" src="https://tailwindui.com/img/logos/mark.svg?color=indigo&shade=500" alt="MyTube" />-->
            </div>
            <div class="hidden md:block">
              <div class="ml-10 flex items-baseline space-x-4">
                <RouterLink to="/" :class="[$route.name === 'feed' ? 'bg-gray-900 text-white' : 'text-gray-300 hover:bg-gray-700 hover:text-white', 'rounded-md px-4 py-3 text-sm font-bold']">
                  <BookOpenIcon class="h-6 w-6 mr-2 inline" />
                  Feed
                </RouterLink>
                <RouterLink to="/watch-later" :class="[$route.name === 'watch-later' ? 'bg-gray-900 text-white' : 'text-gray-300 hover:bg-gray-700 hover:text-white', 'rounded-md px-4 py-3 text-sm font-bold']">
                  <ClockIcon class="h-6 w-6 mr-2 inline" />
                  Watch Later
                </RouterLink>
              </div>
            </div>
          </div>
          <div>
            <a href="https://www.youtube.com" class="text-gray-300 hover:bg-gray-700 hover:text-white rounded-md px-3 py-2 text-sm font-bold">
              <VideoCameraIcon class="h-6 w-6 mr-2 inline" /> Go to YouTube
            </a>
          </div>
          <div class="-mr-2 flex md:hidden">
            <!-- Mobile menu button -->
            <DisclosureButton class="relative inline-flex items-center justify-center rounded-md bg-gray-800 p-2 text-gray-400 hover:bg-gray-700 hover:text-white focus:outline-none focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-gray-800">
              <span class="absolute -inset-0.5" />
              <span class="sr-only">Open main menu</span>
              <Bars3Icon v-if="!open" class="block h-6 w-6" aria-hidden="true" />
              <XMarkIcon v-else class="block h-6 w-6" aria-hidden="true" />
            </DisclosureButton>
          </div>
        </div>
      </div>

      <DisclosurePanel class="md:hidden">
        <div class="space-y-1 px-2 pb-3 pt-2 sm:px-3">
          <DisclosureButton class="text-gray-300 hover:bg-gray-700 hover:text-white block rounded-md px-3 py-2 text-base font-medium">
            <RouterLink to="/">Feed</RouterLink>
          </DisclosureButton>
          <DisclosureButton class="text-gray-300 hover:bg-gray-700 hover:text-white block rounded-md px-3 py-2 text-base font-medium">
            <RouterLink to="/watch-later">Watch Later</RouterLink>
          </DisclosureButton>
        </div>
      </DisclosurePanel>
    </Disclosure>

    <main>
      <div class="mx-auto py-6 sm:px-6 lg:px-8">
        <RouterView />
      </div>
    </main>
  </div>
</template>

<script setup>
import {RouterView} from 'vue-router'
import {Disclosure, DisclosureButton, DisclosurePanel} from '@headlessui/vue'
import {Bars3Icon, XMarkIcon} from '@heroicons/vue/24/outline'
import {BookOpenIcon, ClockIcon, VideoCameraIcon} from "@heroicons/vue/24/solid"
</script>