import { useQuery } from '@tanstack/react-query'
import { Flame, Trophy, Calendar } from 'lucide-react'
import { format, parseISO } from 'date-fns'
import { dashboardApi } from '../api/dashboard'
import type { DaySummary, DayResult } from '../api/types'

const resultColor: Record<DayResult, string> = {
  SUCCESS: 'bg-green-500',
  PARTIAL: 'bg-amber-400',
  MISSED:  'bg-red-400',
}

const thisMonth = format(new Date(), 'yyyy-MM')

export default function Streaks() {
  const streakQuery  = useQuery({ queryKey: ['streak'],            queryFn: dashboardApi.getStreak })
  const calendarQuery = useQuery({ queryKey: ['calendar', thisMonth], queryFn: () => dashboardApi.getCalendar(thisMonth) })

  const currentStreak = streakQuery.data?.currentStreak ?? 0
  const longestStreak = streakQuery.data?.longestStreak ?? 0
  const lastSuccess   = streakQuery.data?.lastSuccessDate ?? null

  const days: DaySummary[] = (calendarQuery.data as DaySummary[] | undefined) ?? []
  const successDays = days.filter((d) => d.result === 'SUCCESS').length
  const partialDays = days.filter((d) => d.result === 'PARTIAL').length

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100">
        <h2 className="text-2xl font-bold text-gray-900">Streaks &amp; Achievements</h2>
        <p className="text-gray-500 text-sm mt-1">Track consecutive successful days and personal records.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
        <div className="bg-gradient-to-br from-orange-400 to-red-500 text-white rounded-2xl p-7 shadow-md flex items-center gap-5">
          <Flame className="w-14 h-14 opacity-90 shrink-0" />
          <div>
            <p className="text-sm font-semibold opacity-80">Current Streak</p>
            <p className="text-5xl font-black">{currentStreak}</p>
            <p className="text-sm opacity-75 mt-0.5">consecutive days</p>
          </div>
        </div>

        <div className="bg-gradient-to-br from-indigo-500 to-purple-600 text-white rounded-2xl p-7 shadow-md flex items-center gap-5">
          <Trophy className="w-14 h-14 opacity-90 shrink-0" />
          <div>
            <p className="text-sm font-semibold opacity-80">Longest Streak</p>
            <p className="text-5xl font-black">{longestStreak}</p>
            <p className="text-sm opacity-75 mt-0.5">
              {lastSuccess
                ? `Last success: ${format(parseISO(lastSuccess), 'MMM d')}`
                : 'all-time record'}
            </p>
          </div>
        </div>
      </div>

      <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100">
        <div className="flex items-center gap-2 mb-4">
          <Calendar className="w-5 h-5 text-indigo-500" />
          <h3 className="font-bold text-gray-900">{format(new Date(), 'MMMM yyyy')} — Calendar</h3>
        </div>

        {days.length === 0 ? (
          <p className="text-sm text-gray-400">No data yet for this month. Start checking in daily!</p>
        ) : (
          <>
            <div className="flex flex-wrap gap-2 mb-4">
              {days.map((d) => (
                <div
                  key={d.date}
                  title={`${d.date}: ${d.result}`}
                  className={`w-10 h-10 rounded-lg flex items-center justify-center text-xs text-white font-bold cursor-default ${resultColor[d.result]}`}
                >
                  {parseInt(d.date.split('-')[2])}
                </div>
              ))}
            </div>
            <div className="grid grid-cols-3 gap-4 text-center">
              <div className="bg-green-50 rounded-xl p-3">
                <p className="text-2xl font-black text-green-700">{successDays}</p>
                <p className="text-xs text-green-600 font-medium">Success days</p>
              </div>
              <div className="bg-amber-50 rounded-xl p-3">
                <p className="text-2xl font-black text-amber-600">{partialDays}</p>
                <p className="text-xs text-amber-500 font-medium">Partial days</p>
              </div>
              <div className="bg-red-50 rounded-xl p-3">
                <p className="text-2xl font-black text-red-500">{days.length - successDays - partialDays}</p>
                <p className="text-xs text-red-400 font-medium">Missed days</p>
              </div>
            </div>
          </>
        )}

        <div className="flex gap-4 mt-4 text-xs text-gray-400">
          <span className="flex items-center gap-1"><span className="w-3 h-3 rounded bg-green-500 inline-block" />Success</span>
          <span className="flex items-center gap-1"><span className="w-3 h-3 rounded bg-amber-400 inline-block" />Partial</span>
          <span className="flex items-center gap-1"><span className="w-3 h-3 rounded bg-red-400 inline-block" />Missed</span>
        </div>
      </div>
    </div>
  )
}
