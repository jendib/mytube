import { defineStore } from 'pinia'
import axios from 'axios'
import { ref } from 'vue'

export const useVideoStore = defineStore('video', () => {
  const videos = ref([])
  const loading = ref(false)
  const error = ref(null)

  async function fetchVideos(params = {}) {
    loading.value = true
    error.value = null
    try {
      const response = await axios.get('/video', { params })
      videos.value = response.data
    } catch (err) {
      error.value = 'Failed to fetch videos'
      console.error(err)
    } finally {
      loading.value = false
    }
  }

  async function addVideo(url) {
    loading.value = true
    try {
      await axios.post('/video', null, { params: { url } })
    } catch (err) {
      error.value = 'Failed to add video'
      console.error(err)
    } finally {
      loading.value = false
    }
  }

  return { videos, loading, error, fetchVideos, addVideo }
})
