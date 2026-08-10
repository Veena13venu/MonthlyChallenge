import apiClient from './client'
import type { DaySummary, Streak, FriendFeedEntry, ChallengeCompletionRate } from './types'

export const dashboardApi = {
  getCalendar: (month: string) =>
    apiClient.get<DaySummary[]>('/dashboard/calendar', { params: { month } }).then((r) => r.data),

  getCompletionRates: (month: string) =>
    apiClient.get<ChallengeCompletionRate[]>('/dashboard/completion-rates', { params: { month } }).then((r) => r.data),

  getStreak: () =>
    apiClient.get<Streak>('/dashboard/streak').then((r) => r.data),

  getFriendsFeed: () =>
    apiClient.get<FriendFeedEntry[]>('/dashboard/friends-feed').then((r) => r.data),
}
