import { SITUATIONS, CONCEPTS, SONGS, SONG_TAGS } from '../data/guestData'
import { getGuestSaved, saveGuestSong, unsaveGuestSong, getGuestHistory, addGuestHistory } from '../utils/guestMode'
import type { GuestSong } from '../utils/guestMode'

function shuffle<T>(arr: T[]): T[] {
  const a = [...arr]
  for (let i = a.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    ;[a[i], a[j]] = [a[j], a[i]]
  }
  return a
}

function toSong(s: typeof SONGS[0], saved: boolean): GuestSong {
  return { id: s.id, title: s.title, artist: s.artist, youtubeUrl: s.youtubeUrl, thumbnailUrl: s.thumbnailUrl, saved }
}

export const guestGetSituations = () =>
  Promise.resolve({ data: SITUATIONS })

export const guestGetConcepts = () =>
  Promise.resolve({ data: CONCEPTS })

export const guestGetComboCounts = (situationId: number) => {
  const counts: Record<number, number> = {}
  for (const c of CONCEPTS) {
    counts[c.id] = SONG_TAGS.filter(t => t.situationId === situationId && t.conceptId === c.id).length
  }
  return Promise.resolve({ data: counts })
}

export const guestRecommend = (situationId: number, conceptId: number) => {
  const savedIds = new Set(getGuestSaved().map(s => s.id))
  const taggedIds = new Set(
    SONG_TAGS.filter(t => t.situationId === situationId && t.conceptId === conceptId).map(t => t.songId)
  )
  const songs = shuffle(SONGS.filter(s => taggedIds.has(s.id))).map(s => toSong(s, savedIds.has(s.id)))
  return Promise.resolve({ data: songs })
}

export const guestSaveSong = (songId: number, situationId?: number, conceptId?: number) => {
  const song = SONGS.find(s => s.id === songId)
  if (!song) return Promise.reject(new Error('not found'))
  const sit = SITUATIONS.find(s => s.id === situationId)
  const con = CONCEPTS.find(c => c.id === conceptId)
  saveGuestSong({
    ...toSong(song, true),
    savedSituationId: sit?.id, savedSituationIcon: sit?.icon, savedSituationName: sit?.name,
    savedConceptId: con?.id, savedConceptIcon: con?.icon, savedConceptName: con?.name,
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
  const song = SONGS.find(s => s.id === songId)
  if (song) {
    const savedIds = new Set(getGuestSaved().map(s => s.id))
    addGuestHistory(toSong(song, savedIds.has(song.id)), situationId, conceptId, SITUATIONS, CONCEPTS)
  }
  return Promise.resolve({ data: null })
}

export const guestGetHistory = () => {
  const savedIds = new Set(getGuestSaved().map(s => s.id))
  const history = getGuestHistory().map(s => ({ ...s, saved: savedIds.has(s.id) }))
  return Promise.resolve({ data: history })
}
