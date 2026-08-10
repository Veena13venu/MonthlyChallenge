import { useState, useEffect } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Save } from 'lucide-react'
import { usersApi } from '../api/friendships'
import type { User } from '../api/types'

export default function Profile() {
  const qc = useQueryClient()
  const { data: userData, isLoading } = useQuery<User>({ queryKey: ['me'], queryFn: () => usersApi.getMe() })
  const user: User | undefined = userData

  const [displayName, setDisplayName] = useState('')
  const [targetValue, setTargetValue]  = useState('1')
  const [isPercentage, setIsPercentage] = useState(false)
  const [saved, setSaved] = useState(false)

  useEffect(() => {
    if (user) {
      setDisplayName(user.displayName ?? '')
      setTargetValue(String(user.minimumTargetValue))
      setIsPercentage(user.minimumTargetIsPercentage)
    }
  }, [user])

  const flash = () => { setSaved(true); setTimeout(() => setSaved(false), 2000) }

  const profileMutation = useMutation({
    mutationFn: () => usersApi.updateProfile({ displayName }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['me'] }); flash() },
  })
  const targetMutation = useMutation({
    mutationFn: () => usersApi.updateMinTarget({ value: Number(targetValue), isPercentage }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['me'] }); flash() },
  })

  if (isLoading) return (
    <div className="flex justify-center py-24 text-gray-400">
      <svg className="animate-spin w-7 h-7" fill="none" viewBox="0 0 24 24">
        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
      </svg>
    </div>
  )

  return (
    <div className="space-y-6 max-w-xl">
      <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100">
        <h2 className="text-2xl font-bold text-gray-900">Account Profile</h2>
        <p className="text-gray-500 text-sm mt-1">Update your display name and daily target settings.</p>
      </div>

      <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100 flex items-center gap-5">
        <div className="w-16 h-16 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-700 text-2xl font-extrabold shrink-0">
          {(user?.displayName ?? user?.username ?? 'U').charAt(0).toUpperCase()}
        </div>
        <div>
          <p className="text-lg font-bold text-gray-900">{user?.displayName ?? user?.username}</p>
          <p className="text-sm text-gray-400">@{user?.username}</p>
          <p className="text-xs text-gray-400 mt-0.5">{user?.email}</p>
        </div>
      </div>

      <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100 space-y-4">
        <h3 className="font-semibold text-gray-800">Display Name</h3>
        <input type="text" value={displayName} onChange={(e) => setDisplayName(e.target.value)} maxLength={100}
          className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500" />
        <button onClick={() => profileMutation.mutate()} disabled={profileMutation.isPending}
          className="flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700 disabled:opacity-50 transition">
          <Save className="w-4 h-4" />{profileMutation.isPending ? 'Saving…' : 'Save Name'}
        </button>
      </div>

      <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100 space-y-4">
        <h3 className="font-semibold text-gray-800">Minimum Daily Target</h3>
        <p className="text-sm text-gray-500">Points you must score per day for it to count as a "Success".</p>
        <div className="flex items-center gap-3">
          <input type="number" value={targetValue} onChange={(e) => setTargetValue(e.target.value)}
            min={0.5} step={isPercentage ? 5 : 0.5} max={isPercentage ? 100 : undefined}
            className="w-28 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500" />
          {[false, true].map((v) => (
            <button key={String(v)} type="button" onClick={() => setIsPercentage(v)}
              className={`px-3 py-1.5 rounded-lg text-sm font-medium border transition ${isPercentage === v ? 'bg-indigo-600 text-white border-indigo-600' : 'bg-white text-gray-600 border-gray-300 hover:border-indigo-400'}`}>
              {v ? '% Percentage' : '# Count'}
            </button>
          ))}
        </div>
        <p className="text-xs text-gray-400">
          {isPercentage ? `Complete at least ${targetValue}% of today's challenges.`
            : `Completed = 1 pt, Half = 0.5 pt. Need ${targetValue} pt(s) per day.`}
        </p>
        <button onClick={() => targetMutation.mutate()} disabled={targetMutation.isPending}
          className="flex items-center gap-2 px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700 disabled:opacity-50 transition">
          <Save className="w-4 h-4" />{targetMutation.isPending ? 'Saving…' : 'Save Target'}
        </button>
      </div>

      {saved && (
        <div className="fixed bottom-6 right-6 bg-green-600 text-white text-sm font-semibold px-5 py-3 rounded-xl shadow-lg">
          ✓ Saved successfully
        </div>
      )}
    </div>
  )
}
