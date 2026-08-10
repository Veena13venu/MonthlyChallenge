import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Users, UserPlus, Check, X, UserMinus, Search, Flame, Clock } from 'lucide-react'
import { friendshipsApi, usersApi } from '../api/friendships'
import { dashboardApi } from '../api/dashboard'
import type { Friendship, User, FriendFeedEntry } from '../api/types'

export default function Friends() {
  const qc = useQueryClient()
  const [searchQ, setSearchQ] = useState('')
  const [searchResult, setSearchResult] = useState<User[]>([])
  const [searching, setSearching] = useState(false)

  const { data: friendData }  = useQuery<Friendship[]>   ({ queryKey: ['friendships'],         queryFn: () => friendshipsApi.getAccepted() })
  const { data: pendingData } = useQuery<Friendship[]>   ({ queryKey: ['friendships-pending'], queryFn: () => friendshipsApi.getPending() })
  const { data: feedData }    = useQuery<FriendFeedEntry[]>({ queryKey: ['friends-feed'],      queryFn: () => dashboardApi.getFriendsFeed() })
  const { data: meData }      = useQuery<User>           ({ queryKey: ['me'],                  queryFn: () => usersApi.getMe() })

  const friends: Friendship[]     = friendData  ?? []
  const pending: Friendship[]     = pendingData  ?? []
  const feed:    FriendFeedEntry[] = feedData    ?? []
  const myId = meData?.id

  // Build sets of user IDs we already have a relationship with
  const acceptedIds = new Set(
    friends.map((f) => (f.requesterId === myId ? f.addresseeId : f.requesterId))
  )
  const pendingOutIds = new Set(
    pending.filter((f) => f.requesterId === myId).map((f) => f.addresseeId)
  )
  const pendingInIds = new Set(
    pending.filter((f) => f.addresseeId === myId).map((f) => f.requesterId)
  )

  const sendMutation   = useMutation({ mutationFn: (id: string) => friendshipsApi.sendRequest(id),  onSuccess: () => qc.invalidateQueries({ queryKey: ['friendships-pending'] }) })
  const acceptMutation = useMutation({ mutationFn: (id: string) => friendshipsApi.accept(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['friendships'] }); qc.invalidateQueries({ queryKey: ['friendships-pending'] }); qc.invalidateQueries({ queryKey: ['friends-feed'] }) } })
  const declineMutation = useMutation({ mutationFn: (id: string) => friendshipsApi.decline(id), onSuccess: () => qc.invalidateQueries({ queryKey: ['friendships-pending'] }) })
  const removeMutation  = useMutation({ mutationFn: (id: string) => friendshipsApi.remove(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['friendships'] }); qc.invalidateQueries({ queryKey: ['friends-feed'] }) } })

  const handleSearch = async () => {
    if (!searchQ.trim()) return
    setSearching(true)
    try { setSearchResult(await usersApi.search(searchQ.trim())) }
    catch { setSearchResult([]) }
    finally { setSearching(false) }
  }

  const incoming = pending.filter((p) => myId && p.addresseeId === myId)
  const outgoing  = pending.filter((p) => myId && p.requesterId === myId)

  const getAddStatus = (userId: string): 'none' | 'friends' | 'pending_out' | 'pending_in' | 'self' => {
    if (userId === myId) return 'self'
    if (acceptedIds.has(userId)) return 'friends'
    if (pendingOutIds.has(userId)) return 'pending_out'
    if (pendingInIds.has(userId)) return 'pending_in'
    return 'none'
  }

  return (
    <div className="space-y-6">
      <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100">
        <h2 className="text-2xl font-bold text-gray-900">Friends &amp; Accountability</h2>
        <p className="text-gray-500 text-sm mt-1">Connect with friends and track each other's progress.</p>
      </div>

      {/* Search */}
      <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100 space-y-3">
        <h3 className="font-semibold text-gray-800 flex items-center gap-2"><UserPlus className="w-4 h-4 text-indigo-500" /> Find Friends</h3>
        <div className="flex gap-2">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-2.5 w-4 h-4 text-gray-400" />
            <input type="text" value={searchQ} onChange={(e) => setSearchQ(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSearch()} placeholder="Search by username…"
              className="w-full pl-9 pr-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500" />
          </div>
          <button onClick={handleSearch} disabled={searching}
            className="px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-lg hover:bg-indigo-700 disabled:opacity-50 transition">
            Search
          </button>
        </div>
        {searchResult.length > 0 && (
          <div className="space-y-2">
            {searchResult.map((u) => (
              <div key={u.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-700 font-bold text-sm">
                    {(u.displayName ?? u.username).charAt(0).toUpperCase()}
                  </div>
                  <div><p className="text-sm font-semibold text-gray-900">{u.displayName}</p><p className="text-xs text-gray-400">@{u.username}</p></div>
                </div>
                {(() => {
                  const status = getAddStatus(u.id)
                  if (status === 'self') return null
                  if (status === 'friends') return (
                    <span className="flex items-center gap-1 px-3 py-1.5 text-xs font-semibold bg-green-100 text-green-700 rounded-lg">
                      <Check className="w-3.5 h-3.5" /> Friends
                    </span>
                  )
                  if (status === 'pending_out') return (
                    <span className="flex items-center gap-1 px-3 py-1.5 text-xs font-semibold bg-amber-100 text-amber-700 rounded-lg">
                      <Clock className="w-3.5 h-3.5" /> Pending
                    </span>
                  )
                  if (status === 'pending_in') return (
                    <button onClick={() => {
                      const req = pending.find((p) => p.requesterId === u.id)
                      if (req) acceptMutation.mutate(req.id)
                    }} className="flex items-center gap-1 px-3 py-1.5 text-xs font-semibold bg-green-600 text-white rounded-lg hover:bg-green-700 transition">
                      <Check className="w-3.5 h-3.5" /> Accept
                    </button>
                  )
                  return (
                    <button onClick={() => sendMutation.mutate(u.id)} disabled={sendMutation.isPending}
                      className="flex items-center gap-1 px-3 py-1.5 text-xs font-semibold bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:opacity-50 transition">
                      <UserPlus className="w-3.5 h-3.5" /> Add
                    </button>
                  )
                })()}
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Incoming requests */}
      {incoming.length > 0 && (
        <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100 space-y-3">
          <h3 className="font-semibold text-gray-800">Incoming Requests ({incoming.length})</h3>
          {incoming.map((f) => (
            <div key={f.id} className="flex items-center justify-between p-3 bg-indigo-50 rounded-xl">
              <div><p className="text-sm font-semibold text-gray-900">User {f.requesterId.slice(0, 8)}…</p><p className="text-xs text-gray-400">wants to connect</p></div>
              <div className="flex gap-2">
                <button onClick={() => acceptMutation.mutate(f.id)} className="p-2 rounded-lg bg-green-500 text-white hover:bg-green-600" title="Accept"><Check className="w-4 h-4" /></button>
                <button onClick={() => declineMutation.mutate(f.id)} className="p-2 rounded-lg bg-gray-200 text-gray-600 hover:bg-gray-300" title="Decline"><X className="w-4 h-4" /></button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Friends feed */}
      <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100 space-y-3">
        <h3 className="font-semibold text-gray-800 flex items-center gap-2"><Users className="w-4 h-4 text-indigo-500" /> Friends ({friends.length})</h3>
        {feed.length === 0 ? (
          <div className="border border-dashed border-gray-200 rounded-xl p-10 text-center">
            <Users className="w-10 h-10 mx-auto text-gray-300 mb-3" />
            <p className="text-sm font-medium text-gray-600">No friends yet</p>
            <p className="text-xs text-gray-400 mt-1">Search above to add accountability friends.</p>
          </div>
        ) : feed.map((f) => {
          const fr = friends.find((x) => x.requesterId === f.friendUserId || x.addresseeId === f.friendUserId)
          return (
            <div key={f.friendUserId} className="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-700 font-bold">
                  {(f.displayName ?? f.username).charAt(0).toUpperCase()}
                </div>
                <div>
                  <p className="text-sm font-semibold text-gray-900">{f.displayName}</p>
                  <p className="text-xs text-gray-400 flex items-center gap-1">
                    <Flame className="w-3 h-3 text-orange-400" /> {f.currentStreak} day streak · {f.completedToday}/{f.totalSharedChallenges} done
                  </p>
                </div>
              </div>
              {fr && (
                <button onClick={() => removeMutation.mutate(fr.id)}
                  className="p-2 rounded-lg text-gray-400 hover:text-red-500 hover:bg-red-50 transition" title="Remove friend">
                  <UserMinus className="w-4 h-4" />
                </button>
              )}
            </div>
          )
        })}
      </div>

      {outgoing.length > 0 && (
        <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100 space-y-3">
          <h3 className="font-semibold text-gray-800">Sent Requests ({outgoing.length})</h3>
          {outgoing.map((f) => (
            <div key={f.id} className="p-3 bg-gray-50 rounded-xl">
              <p className="text-sm text-gray-700">To: {f.addresseeId.slice(0, 8)}… <span className="text-xs text-amber-500 font-medium">· Pending</span></p>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
