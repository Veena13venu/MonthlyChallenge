import { Routes, Route, Link, useLocation } from 'react-router-dom'
import { useAuth } from './auth/AuthProvider'

// ─── Real page components ────────────────────────────────────────────────────
import Dashboard        from './pages/Dashboard'
import MyChallenges     from './pages/MyChallenges'
import DailyCheckIn     from './pages/DailyCheckIn'
import Streaks          from './pages/Streaks'
import HistoryReports   from './pages/HistoryReports'
import ChallengeLibrary from './pages/ChallengeLibrary'
import Friends          from './pages/Friends'
import Profile          from './pages/Profile'

// ─── Lucide icons ────────────────────────────────────────────────────────────
import {
  LayoutDashboard, Target, CheckSquare, Flame, BarChart2,
  BookOpen, Users, Bell, User, Settings as SettingsIcon,
  Layers, Shield, LogIn, LogOut, Compass,
} from 'lucide-react'

// ─── Lightweight placeholder pages (not worth a separate file) ───────────────
function Notifications() {
  return (
    <div className="bg-white rounded-2xl shadow-sm p-8 border border-gray-100 space-y-4">
      <h2 className="text-2xl font-bold text-gray-900">Notifications</h2>
      <div className="p-4 bg-indigo-50 rounded-xl border border-indigo-100 flex items-start gap-3">
        <div className="w-2 h-2 mt-2 bg-indigo-600 rounded-full shrink-0" />
        <div>
          <p className="text-sm font-semibold text-gray-900">Welcome to MonthlyChallenge!</p>
          <p className="text-xs text-gray-500 mt-1">
            Get started by creating your first challenge in My Challenges.
          </p>
        </div>
      </div>
    </div>
  )
}

function Settings() {
  return (
    <div className="bg-white rounded-2xl shadow-sm p-8 border border-gray-100">
      <h2 className="text-2xl font-bold text-gray-900">Settings</h2>
      <p className="text-gray-500 text-sm mt-2">
        App-wide preferences will be configurable here in a future update.
      </p>
    </div>
  )
}

function ManageTemplates() {
  return (
    <div className="bg-white rounded-2xl shadow-sm p-8 border border-gray-100">
      <h2 className="text-2xl font-bold text-gray-900">Manage Templates (Admin)</h2>
      <p className="text-gray-500 text-sm mt-2">Admin template management — coming soon.</p>
    </div>
  )
}

function ManageUsers() {
  return (
    <div className="bg-white rounded-2xl shadow-sm p-8 border border-gray-100">
      <h2 className="text-2xl font-bold text-gray-900">Manage Users (Admin)</h2>
      <p className="text-gray-500 text-sm mt-2">Admin user management — coming soon.</p>
    </div>
  )
}

// ─── Nav link config ─────────────────────────────────────────────────────────
const mainNav = [
  { to: '/',                label: 'Dashboard',           icon: LayoutDashboard },
  { to: '/my-challenges',   label: 'My challenges',       icon: Target },
  { to: '/daily-check-in',  label: 'Daily check-in',      icon: CheckSquare },
  { to: '/streaks',         label: 'Streaks',             icon: Flame },
  { to: '/history-reports', label: 'History and reports', icon: BarChart2 },
  { to: '/challenge-library', label: 'Challenge library', icon: BookOpen },
]
const socialNav = [
  { to: '/friends',       label: 'Friends',       icon: Users },
  { to: '/notifications', label: 'Notifications', icon: Bell },
]
const accountNav = [
  { to: '/profile',  label: 'Profile',  icon: User },
  { to: '/settings', label: 'Settings', icon: SettingsIcon },
]
const adminNav = [
  { to: '/admin/templates', label: 'Manage templates', icon: Layers },
  { to: '/admin/users',     label: 'Manage users',     icon: Shield },
]

// ─── NavLink helper ──────────────────────────────────────────────────────────
function NavLink({ to, label, Icon, active }: {
  to: string; label: string; Icon: React.ElementType; active: boolean
}) {
  return (
    <li>
      <Link
        to={to}
        className={`flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium transition ${
          active
            ? 'bg-[#0f2d59] text-white'
            : 'text-zinc-400 hover:text-white hover:bg-zinc-800/50'
        }`}
      >
        <Icon className="w-4 h-4 shrink-0" />
        <span>{label}</span>
      </Link>
    </li>
  )
}

