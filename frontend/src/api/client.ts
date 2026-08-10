import axios, { type InternalAxiosRequestConfig } from 'axios'
import keycloak from '../auth/keycloak'

/**
 * Custom flag on every request config.
 * Set requiresAuth: false only for public endpoints.
 * All protected endpoints: just call the API normally (default is true).
 */
export interface AuthRequestConfig {
  requiresAuth?: boolean
}

// Augment InternalAxiosRequestConfig with our flag
declare module 'axios' {
  interface InternalAxiosRequestConfig {
    requiresAuth?: boolean
  }
  interface AxiosRequestConfig {
    requiresAuth?: boolean
  }
}

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api/v1',
  headers: { 'Content-Type': 'application/json' },
})

// ── Request interceptor — attach Bearer token ─────────────────────────────────
apiClient.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    // Default: add auth header unless explicitly opted out
    const skip = config.requiresAuth === false
    if (!skip) {
      try {
        await keycloak.updateToken(60)
      } catch {
        keycloak.login()
        return Promise.reject(new Error('Session expired. Redirecting to login…'))
      }
      const token = keycloak.token
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      } else {
        keycloak.login()
        return Promise.reject(new Error('Not authenticated'))
      }
    }
    return config
  },
  (error) => Promise.reject(error),
)

// ── Response interceptor — normalise errors ───────────────────────────────────
apiClient.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401) {
      keycloak.login()
      return Promise.reject(new Error('Session expired. Redirecting to login…'))
    }
    const message =
      error.response?.data?.detail ??
      error.response?.data?.message ??
      error.message ??
      'An unexpected error occurred'
    return Promise.reject(new Error(message))
  },
)

export default apiClient
