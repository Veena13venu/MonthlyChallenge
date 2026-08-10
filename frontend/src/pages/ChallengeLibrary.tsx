import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { format } from 'date-fns'
import { BookOpen, Plus, CheckCircle } from 'lucide-react'
import { challengesApi } from '../api/challenges'
import type { ChallengeTemplate, CreateChallengePayload } from '../api/types'

const catColors: Record<string, string> = {
  HEALTH: 'bg-red-100 text-red-700', FITNESS: 'bg-orange-100 text-orange-700',
  LEARNING: 'bg-blue-100 text-blue-700', MINDFULNESS: 'bg-purple-100 text-purple-700',
  NUTRITION: 'bg-green-100 text-green-700', SLEEP: 'bg-indigo-100 text-indigo-700',
  PRODUCTIVITY: 'bg-yellow-100 text-yellow-700', OTHER: 'bg-gray-100 text-gray-600',
}

export default function ChallengeLibrary() {
  const qc = useQueryClient()
  const [added, setAdded] = useState<Set<string>>(new Set())
  const month = format(new Date(), 'yyyy-MM')

  const { data: tmplData, isLoading } = useQuery<ChallengeTemplate[]>({
    queryKey: ['templates'],
    queryFn: () => challengesApi.getTemplates(),
  })
  const templates: ChallengeTemplate[] = tmplData ?? []

  const mutation = useMutation({
    mutationFn: (t: ChallengeTemplate) => {
      const payload: CreateChallengePayload = {
        title: t.title, description: t.description ?? undefined,
        category: t.category, frequency: t.suggestedFrequency, month,
        visibility: 'SHARED',
        targetValue: t.suggestedTargetValue ?? undefined,
        targetUnit: t.suggestedTargetUnit ?? undefined,
      }
      return challengesApi.create(payload)
    },
    onSuccess: (_data, t) => {
      setAdded((prev) => new Set([...prev, t.id]))
      qc.invalidateQueries({ queryKey: ['challenges'] })
    },
  })

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100">
        <div className="flex items-center gap-3">
          <BookOpen className="w-6 h-6 text-indigo-500" />
          <div>
            <h2 className="text-2xl font-bold text-gray-900">Challenge Library</h2>
            <p className="text-gray-500 text-sm mt-0.5">Pick a template and add it to {month}.</p>
          </div>
        </div>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-20 text-gray-400">
          <svg className="animate-spin w-7 h-7 mr-2" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
          </svg>
          Loading templates…
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-5">
          {templates.map((t) => {
            const isAdded = added.has(t.id)
            return (
              <div key={t.id} className="bg-white rounded-xl border border-gray-100 shadow-sm p-5 flex flex-col gap-3 hover:shadow-md transition">
                <div>
                  <h3 className="font-semibold text-gray-900">{t.title}</h3>
                  {t.description && <p className="text-sm text-gray-500 mt-1 line-clamp-2">{t.description}</p>}
                </div>
                <div className="flex flex-wrap gap-2 text-xs">
                  <span className={`px-2.5 py-1 rounded-full font-medium ${catColors[t.category] ?? 'bg-gray-100 text-gray-600'}`}>
                    {t.category.charAt(0) + t.category.slice(1).toLowerCase()}
                  </span>
                  <span className="px-2.5 py-1 rounded-full font-medium bg-indigo-50 text-indigo-700">
                    {t.suggestedFrequency.charAt(0) + t.suggestedFrequency.slice(1).toLowerCase()}
                  </span>
                  {t.suggestedTargetValue != null && (
                    <span className="px-2.5 py-1 rounded-full font-medium bg-gray-100 text-gray-600">
                      {t.suggestedTargetValue} {t.suggestedTargetUnit}
                    </span>
                  )}
                </div>
                <button onClick={() => !isAdded && mutation.mutate(t)}
                  disabled={mutation.isPending || isAdded}
                  className={`mt-auto flex items-center justify-center gap-2 py-2 rounded-lg text-sm font-semibold transition ${
                    isAdded ? 'bg-green-50 text-green-700 border border-green-200 cursor-default'
                            : 'bg-indigo-600 text-white hover:bg-indigo-700 disabled:opacity-50'}`}>
                  {isAdded ? <><CheckCircle className="w-4 h-4" /> Added</> : <><Plus className="w-4 h-4" /> Add to {month}</>}
                </button>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
