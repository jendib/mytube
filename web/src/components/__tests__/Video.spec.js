import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import Video from '../Video.vue'
import axios from 'axios'

vi.mock('axios')

describe('Video.vue', () => {
  const mockVideo = {
    id: 1,
    youtubeId: 'abc',
    thumbnailUrl: 'http://example.com/thumb.jpg',
    duration: 120,
    viewCount: 1000,
    publishedDate: new Date().toISOString(),
    watchLater: false,
    channelId: 'channel1',
    channelTitle: 'Channel 1',
    title: 'Video Title',
    description: 'Video Description',
    seen: false
  }

  it('renders video details correctly', () => {
    const wrapper = mount(Video, {
      props: {
        modelValue: mockVideo
      }
    })

    expect(wrapper.text()).toContain('Video Title')
    expect(wrapper.text()).toContain('Channel 1')
    expect(wrapper.text()).toContain('02:00') // 120 seconds
    expect(wrapper.text()).toContain('1K views')
    expect(wrapper.find('img').attributes('src')).toBe(mockVideo.thumbnailUrl)
  })

  it('applies correct classes based on seen and duration', async () => {
    const wrapper = mount(Video, {
      props: {
        modelValue: { ...mockVideo, seen: true, duration: 30 }
      }
    })

    expect(wrapper.find('div').classes()).toContain('bg-gray-800')
    expect(wrapper.find('div').classes()).toContain('opacity-80')
  })

  it('emits watch-later-changed when button is clicked', async () => {
    axios.post.mockResolvedValue({ data: { ...mockVideo, watchLater: true } })
    
    const wrapper = mount(Video, {
      props: {
        modelValue: mockVideo
      }
    })

    const button = wrapper.find('button')
    await button.trigger('click')

    expect(axios.post).toHaveBeenCalled()
    expect(wrapper.emitted('watch-later-changed')).toBeTruthy()
    expect(wrapper.emitted('watch-later-changed')[0]).toEqual([true])
  })
})
