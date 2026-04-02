import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useVideoStore } from '../video'
import axios from 'axios'

vi.mock('axios')

describe('Video Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('fetches videos successfully', async () => {
    const store = useVideoStore()
    const mockVideos = [{ id: 1, title: 'Video 1' }]
    axios.get.mockResolvedValue({ data: mockVideos })

    await store.fetchVideos()

    expect(store.videos).toEqual(mockVideos)
    expect(store.loading).toBe(false)
    expect(store.error).toBeNull()
    expect(axios.get).toHaveBeenCalledWith('/video', { params: {} })
  })

  it('handles fetch error', async () => {
    const store = useVideoStore()
    axios.get.mockRejectedValue(new Error('Network error'))

    await store.fetchVideos()

    expect(store.videos).toEqual([])
    expect(store.loading).toBe(false)
    expect(store.error).toBe('Failed to fetch videos')
  })

  it('adds a video successfully', async () => {
    const store = useVideoStore()
    const url = 'https://youtube.com/v123'
    axios.post.mockResolvedValue({})

    await store.addVideo(url)

    expect(axios.post).toHaveBeenCalledWith('/video', null, { params: { url } })
    expect(store.loading).toBe(false)
  })
})
