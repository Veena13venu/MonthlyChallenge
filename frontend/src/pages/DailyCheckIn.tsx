import { useState } from 'react'
import { format } from 'date-fns'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { CheckCircle2, MinusCircle, XCircle, ChevronRight } from 'lucide-react'
import { challengesApi } from '../api/challenges'
import { checkInsApi } from '../api/checkIns'
import type { Challenge, CheckIn, CheckInStatus, DaySummary, RecordCheckInPayload } from '../api/types'

const today = format(new Date(), 'yyyy-MM-dd')

const STATUS_OPTS: { value: CheckInStatus; label: string; color: string; icon: React.ReactNode }[] = [
  { value: 'COMPLETED',      label: 'Completed', color: 'bg-green-500 hover:bg-green-600', icon: <CheckCircle2 className="w-4 h-4" /> },
  { value: 'HALF_COMPLETED', label: 'Half done', color: 'bg-amber-400 hover:bg-amber-500', icon: <MinusCircle  className="w-4 h-4" /> },
  { value: 'MISSED',         label: 'Missed',    color: 'bg-red-400  hover:bg-red-500',   icon: <XCircle      className="w-4 h-4" /> },
]

const statusBadge: Record<CheckInStatus, string> = {
  COMPLETED:      'bg-green-100 text-green-700',
  HALF_COMPLETED: 'bg-amber-100 text-amber-700',
  MISSED:         'bg-red-100 text-red-700',
}

function ChallengeRow({ challenge: c, checkIn, onRecord, isPending }: {
  challenge: Challenge; checkIn: CheckIn | undefined
  onRecord: (p: RecordCheckInPayload) => void; isPending: boolean
}) {
  const [actualValue, setActualValue] = useState('')
  const [expanded, setExpanded] = useState(false)
  return (
    <div className="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
      <div className="flex items-center justify-between p-4 cursor-pointer hover:bg-gray-50 transition" onClick={() => setExpanded((v) => !v)}>
        <div className="flex-1 min-w-0">
          <p className="font-semibold text-gray-900 truncate">{c.title}</p>
          <div className="flex items-center gap-2 mt-0.5">
            <span className="text-xs text-gray-400">{c.category.toLowerCase()}</span>
            {c.targetValue != null && <span className="text-xs text-gray-400">· {c.targetValue} {c.targetUnit}</span>}
            {checkIn && (
              <span className={`text-xs font-medium px-2 py-0.5 rounded-full ${statusBadge[checkIn.status]}`}>
                {checkIn.status === 'COMPLETED' ? '✓ Done' : checkIn.status === 'HALF_COMPLETED' ? 'Half done' : 'Missed'}
              </span>
            )}
          </div>
        </div>
        <ChevronRight className={`w-4 h-4 text-gray-400 transition-transform ${expanded ? 'rotate-90' : ''}`} />
      </div>
      {expanded && (
        <div className="px-4 pb-4 border-t border-gray-50 pt-3 space-y-3">
          {c.targetValue != null && (
            <div>
              <label className="text-xs text-gray-500 mb-1 block">Actual value (optional)</label>
              <div className="flex gap-2 items-center">
                <input type="number" value={actualValue} onChange={(e) => setActualValue(e.target.value)}
                  placeholder={`of ${c.targetValue}`}
                  className="w-32 border border-gray-300 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500" />
                <span className="text-sm text-gray-400">{c.targetUnit}</span>
              </div>
            </div>
          )}
          <div className="flex gap-2 flex-wrap">
            {STATUS_OPTS.map((opt) => (
              <button key={opt.value} disabled={isPending}
                onClick={() => onRecord({ challengeId: c.id, status: opt.value, actualValue: actualValue ? Number(actualValue) : undefined })}
                className={`flex items-center gap-1.5 px-3 py-2 rounded-lg text-white text-sm font-medium transition disabled:opacity-50 ${opt.color} ${checkIn?.status === opt.value ? 'ring-2 ring-offset-1 ring-gray-400' : ''}`}>
                {opt.icon} {opt.label}
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

export default function DailyCheckIn() {
  const qc = useQueryClient()

  const { data: challengeData, isLoading: lc } = useQuery<Challenge[]>({ queryKey: ['challenges-today'], queryFn: () => challengesApi.getToday() })
  const { data: checkInData,   isLoading: li }  = useQuery<CheckIn[]>  ({ queryKey: ['check-ins', today], queryFn: () => checkInsApi.getForDate(today) })
  const { data: summaryData }                   = useQuery<DaySummary> ({ queryKey: ['summary-live', today], queryFn: () => checkInsApi.getSummary(today) })

  const challenges: Challenge[] = challengeData ?? []
  const checkIns:   CheckIn[]   = checkInData   ?? []
  const summary:    DaySummary | undefined = summaryData

  const mutation = useMutation({
    mutationFn: (p: RecordCheckInPayload) => checkInsApi.record(p),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['check-ins'] })
      qc.invalidateQueries({ queryKey: ['summary-live'] })
    },
  })

  const checkInMap = new Map(checkIns.map((ci) => [ci.challengeId, ci]))
  const completed  = checkIns.filter((ci) => ci.status === 'COMPLETED').length
  const halfDone   = checkIns.filter((ci) => ci.status === 'HALF_COMPLETED').length
  const totalPoints = summary?.totalPoints ?? (completed + halfDone * 0.5)
  const threshold   = summary?.minimumThreshold ?? 1
  const pct = Math.min(100, (totalPoints / Math.max(threshold, 1)) * 100)

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100">
        <h2 className="text-2xl font-bold text-gray-900">Daily Check-in</h2>
        <p className="text-gray-500 text-sm mt-1">{format(new Date(), 'EEEE, MMMM d')}</p>
        <div className="mt-4">
          <div className="flex justify-between text-sm mb-1">
            <span className="text-gray-600 font-medium">
              Progress: <span className="text-indigo-600 font-bold">{totalPoints.toFixed(1)}</span> / {threshold} pts
            </span>
            <span className={`font-semibold ${summary?.result === 'SUCCESS' ? 'text-green-600' : summary?.result === 'PARTIAL' ? 'text-amber-600' : 'text-gray-400'}`}>
              {summary?.result ?? 'IN PROGRESS'}
            </span>
          </div>
          <div className="w-full bg-gray-100 rounded-full h-3 overflow-hidden">
            <div className={`h-3 rounded-full transition-all duration-500 ${pct >= 100 ? 'bg-green-500' : pct > 50 ? 'bg-amber-400' : 'bg-indigo-500'}`}
              style={{ width: `${pct}%` }} />
          </div>
        </div>
      </div>

      {(lc || li) ? (
        <div className="flex justify-center py-20 text-gray-400">
          <svg className="animate-spin w-7 h-7 mr-2" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
          </svg>
          Loading…
        </div>
      ) : challenges.length === 0 ? (
        <div className="bg-white rounded-2xl border border-dashed border-gray-200 p-16 text-center">
          <CheckCircle2 className="w-12 h-12 mx-auto text-gray-300 mb-3" />
          <p className="font-semibold text-gray-700">No challenges due today</p>
          <p className="text-sm text-gray-400 mt-1">Go to My Challenges to create some.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {challenges.map((c) => (
            <ChallengeRow key={c.id} challenge={c} checkIn={checkInMap.get(c.id)}
              onRecord={(p) => mutation.mutate(p)} isPending={mutation.isPending} />
          ))}
        </div>
      )}
    </div>
  )
}
