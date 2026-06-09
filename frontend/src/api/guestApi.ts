import api from './axios'
import { SITUATIONS, CONCEPTS, PLAYLISTS } from '../data/guestData'
import { getGuestSaved, saveGuestSong, unsaveGuestSong, getGuestHistory, addGuestHistory } from '../utils/guestMode'
import type { GuestSong } from '../utils/guestMode'

// 서버에서 추천받은 곡을 저장/히스토리 조회에 활용하기 위한 인메모리 캐시
const songCache = new Map<number, GuestSong>()

function cacheSong(s: any): GuestSong {
  const entry: GuestSong = {
    id: s.id, title: s.title, artist: s.artist,
    youtubeUrl: s.youtubeUrl, thumbnailUrl: s.thumbnailUrl, saved: false,
  }
  songCache.set(s.id, entry)
  return entry
}

// 서버 API 직접 사용 (situations, concepts, playlists, recommend, combo-counts)
export const guestGetSituations = () => api.get('/api/situations')
export const guestGetConcepts   = () => api.get('/api/concepts')

export const guestGetComboCounts = (situationId: number) =>
  api.get<Record<number, number>>('/api/songs/combo-counts', { params: { situationId } })

export const guestRecommend = async (situationId: number, conceptId: number, _excludePlayed = false) => {
  const res = await api.get('/api/songs/recommend', { params: { situationId, conceptId } })
  const savedIds = new Set(getGuestSaved().map(s => s.id))
  const songs: GuestSong[] = res.data.map((s: any) => {
    const song = cacheSong(s)
    return { ...song, saved: savedIds.has(s.id) }
  })
  return { data: songs }
}

export const guestGetPlaylists = (situationId: number, conceptId: number) =>
  Promise.resolve({ data: PLAYLISTS.filter(p => p.situationId === situationId && p.conceptId === conceptId) })

// 저장/히스토리는 localStorage 유지
export const guestSaveSong = (songId: number, situationId?: number, conceptId?: number) => {
  const cached = songCache.get(songId)
  if (!cached) return Promise.reject(new Error('not found'))
  const sit = SITUATIONS.find(s => s.id === situationId)
  const con = CONCEPTS.find(c => c.id === conceptId)
  saveGuestSong({
    ...cached,
    saved: true,
    savedSituationId: sit?.id, savedSituationIcon: sit?.icon, savedSituationName: sit?.name,
    savedConceptId: con?.id,   savedConceptIcon: con?.icon,   savedConceptName: con?.name,
  })
  return Promise.resolve({ data: null })
}

export const guestUnsaveSong = (songId: number) => {
  unsaveGuestSong(songId)
  return Promise.resolve({ data: null })
}

export const guestGetSaved = () =>
  Promise.resolve({ data: getGuestSaved() })

export const guestRecordPlay = (songId: number, situationId?: number, conceptId?: number) => {
  const song = songCache.get(songId)
  if (song) {
    const savedIds = new Set(getGuestSaved().map(s => s.id))
    addGuestHistory({ ...song, saved: savedIds.has(songId) }, situationId, conceptId, SITUATIONS, CONCEPTS)
  }
  return Promise.resolve({ data: null })
}

export const guestGetHistory = () => {
  const savedIds = new Set(getGuestSaved().map(s => s.id))
  const history = getGuestHistory().map(s => ({ ...s, saved: savedIds.has(s.id) }))
  return Promise.resolve({ data: history })
}
