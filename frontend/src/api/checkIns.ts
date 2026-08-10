import apiClient from './client'
import type { CheckIn, RecordCheckInPayload, DaySummary } from './types'

export const checkInsApi = {
  record: (data: RecordCheckInPayload) =>
    apiClient.post<CheckIn>('/check-ins', data).then((r) => r.data),

  getForDate: (date: string) =>
    apiClient.get<CheckIn[]>('/check-ins', { params: { date } }).then((r) => r.data),

  getSummary: (date?: string) =>
    apiClient.get<DaySummary>('/check-ins/summary', { params: date ? { date } : {} }).then((r) => r.data),
}
