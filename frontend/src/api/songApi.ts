import api from './axios'

export interface Situation {
  id: number
  name: string
  icon: string
}

export interface Concept {
  id: number
  name: string
  icon: string
}

export interface Song {
  id: number
  title: string
  artist: string
  youtubeUrl: string
  thumbnailUrl: string
  saved: boolean
  savedSituationId?: number
  savedSituationIcon?: string
  savedSituationName?: string
  savedConceptId?: number
  savedConceptIcon?: string
  savedConceptName?: string
}

export const getSituations = () =>
  api.get<Situation[]>('/api/situations')

export const getConcepts = () =>
  api.get<Concept[]>('/api/concepts')

export const recommend = (situationId: number, conceptId: number, excludePlayed = false) =>
  api.get<Song[]>('/api/songs/recommend', { params: { situationId, conceptId, excludePlayed } })

export const saveSong = (songId: number, situationId?: number, conceptId?: number) =>
  api.post(`/api/songs/${songId}/save`, null, {
    params: situationId && conceptId ? { situationId, conceptId } : undefined,
  })

export const unsaveSong = (songId: number) =>
  api.delete(`/api/songs/${songId}/save`)

export const getSaved = (situationId?: number, conceptId?: number) =>
  api.get<Song[]>('/api/songs/saved', { params: { situationId, conceptId } })

export const recordPlay = (songId: number, situationId?: number, conceptId?: number) =>
  api.post(`/api/songs/${songId}/play`, null, { params: { situationId, conceptId } })

export const getHistory = () =>
  api.get<Song[]>('/api/songs/history')
