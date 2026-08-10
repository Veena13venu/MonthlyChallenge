import { format } from 'date-fns'
import { useQuery } from '@tanstack/react-query'
import { Trophy, CheckCircle, Users, Flame, TrendingUp } from 'lucide-react'
import { dashboardApi } from '../api/dashboard'
import { challengesApi } from '../api/challenges'
import type { DaySummary, Streak, Challenge, FriendFeedEntry, DayResult } from '../api/types'

const resultColor: Record<DayResult, string> = {
  SUCCESS: 'bg-green-500',
  PARTIAL: 'bg-amber-400',
  MISSED:  'bg-red-400',
}

export default function Dashboard() {
  const today     = format(new Date(), 'yyyy-MM-dd')
  const thisMonth = format(new Date(), 'yyyy-MM')

  const { data: streak }           = useQuery<Streak>          ({ queryKey: ['streak'],           queryFn: () => dashboardApi.getStreak() })
  const { data: todayChallenges }  = useQuery<Challenge[]>     ({ queryKey: ['challenges-today'], queryFn: () => challengesApi.getToday() })
  const { data: calendarData }     = useQuery<DaySummary[]>    ({ queryKey: ['calendar', thisMonth], queryFn: () => dashboardApi.getCalendar(thisMonth) })
  const { data: friendsData }      = useQuery<FriendFeedEntry[]>({ queryKey: ['friends-feed'],    queryFn: () => dashboardApi.getFriendsFeed() })

  const calendarDays: DaySummary[]    = calendarData    ?? []
  const challenges:   Challenge[]     = todayChallenges ?? []
  const friends:      FriendFeedEntry[] = friendsData   ?? []
  const todayEntry = calendarDays.find((d) => d.date === today)

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-2xl shadow-sm p-8 border border-gray-100">
        <h2 className="text-3xl font-extrabold text-gray-900 mb-1">Welcome back! 👋</h2>
        <p className="text-gray-500">{format(new Date(), 'EEEE, MMMM d, yyyy')} — keep going, every day counts.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        <div className="bg-gradient-to-br from-indigo-500 to-purple-600 text-white rounded-2xl p-6 shadow-md">
          <div className="flex items-center justify-between mb-4">
            <Trophy className="w-8 h-8 opacity-80" />
            <span className="text-xs font-semibold px-2.5 py-1 bg-white/20 rounded-full">Current Streak</span>
          </div>
          <p className="text-4xl font-extrabold">{streak?.currentStreak ?? 0}</p>
          <p className="text-sm opacity-90 mt-1">Best: {streak?.longestStreak ?? 0} days</p>
        </div>

        <div className="bg-gradient-to-br from-emerald-500 to-teal-600 text-white rounded-2xl p-6 shadow-md">
          <div className="flex items-center justify-between mb-4">
            <CheckCircle className="w-8 h-8 opacity-80" />
            <span className="text-xs font-semibold px-2.5 py-1 bg-white/20 rounded-full">Today</span>
          </div>
          <p className="text-4xl font-extrabold">
            {todayEntry
              ? todayEntry.result
              : challenges.length === 0 ? 'No tasks' : 'Pending'}
          </p>
          <p className="text-sm opacity-90 mt-1">{challenges.length} challenges due today</p>
        </div>

        <div className="bg-gradient-to-br from-blue-500 to-cyan-600 text-white rounded-2xl p-6 shadow-md">
          <div className="flex items-center justify-between mb-4">
            <Users className="w-8 h-8 opacity-80" />
            <span className="text-xs font-semibold px-2.5 py-1 bg-white/20 rounded-full">Friends</span>
          </div>
          <p className="text-4xl font-extrabold">{friends.length}</p>
          <p className="text-sm opacity-90 mt-1">Accountability connections</p>
        </div>
      </div>

      <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100">
        <div className="flex items-center gap-2 mb-4">
          <Flame className="w-5 h-5 text-orange-500" />
          <h3 className="font-bold text-gray-900">{format(new Date(), 'MMMM yyyy')} — Daily Results</h3>
        </div>
        {calendarDays.length === 0 ? (
          <p className="text-sm text-gray-400">No data yet — start checking in daily.</p>
        ) : (
          <div className="flex flex-wrap gap-2">
            {calendarDays.map((d) => (
              <div key={d.date} title={`${d.date}: ${d.result}`}
                className={`w-9 h-9 rounded-lg flex items-center justify-center text-xs text-white font-semibold cursor-default ${resultColor[d.result]}`}>
                {parseInt(d.date.split('-')[2])}
              </div>
            ))}
          </div>
        )}
        <div className="flex gap-4 mt-4 text-xs text-gray-500">
          <span className="flex items-center gap-1"><span className="w-3 h-3 rounded bg-green-500 inline-block" />Success</span>
          <span className="flex items-center gap-1"><span className="w-3 h-3 rounded bg-amber-400 inline-block" />Partial</span>
          <span className="flex items-center gap-1"><span className="w-3 h-3 rounded bg-red-400 inline-block" />Missed</span>
        </div>
      </div>

      {friends.length > 0 && (
        <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100">
          <div className="flex items-center gap-2 mb-4">
            <TrendingUp className="w-5 h-5 text-indigo-500" />
            <h3 className="font-bold text-gray-900">Friends Activity</h3>
          </div>
          <div className="space-y-3">
            {friends.slice(0, 3).map((f) => (
              <div key={f.friendUserId} className="flex items-center justify-between p-3 rounded-xl bg-gray-50">
                <div className="flex items-center gap-3">
                  <div className="w-9 h-9 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-700 font-bold text-sm">
                    {(f.displayName ?? f.username).charAt(0).toUpperCase()}
                  </div>
                  <div>
                    <p className="text-sm font-semibold text-gray-900">{f.displayName ?? f.username}</p>
                    <p className="text-xs text-gray-400">🔥 {f.currentStreak} day streak</p>
                  </div>
                </div>
                <span className="text-sm font-medium text-emerald-600">
                  {f.completedToday}/{f.totalSharedChallenges} done
                </span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
