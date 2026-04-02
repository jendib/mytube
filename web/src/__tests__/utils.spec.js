import { describe, it, expect } from 'vitest'
import { formatDuration, abbreviateNumber } from '../utils'

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
})
