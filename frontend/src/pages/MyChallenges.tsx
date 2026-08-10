import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { format } from 'date-fns'
import {
  Plus, Target, Pencil, Trash2, X, ChevronDown,
  Flame, BookOpen, Moon, Heart, Dumbbell, Brain, Utensils, Grid3X3,
} from 'lucide-react'
import { challengesApi } from '../api/challenges'
import type {
  Challenge, ChallengeCategory, ChallengeFrequency,
  ChallengeVisibility, CreateChallengePayload,
} from '../api/types'

// ─── helpers ──────────────────────────────────────────────────────────────────

const currentMonth = () => format(new Date(), 'yyyy-MM')

const CATEGORIES: { value: ChallengeCategory; label: string; icon: React.ReactNode }[] = [
  { value: 'HEALTH',       label: 'Health',       icon: <Heart    className="w-4 h-4" /> },
  { value: 'FITNESS',      label: 'Fitness',      icon: <Dumbbell className="w-4 h-4" /> },
  { value: 'LEARNING',     label: 'Learning',     icon: <BookOpen className="w-4 h-4" /> },
  { value: 'MINDFULNESS',  label: 'Mindfulness',  icon: <Brain    className="w-4 h-4" /> },
  { value: 'NUTRITION',    label: 'Nutrition',    icon: <Utensils className="w-4 h-4" /> },
  { value: 'SLEEP',        label: 'Sleep',        icon: <Moon     className="w-4 h-4" /> },
  { value: 'PRODUCTIVITY', label: 'Productivity', icon: <Flame    className="w-4 h-4" /> },
  { value: 'OTHER',        label: 'Other',        icon: <Grid3X3  className="w-4 h-4" /> },
]

const categoryLabel = (v: ChallengeCategory) =>
  CATEGORIES.find((c) => c.value === v)?.label ?? v

const categoryColors: Record<ChallengeCategory, string> = {
  HEALTH:       'bg-red-100 text-red-700',
  FITNESS:      'bg-orange-100 text-orange-700',
  LEARNING:     'bg-blue-100 text-blue-700',
  MINDFULNESS:  'bg-purple-100 text-purple-700',
  NUTRITION:    'bg-green-100 text-green-700',
  SLEEP:        'bg-indigo-100 text-indigo-700',
  PRODUCTIVITY: 'bg-yellow-100 text-yellow-700',
  OTHER:        'bg-gray-100 text-gray-700',
}

// ─── blank form ───────────────────────────────────────────────────────────────

const blankForm = (): CreateChallengePayload => ({
  title: '',
  description: '',
  category: 'HEALTH',
  frequency: 'DAILY',
  month: currentMonth(),
  visibility: 'SHARED',
  targetValue: undefined,
  targetUnit: '',
  reminderHour: undefined,
  reminderMinute: undefined,
})

// ─── ChallengeModal ───────────────────────────────────────────────────────────

interface ModalProps {
  initial?: Challenge | null
  onClose: () => void
  onSaved: () => void
}