// ─── App ─────────────────────────────────────────────────────────────────────
export default function App() {
  const { isAuthenticated, isLoading, login, logout, username, email } = useAuth()
  const location = useLocation()
  const isActive = (p: string) => location.pathname === p

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-[#121212] text-zinc-400">
        <div className="text-center space-y-3">
          <svg className="animate-spin w-10 h-10 mx-auto text-indigo-500" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
          </svg>
          <p className="text-sm font-medium">Loading MonthlyChallenge…</p>
        </div>
      </div>
    )
  }

  const pageName = location.pathname === '/'
    ? 'Dashboard'
    : location.pathname.split('/').filter(Boolean).pop()?.replace(/-/g, ' ') ?? ''

  return (
    <div className="flex min-h-screen bg-gray-50 text-gray-900 font-sans">

      {/* ── Sidebar ────────────────────────────────────────────────────── */}
      <aside className="w-[280px] bg-[#121212] text-zinc-300 flex flex-col justify-between border-r border-zinc-800 shrink-0 select-none">
        <div className="flex flex-col">
          {/* Brand */}
          <div className="flex items-center space-x-3 px-6 py-5 border-b border-zinc-900 mb-4">
            <div className="bg-indigo-600 rounded-lg p-2 shadow-sm">
              <Compass className="w-5 h-5 text-white" />
            </div>
            <span className="text-lg font-bold text-white tracking-tight">MonthlyChallenge</span>
          </div>

          <div className="px-4 space-y-5 overflow-y-auto">
            {/* Main */}
            <div>
              <span className="text-[11px] font-semibold text-zinc-500 uppercase tracking-wider px-3 mb-2 block">Main</span>
              <ul className="space-y-1">
                {mainNav.map((n) => <NavLink key={n.to} to={n.to} label={n.label} Icon={n.icon} active={isActive(n.to)} />)}
              </ul>
            </div>
            {/* Social */}
            <div>
              <span className="text-[11px] font-semibold text-zinc-500 uppercase tracking-wider px-3 mb-2 block">Social</span>
              <ul className="space-y-1">
                {socialNav.map((n) => <NavLink key={n.to} to={n.to} label={n.label} Icon={n.icon} active={isActive(n.to)} />)}
              </ul>
            </div>
            {/* Account */}
            <div>
              <span className="text-[11px] font-semibold text-zinc-500 uppercase tracking-wider px-3 mb-2 block">Account</span>
              <ul className="space-y-1">
                {accountNav.map((n) => <NavLink key={n.to} to={n.to} label={n.label} Icon={n.icon} active={isActive(n.to)} />)}
              </ul>
            </div>
            {/* Admin */}
            <div>
              <span className="text-[11px] font-semibold text-zinc-500 uppercase tracking-wider px-3 mb-2 block">Admin only</span>
              <ul className="space-y-1">
                {adminNav.map((n) => <NavLink key={n.to} to={n.to} label={n.label} Icon={n.icon} active={isActive(n.to)} />)}
              </ul>
            </div>
          </div>
        </div>

        {/* User footer */}
        <div className="p-4 border-t border-zinc-900 bg-[#0a0a0a]">
          {isAuthenticated ? (
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-3 min-w-0">
                <div className="w-9 h-9 rounded-full bg-indigo-600 text-white flex items-center justify-center text-sm font-bold shrink-0">
                  {username ? username.charAt(0).toUpperCase() : 'U'}
                </div>
                <div className="min-w-0">
                  <p className="text-xs font-bold text-white truncate">{username || 'User'}</p>
                  <p className="text-[10px] text-zinc-500 truncate">{email}</p>
                </div>
              </div>
              <button
                onClick={logout}
                title="Sign Out"
                className="p-1.5 rounded-lg text-zinc-400 hover:text-red-400 hover:bg-zinc-800/50 transition"
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>
          ) : (
            <button
              onClick={login}
              className="w-full flex items-center justify-center gap-2 px-4 py-2 text-sm font-semibold rounded-lg text-white bg-indigo-600 hover:bg-indigo-700 transition"
            >
              <LogIn className="w-4 h-4" /> Sign In
            </button>
          )}
        </div>
      </aside>

      {/* ── Main content ───────────────────────────────────────────────── */}
      <div className="flex-1 flex flex-col min-h-screen">
        {/* Top bar */}
        <header className="bg-white border-b border-gray-200 h-16 flex items-center justify-between px-8 shrink-0">
          <div className="flex items-center gap-2">
            <span className="text-sm font-semibold text-gray-500">MonthlyChallenge</span>
            <span className="text-gray-300">/</span>
            <span className="text-sm font-bold text-gray-800 capitalize">{pageName}</span>
          </div>
          <div className="flex items-center gap-3">
            <span className={`text-xs font-semibold px-2.5 py-1 rounded-full border ${
              isAuthenticated
                ? 'bg-green-50 text-green-700 border-green-200'
                : 'bg-gray-100 text-gray-600 border-gray-200'
            }`}>
              {isAuthenticated ? `● ${username}` : 'Not signed in'}
            </span>
            {isAuthenticated && (
              <button
                onClick={logout}
                title="Sign Out"
                className="flex items-center gap-1.5 text-xs font-medium text-gray-500 hover:text-red-600 transition"
              >
                <LogOut className="w-3.5 h-3.5" /> Sign Out
              </button>
            )}
          </div>
        </header>

        <main className="flex-1 p-8 overflow-y-auto">
          <Routes>
            <Route path="/"                  element={<Dashboard />} />
            <Route path="/my-challenges"     element={<MyChallenges />} />
            <Route path="/daily-check-in"    element={<DailyCheckIn />} />
            <Route path="/streaks"           element={<Streaks />} />
            <Route path="/history-reports"   element={<HistoryReports />} />
            <Route path="/challenge-library" element={<ChallengeLibrary />} />
            <Route path="/friends"           element={<Friends />} />
            <Route path="/notifications"     element={<Notifications />} />
            <Route path="/profile"           element={<Profile />} />
            <Route path="/settings"          element={<Settings />} />
            <Route path="/admin/templates"   element={<ManageTemplates />} />
            <Route path="/admin/users"       element={<ManageUsers />} />
          </Routes>
        </main>
      </div>
    </div>
  )
}
