import React, { createContext, useContext, useEffect, useState } from 'react'
import keycloak from './keycloak'
import apiClient from '../api/client'

interface AuthContextValue {
  isAuthenticated: boolean
  isLoading: boolean
  username: string
  email: string
  token: string | undefined
  logout: () => void
  login: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

// Module-level singleton — keycloak.init() must only ever be called once per page load.
let keycloakInitPromise: Promise<boolean> | undefined

function getInitPromise(): Promise<boolean> {
  if (!keycloakInitPromise) {
    keycloakInitPromise = keycloak.init({
      onLoad: 'login-required',
      pkceMethod: 'S256',
      checkLoginIframe: false,
      responseMode: 'query',   // use ?code= not #code=  → stops the hash loop
    })
  }
  return keycloakInitPromise
}

async function provisionUser(): Promise<void> {
  try {
    await apiClient.get('/users/me')
    console.log('[Auth] User provisioned in DB')
  } catch (err) {
    console.error('[Auth] User provisioning failed:', err)
  }
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [isLoading, setIsLoading]             = useState(true)
  const [username, setUsername]               = useState('')
  const [email, setEmail]                     = useState('')

  const syncClaims = () => {
    if (keycloak.tokenParsed) {
      setUsername((keycloak.tokenParsed as Record<string, string>)['preferred_username'] ?? '')
      setEmail   ((keycloak.tokenParsed as Record<string, string>)['email']              ?? '')
    }
  }

  useEffect(() => {
    getInitPromise()
      .then(async (authenticated: boolean) => {
        setIsAuthenticated(authenticated)

        if (authenticated) {
          syncClaims()
          await provisionUser()   // creates the users row in PostgreSQL if new

          // Refresh token 60 s before expiry
          setInterval(() => {
            keycloak.updateToken(60)
              .then((refreshed: boolean) => { if (refreshed) syncClaims() })
              .catch(() => keycloak.logout())
          }, 30_000)
        }

        setIsLoading(false)
      })
      .catch((err: unknown) => {
        console.error('Keycloak init error:', err)
        setIsLoading(false)
      })
  }, [])   // empty deps — intentional, runs once per mount

  return (
    <AuthContext.Provider value={{
      isAuthenticated, isLoading, username, email,
      token:  keycloak.token,
      logout: () => keycloak.logout({ redirectUri: window.location.origin }),
      login:  () => keycloak.login(),
    }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
