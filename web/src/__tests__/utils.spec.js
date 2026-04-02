import { describe, it, expect } from 'vitest'
import { formatDuration, abbreviateNumber, timeAgo } from '../utils'

describe('utils', () => {
  describe('formatDuration', () => {
    it('formats duration less than an hour', () => {
      expect(formatDuration(125)).toBe('02:05')
    })

    it('formats duration more than an hour', () => {
      expect(formatDuration(3661)).toBe('01:01:01')
    })
  })

  describe('abbreviateNumber', () => {
    it('abbreviates thousands', () => {
      expect(abbreviateNumber(1200)).toBe('1.2K')
    })

    it('abbreviates millions', () => {
      expect(abbreviateNumber(1200000)).toBe('1.2M')
    })
  })

  describe('timeAgo', () => {
    it('returns "now" for current date', () => {
      const now = new Date()
      expect(timeAgo(now)).toBe('now')
    })

    it('returns "1 hour ago" for one hour past', () => {
      const oneHourAgo = new Date(Date.now() - 3601 * 1000)
      expect(timeAgo(oneHourAgo)).toBe('1 hour ago')
    })

    it('returns "2 days ago" for two days past', () => {
      const twoDaysAgo = new Date(Date.now() - 2 * 24 * 3600 * 1000 - 1000)
      expect(timeAgo(twoDaysAgo)).toBe('2 days ago')
    })
  })
})
