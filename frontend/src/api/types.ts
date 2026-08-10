// ─── Shared enums ────────────────────────────────────────────────────────────

export type ChallengeCategory =
  | 'HEALTH' | 'FITNESS' | 'LEARNING' | 'MINDFULNESS'
  | 'NUTRITION' | 'SLEEP' | 'PRODUCTIVITY' | 'OTHER'

export type ChallengeFrequency = 'DAILY' | 'WEEKLY' | 'MONTHLY'
export type ChallengeVisibility = 'SHARED' | 'PRIVATE'
export type CheckInStatus = 'COMPLETED' | 'HALF_COMPLETED' | 'MISSED'
export type DayResult = 'SUCCESS' | 'PARTIAL' | 'MISSED'
export type FriendshipStatus = 'PENDING' | 'ACCEPTED' | 'DECLINED'

// ─── User ─────────────────────────────────────────────────────────────────────

export interface User {
  id: string
  username: string
  displayName: string
  email: string
  profilePhotoUrl: string | null
  minimumTargetValue: number
  minimumTargetIsPercentage: boolean
}

// ─── Challenge ────────────────────────────────────────────────────────────────

export interface Challenge {
  id: string
  title: string
  description: string | null
  category: ChallengeCategory
  frequency: ChallengeFrequency
  month: string          // "YYYY-MM"
  visibility: ChallengeVisibility
  targetValue: number | null
  targetUnit: string | null
  reminderHour: number | null
  reminderMinute: number | null
  weeklyDueDays: string[] | null
  monthlyDueDay: number | null
  active: boolean
}

export interface ChallengeTemplate {
  id: string
  title: string
  description: string | null
  category: ChallengeCategory
  suggestedFrequency: ChallengeFrequency
  suggestedTargetValue: number | null
  suggestedTargetUnit: string | null
}

export interface CreateChallengePayload {
  title: string
  description?: string
  category: ChallengeCategory
  frequency: ChallengeFrequency
  month: string          // "YYYY-MM"
  visibility?: ChallengeVisibility
  targetValue?: number
  targetUnit?: string
  reminderHour?: number
  reminderMinute?: number
  weeklyDueDays?: string[]
  monthlyDueDay?: number
}

export interface UpdateChallengePayload {
  title?: string
  description?: string
  category?: ChallengeCategory
  visibility?: ChallengeVisibility
  targetValue?: number
  targetUnit?: string
  reminderHour?: number
  reminderMinute?: number
  weeklyDueDays?: string[]
  monthlyDueDay?: number
}

// ─── Check-in ─────────────────────────────────────────────────────────────────

export interface CheckIn {
  id: string
  challengeId: string
  date: string           // "YYYY-MM-DD"
  status: CheckInStatus
  actualValue: number | null
  pointValue: number
}

export interface RecordCheckInPayload {
  challengeId: string
  status: CheckInStatus
  actualValue?: number
}

export interface DaySummary {
  date: string
  totalPoints: number
  minimumThreshold: number
  result: DayResult
}

// ─── Streak ───────────────────────────────────────────────────────────────────

export interface Streak {
  currentStreak: number
  longestStreak: number
  lastSuccessDate: string | null
}

// ─── Friendship ───────────────────────────────────────────────────────────────

export interface Friendship {
  id: string
  requesterId: string
  addresseeId: string
  status: FriendshipStatus
  createdAt: string
}

// ─── Friends feed ─────────────────────────────────────────────────────────────

export interface FriendChallengeEntry {
  challengeId: string
  title: string
  status: CheckInStatus | null
}

export interface FriendFeedEntry {
  friendUserId: string
  username: string
  displayName: string
  profilePhotoUrl: string | null
  currentStreak: number
  totalSharedChallenges: number
  completedToday: number
  halfCompletedToday: number
  sharedChallenges: FriendChallengeEntry[]
}

// ─── Dashboard ────────────────────────────────────────────────────────────────

export interface ChallengeCompletionRate {
  challengeId: string
  challengeTitle: string
  totalDueDays: number
  completedDays: number
  halfCompletedDays: number
  missedDays: number
  completionPercentage: number
}
