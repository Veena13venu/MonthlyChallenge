import { useState } from 'react'
import { format } from 'date-fns'
import { useQuery } from '@tanstack/react-query'
import { Calendar, TrendingUp } from 'lucide-react'
import { dashboardApi } from '../api/dashboard'
import type { DaySummary, ChallengeCompletionRate, DayResult } from '../api/types'

const resultColor: Record<DayResult, string> = {
  SUCCESS: 'bg-green-500',
  PARTIAL: 'bg-amber-400',
  MISSED:  'bg-red-400',
}

export default function HistoryReports() {
  const [month, setMonth] = useState(format(new Date(), 'yyyy-MM'))

  const { data: calData,  isLoading: calLoading }   = useQuery<DaySummary[]>           ({ queryKey: ['calendar', month],          queryFn: () => dashboardApi.getCalendar(month) })
  const { data: rateData, isLoading: ratesLoading }  = useQuery<ChallengeCompletionRate[]>({ queryKey: ['completion-rates', month], queryFn: () => dashboardApi.getCompletionRates(month) })

  const calendar: DaySummary[]            = calData  ?? []
  const rates:    ChallengeCompletionRate[] = rateData ?? []

  const successDays = calendar.filter((d) => d.result === 'SUCCESS').length
  const partialDays = calendar.filter((d) => d.result === 'PARTIAL').length
  const missedDays  = calendar.length - successDays - partialDays

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100 flex items-center justify-between flex-wrap gap-4">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">History &amp; Reports</h2>
          <p className="text-gray-500 text-sm mt-1">Review past performance and monthly trends.</p>
        </div>
        <input type="month" value={month} onChange={(e) => setMonth(e.target.value)}
          className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500" />
      </div>

      <div className="grid grid-cols-3 gap-4">
        {[
          { label: 'Success days', count: successDays, cls: 'from-green-400 to-emerald-500' },
          { label: 'Partial days', count: partialDays, cls: 'from-amber-400 to-orange-400' },
          { label: 'Missed days',  count: missedDays,  cls: 'from-red-400 to-rose-500' },
        ].map((s) => (
          <div key={s.label} className={`bg-gradient-to-br ${s.cls} text-white rounded-2xl p-5 shadow-sm`}>
            <p className="text-3xl font-extrabold">{s.count}</p>
            <p className="text-sm opacity-90 mt-1">{s.label}</p>
          </div>
        ))}
      </div>

      <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100">
        <div className="flex items-center gap-2 mb-4">
          <Calendar className="w-5 h-5 text-indigo-500" />
          <h3 className="font-bold text-gray-900">Daily Calendar — {month}</h3>
        </div>
        {calLoading ? <p className="text-sm text-gray-400 animate-pulse">Loading…</p>
          : calendar.length === 0 ? <p className="text-sm text-gray-400">No check-in data for {month}.</p>
          : (
            <div className="flex flex-wrap gap-2">
              {calendar.map((d) => (
                <div key={d.date} title={`${d.date}: ${d.result}`}
                  className={`w-10 h-10 rounded-lg flex items-center justify-center text-xs text-white font-bold cursor-default ${resultColor[d.result]}`}>
                  {parseInt(d.date.split('-')[2])}
                </div>
              ))}
            </div>
          )}
        <div className="flex gap-4 mt-3 text-xs text-gray-400">
          <span className="flex items-center gap-1"><span className="w-3 h-3 rounded bg-green-500 inline-block" />Success</span>
          <span className="flex items-center gap-1"><span className="w-3 h-3 rounded bg-amber-400 inline-block" />Partial</span>
          <span className="flex items-center gap-1"><span className="w-3 h-3 rounded bg-red-400 inline-block" />Missed</span>
        </div>
      </div>

      <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100">
        <div className="flex items-center gap-2 mb-4">
          <TrendingUp className="w-5 h-5 text-indigo-500" />
          <h3 className="font-bold text-gray-900">Per-Challenge Completion — {month}</h3>
        </div>
        {ratesLoading ? <p className="text-sm text-gray-400 animate-pulse">Loading…</p>
          : rates.length === 0 ? <p className="text-sm text-gray-400">No challenge data for {month}.</p>
          : (
            <div className="space-y-4">
              {rates.map((r) => {
                const pct = Math.round(r.completionPercentage)
                return (
                  <div key={r.challengeId}>
                    <div className="flex justify-between text-sm mb-1">
                      <span className="font-medium text-gray-800 truncate max-w-xs">{r.challengeTitle}</span>
                      <span className="font-bold text-indigo-600 shrink-0 ml-2">{pct}%</span>
                    </div>
                    <div className="w-full bg-gray-100 rounded-full h-2.5 overflow-hidden">
                      <div className={`h-2.5 rounded-full ${pct >= 80 ? 'bg-green-500' : pct >= 50 ? 'bg-amber-400' : 'bg-red-400'}`}
                        style={{ width: `${pct}%` }} />
                    </div>
                    <p className="text-xs text-gray-400 mt-0.5">
                      ✓ {r.completedDays} · ½ {r.halfCompletedDays} · ✗ {r.missedDays} / {r.totalDueDays} total
                    </p>
                  </div>
                )
              })}
            </div>
          )}
      </div>
    </div>
  )
}
