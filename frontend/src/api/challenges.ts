import apiClient from './client'
import type { Challenge, ChallengeTemplate, CreateChallengePayload, UpdateChallengePayload } from './types'

// All endpoints require auth — the interceptor adds the Bearer token automatically.
export const challengesApi = {
  create: (data: CreateChallengePayload) =>
    apiClient.post<Challenge>('/challenges', data).then((r) => r.data),

  listForMonth: (month: string) =>
    apiClient.get<Challenge[]>('/challenges', { params: { month } }).then((r) => r.data),

  getToday: () =>
    apiClient.get<Challenge[]>('/challenges/today').then((r) => r.data),

  update: (id: string, data: UpdateChallengePayload) =>
    apiClient.put<Challenge>(`/challenges/${id}`, data).then((r) => r.data),

  delete: (id: string) =>
    apiClient.delete(`/challenges/${id}`),

  rollover: (fromMonth: string, toMonth: string) =>
    apiClient.post<Challenge[]>('/challenges/rollover', null, { params: { fromMonth, toMonth } }).then((r) => r.data),

  getTemplates: () =>
    apiClient.get<ChallengeTemplate[]>('/challenges/templates').then((r) => r.data),
}
