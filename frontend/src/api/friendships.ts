import apiClient from './client'
import type { Friendship, User } from './types'

export const friendshipsApi = {
  sendRequest: (addresseeId: string) =>
    apiClient.post<Friendship>('/friendships/requests', { addresseeId }).then((r) => r.data),

  accept: (id: string) =>
    apiClient.post<Friendship>(`/friendships/requests/${id}/accept`, {}).then((r) => r.data),

  decline: (id: string) =>
    apiClient.post(`/friendships/requests/${id}/decline`, {}),

  remove: (id: string) =>
    apiClient.delete(`/friendships/${id}`),

  getAccepted: () =>
    apiClient.get<Friendship[]>('/friendships').then((r) => r.data),

  getPending: () =>
    apiClient.get<Friendship[]>('/friendships/requests/pending').then((r) => r.data),
}

export const usersApi = {
  getMe: () =>
    apiClient.get<User>('/users/me').then((r) => r.data),

  search: (q: string) =>
    apiClient.get<User[]>('/users/search', { params: { q } }).then((r) => r.data),

  updateProfile: (data: { displayName?: string; profilePhotoUrl?: string }) =>
    apiClient.put<User>('/users/me/profile', data).then((r) => r.data),

  updateMinTarget: (data: { value: number; isPercentage: boolean }) =>
    apiClient.put<User>('/users/me/minimum-target', data).then((r) => r.data),
}