function ChallengeModal({ initial, onClose, onSaved }: ModalProps) {
  const [form, setForm] = useState<CreateChallengePayload>(
    initial
      ? {
          title: initial.title,
          description: initial.description ?? '',
          category: initial.category,
          frequency: initial.frequency,
          month: initial.month,
          visibility: initial.visibility,
          targetValue: initial.targetValue ?? undefined,
          targetUnit: initial.targetUnit ?? '',
          reminderHour: initial.reminderHour ?? undefined,
          reminderMinute: initial.reminderMinute ?? undefined,
        }
      : blankForm(),
  )

  const [errors, setErrors] = useState<Record<string, string>>({})

  const createMutation = useMutation({
    mutationFn: (data: CreateChallengePayload) => challengesApi.create(data),
    onSuccess: () => { onSaved() },
    onError: (err: Error) => setErrors({ _: err.message }),
  })

  const updateMutation = useMutation({
    mutationFn: (data: CreateChallengePayload) =>
      challengesApi.update(initial!.id, data),
    onSuccess: () => { onSaved() },
    onError: (err: Error) => setErrors({ _: err.message }),
  })

  const set = <K extends keyof CreateChallengePayload>(
    key: K,
    value: CreateChallengePayload[K],
  ) => setForm((prev) => ({ ...prev, [key]: value }))

  const validate = () => {
    const e: Record<string, string> = {}
    if (!form.title.trim()) e.title = 'Title is required'
    if (!form.month)         e.month = 'Month is required'
    return e
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const errs = validate()
    if (Object.keys(errs).length) { setErrors(errs); return }

    const payload: CreateChallengePayload = {
      ...form,
      title: form.title.trim(),
      description: form.description?.trim() || undefined,
      targetUnit: form.targetUnit?.trim() || undefined,
      targetValue: form.targetValue ? Number(form.targetValue) : undefined,
      reminderHour: form.reminderHour !== undefined ? Number(form.reminderHour) : undefined,
      reminderMinute: form.reminderMinute !== undefined ? Number(form.reminderMinute) : undefined,
    }

    if (initial) {
      updateMutation.mutate(payload)
    } else {
      createMutation.mutate(payload)
    }
  }

  const isPending = createMutation.isPending || updateMutation.isPending

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-100">
          <h2 className="text-xl font-bold text-gray-900">
            {initial ? 'Edit Challenge' : 'Create Challenge'}
          </h2>
          <button
            onClick={onClose}
            className="p-2 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-gray-100 transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-5">
          {/* Global error */}
          {errors._ && (
            <div className="rounded-lg bg-red-50 border border-red-200 text-red-700 text-sm p-3">
              {errors._}
            </div>
          )}

          {/* Title */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Title <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={form.title}
              onChange={(e) => set('title', e.target.value)}
              placeholder="e.g. Drink 3L of water"
              maxLength={150}
              className={`w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 ${
                errors.title ? 'border-red-400' : 'border-gray-300'
              }`}
            />
            {errors.title && <p className="text-red-500 text-xs mt-1">{errors.title}</p>}
          </div>

          {/* Description */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Description <span className="text-gray-400 font-normal">(optional)</span>
            </label>
            <textarea
              value={form.description ?? ''}
              onChange={(e) => set('description', e.target.value)}
              rows={2}
              placeholder="Briefly describe this challenge..."
              maxLength={500}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 resize-none"
            />
          </div>

          {/* Category + Frequency in 2 columns */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
              <div className="relative">
                <select
                  value={form.category}
                  onChange={(e) => set('category', e.target.value as ChallengeCategory)}
                  className="w-full appearance-none border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 pr-8"
                >
                  {CATEGORIES.map((c) => (
                    <option key={c.value} value={c.value}>{c.label}</option>
                  ))}
                </select>
                <ChevronDown className="absolute right-2 top-2.5 w-4 h-4 text-gray-400 pointer-events-none" />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Frequency</label>
              <div className="relative">
                <select
                  value={form.frequency}
                  onChange={(e) => set('frequency', e.target.value as ChallengeFrequency)}
                  className="w-full appearance-none border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 pr-8"
                >
                  <option value="DAILY">Daily</option>
                  <option value="WEEKLY">Weekly</option>
                  <option value="MONTHLY">Monthly</option>
                </select>
                <ChevronDown className="absolute right-2 top-2.5 w-4 h-4 text-gray-400 pointer-events-none" />
              </div>
            </div>
          </div>

          {/* Month */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Month <span className="text-red-500">*</span>
            </label>
            <input
              type="month"
              value={form.month}
              onChange={(e) => set('month', e.target.value)}
              className={`w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 ${
                errors.month ? 'border-red-400' : 'border-gray-300'
              }`}
            />
            {errors.month && <p className="text-red-500 text-xs mt-1">{errors.month}</p>}
          </div>

          {/* Target value + unit */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Measurable Target <span className="text-gray-400 font-normal">(optional)</span>
            </label>
            <div className="flex gap-2">
              <input
                type="number"
                value={form.targetValue ?? ''}
                onChange={(e) => set('targetValue', e.target.value ? Number(e.target.value) : undefined)}
                placeholder="Value"
                min={0}
                step="0.1"
                className="w-28 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
              <input
                type="text"
                value={form.targetUnit ?? ''}
                onChange={(e) => set('targetUnit', e.target.value)}
                placeholder="Unit (e.g. Litres, pages, km)"
                className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
          </div>

          {/* Visibility */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Visibility</label>
            <div className="flex gap-3">
              {(['SHARED', 'PRIVATE'] as ChallengeVisibility[]).map((v) => (
                <button
                  key={v}
                  type="button"
                  onClick={() => set('visibility', v)}
                  className={`flex-1 py-2 rounded-lg text-sm font-medium border transition ${
                    form.visibility === v
                      ? 'bg-indigo-600 text-white border-indigo-600'
                      : 'bg-white text-gray-600 border-gray-300 hover:border-indigo-400'
                  }`}
                >
                  {v === 'SHARED' ? '👥 Shared with friends' : '🔒 Private'}
                </button>
              ))}
            </div>
          </div>

          {/* Reminder */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Daily Reminder <span className="text-gray-400 font-normal">(optional)</span>
            </label>
            <div className="flex items-center gap-2">
              <input
                type="number"
                value={form.reminderHour ?? ''}
                onChange={(e) => set('reminderHour', e.target.value !== '' ? Number(e.target.value) : undefined)}
                placeholder="HH"
                min={0} max={23}
                className="w-20 border border-gray-300 rounded-lg px-3 py-2 text-sm text-center focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
              <span className="text-gray-500 font-bold">:</span>
              <input
                type="number"
                value={form.reminderMinute ?? ''}
                onChange={(e) => set('reminderMinute', e.target.value !== '' ? Number(e.target.value) : undefined)}
                placeholder="MM"
                min={0} max={59}
                className="w-20 border border-gray-300 rounded-lg px-3 py-2 text-sm text-center focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
              <span className="text-xs text-gray-400">(24h format)</span>
            </div>
          </div>

          {/* Actions */}
          <div className="flex gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-2.5 rounded-lg border border-gray-300 text-sm font-medium text-gray-700 hover:bg-gray-50 transition"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isPending}
              className="flex-1 py-2.5 rounded-lg bg-indigo-600 text-white text-sm font-semibold hover:bg-indigo-700 disabled:opacity-50 transition"
            >
              {isPending ? 'Saving...' : initial ? 'Save Changes' : 'Create Challenge'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

// ─── ChallengeCard ────────────────────────────────────────────────────────────

interface CardProps {
  challenge: Challenge
  onEdit: (c: Challenge) => void
  onDelete: (id: string) => void
}

function ChallengeCard({ challenge: c, onEdit, onDelete }: CardProps) {
  return (
    <div className="bg-white rounded-xl border border-gray-100 shadow-sm p-5 flex flex-col gap-3 hover:shadow-md transition">
      <div className="flex items-start justify-between">
        <div className="flex-1 min-w-0">
          <h3 className="font-semibold text-gray-900 truncate">{c.title}</h3>
          {c.description && (
            <p className="text-sm text-gray-500 mt-0.5 line-clamp-2">{c.description}</p>
          )}
        </div>
        <div className="flex gap-1 ml-3 shrink-0">
          <button
            onClick={() => onEdit(c)}
            className="p-1.5 rounded-lg text-gray-400 hover:text-indigo-600 hover:bg-indigo-50 transition"
            title="Edit"
          >
            <Pencil className="w-4 h-4" />
          </button>
          <button
            onClick={() => onDelete(c.id)}
            className="p-1.5 rounded-lg text-gray-400 hover:text-red-600 hover:bg-red-50 transition"
            title="Delete"
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      </div>

      <div className="flex flex-wrap gap-2 text-xs">
        <span className={`px-2.5 py-1 rounded-full font-medium ${categoryColors[c.category]}`}>
          {categoryLabel(c.category)}
        </span>
        <span className="px-2.5 py-1 rounded-full font-medium bg-indigo-50 text-indigo-700">
          {c.frequency.charAt(0) + c.frequency.slice(1).toLowerCase()}
        </span>
        {c.targetValue != null && (
          <span className="px-2.5 py-1 rounded-full font-medium bg-gray-100 text-gray-600">
            {c.targetValue} {c.targetUnit}
          </span>
        )}
        <span className={`px-2.5 py-1 rounded-full font-medium ${
          c.visibility === 'SHARED' ? 'bg-green-50 text-green-700' : 'bg-gray-100 text-gray-500'
        }`}>
          {c.visibility === 'SHARED' ? '👥 Shared' : '🔒 Private'}
        </span>
      </div>
    </div>
  )
}

// ─── MyChallenges (main page) ─────────────────────────────────────────────────

export default function MyChallenges() {
  const qc = useQueryClient()
  const [month, setMonth] = useState(currentMonth())
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState<Challenge | null>(null)
  const [deleteConfirm, setDeleteConfirm] = useState<string | null>(null)

  const { data: challenges = [], isLoading, error } = useQuery({
    queryKey: ['challenges', month],
    queryFn: () => challengesApi.listForMonth(month),
  })

  const deleteMutation = useMutation({
    mutationFn: (id: string) => challengesApi.delete(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['challenges'] })
      setDeleteConfirm(null)
    },
  })

  const handleSaved = () => {
    qc.invalidateQueries({ queryKey: ['challenges'] })
    setShowModal(false)
    setEditing(null)
  }

  const openCreate = () => { setEditing(null); setShowModal(true) }
  const openEdit   = (c: Challenge) => { setEditing(c); setShowModal(true) }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100 flex items-center justify-between flex-wrap gap-4">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">My Challenges</h2>
          <p className="text-gray-500 text-sm mt-1">Manage your active habits for the selected month.</p>
        </div>
        <div className="flex items-center gap-3">
          <input
            type="month"
            value={month}
            onChange={(e) => setMonth(e.target.value)}
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
          <button
            onClick={openCreate}
            className="inline-flex items-center gap-2 px-4 py-2 rounded-lg bg-indigo-600 text-white text-sm font-semibold hover:bg-indigo-700 transition shadow-sm"
          >
            <Plus className="w-4 h-4" />
            Create Challenge
          </button>
        </div>
      </div>

      {/* Content */}
      {isLoading ? (
        <div className="flex items-center justify-center py-24 text-gray-400">
          <svg className="animate-spin w-8 h-8 mr-3" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
          </svg>
          Loading challenges…
        </div>
      ) : error ? (
        <div className="bg-red-50 border border-red-200 text-red-700 rounded-xl p-6 text-sm">
          Failed to load challenges: {(error as Error).message}
        </div>
      ) : challenges.length === 0 ? (
        <div className="bg-white rounded-2xl shadow-sm border border-dashed border-gray-200 p-16 text-center">
          <Target className="w-14 h-14 mx-auto text-gray-300 mb-4" />
          <p className="text-lg font-semibold text-gray-700">No challenges for {month}</p>
          <p className="text-sm text-gray-400 mt-1 mb-6">
            Create your first challenge or roll over challenges from a previous month.
          </p>
          <button
            onClick={openCreate}
            className="inline-flex items-center gap-2 px-5 py-2.5 rounded-lg bg-indigo-600 text-white text-sm font-semibold hover:bg-indigo-700 transition"
          >
            <Plus className="w-4 h-4" />
            Create your first challenge
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-5">
          {challenges.map((c) => (
            <ChallengeCard
              key={c.id}
              challenge={c}
              onEdit={openEdit}
              onDelete={(id) => setDeleteConfirm(id)}
            />
          ))}
        </div>
      )}

      {/* Create / Edit modal */}
      {showModal && (
        <ChallengeModal
          initial={editing}
          onClose={() => { setShowModal(false); setEditing(null) }}
          onSaved={handleSaved}
        />
      )}

      {/* Delete confirmation */}
      {deleteConfirm && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm p-6 space-y-4">
            <h3 className="text-lg font-bold text-gray-900">Delete Challenge?</h3>
            <p className="text-sm text-gray-500">
              This will archive the challenge. Your check-in history will be preserved.
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => setDeleteConfirm(null)}
                className="flex-1 py-2.5 rounded-lg border border-gray-300 text-sm font-medium text-gray-700 hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                onClick={() => deleteMutation.mutate(deleteConfirm)}
                disabled={deleteMutation.isPending}
                className="flex-1 py-2.5 rounded-lg bg-red-600 text-white text-sm font-semibold hover:bg-red-700 disabled:opacity-50"
              >
                {deleteMutation.isPending ? 'Deleting…' : 'Delete'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
