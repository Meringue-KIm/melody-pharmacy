export const isGuest = () =>
  !localStorage.getItem('token') && localStorage.getItem('guestMode') === 'true'

export const enterGuestMode = () => localStorage.setItem('guestMode', 'true')

export const exitGuestMode = () => localStorage.removeItem('guestMode')

const GUEST_SAVED_KEY = 'guestSaved'
const GUEST_HISTORY_KEY = 'guestHistory'

export interface GuestSong {
  id: number; title: string; artist: string
  youtubeUrl: string; thumbnailUrl: string; saved: boolean
  savedSituationId?: number; savedSituationIcon?: string; savedSituationName?: string
  savedConceptId?: number; savedConceptIcon?: string; savedConceptName?: string
}

export const getGuestSaved = (): GuestSong[] => {
  try { return JSON.parse(localStorage.getItem(GUEST_SAVED_KEY) || '[]') } catch { return [] }
}

export const saveGuestSong = (song: GuestSong) => {
  const saved = getGuestSaved().filter(s => s.id !== song.id)
  localStorage.setItem(GUEST_SAVED_KEY, JSON.stringify([...saved, { ...song, saved: true }]))
}

export const unsaveGuestSong = (songId: number) => {
  localStorage.setItem(GUEST_SAVED_KEY, JSON.stringify(getGuestSaved().filter(s => s.id !== songId)))
}

export const getGuestHistory = (): GuestSong[] => {
  try { return JSON.parse(localStorage.getItem(GUEST_HISTORY_KEY) || '[]') } catch { return [] }
}

export const migrateGuestDataToServer = async (saveFn: (id: number, sitId?: number, conId?: number) => Promise<any>) => {
  const saved = getGuestSaved()
  if (saved.length === 0) { exitGuestMode(); return }
  await Promise.allSettled(saved.map(s => saveFn(s.id, s.savedSituationId, s.savedConceptId)))
  localStorage.removeItem(GUEST_SAVED_KEY)
  localStorage.removeItem(GUEST_HISTORY_KEY)
  exitGuestMode()
}

export const addGuestHistory = (song: GuestSong, situationId?: number, conceptId?: number, situations?: any[], concepts?: any[]) => {
  const sit = situations?.find((s: any) => s.id === situationId)
  const con = concepts?.find((c: any) => c.id === conceptId)
  const entry: GuestSong = {
    ...song,
    savedSituationId: sit?.id, savedSituationIcon: sit?.icon, savedSituationName: sit?.name,
    savedConceptId: con?.id, savedConceptIcon: con?.icon, savedConceptName: con?.name,
  }
  const prev = getGuestHistory().filter(s => s.id !== song.id)
  localStorage.setItem(GUEST_HISTORY_KEY, JSON.stringify([entry, ...prev].slice(0, 20)))
}
