import songsJson from './songs.json'
import songTagsJson from './songTags.json'

export const SITUATIONS = [
  { id: 1, icon: 'subway',   name: '출근길' },
  { id: 2, icon: 'umbrella', name: '비 오는 날' },
  { id: 3, icon: 'dumbbell', name: '운동할 때' },
  { id: 4, icon: 'car',      name: '드라이브' },
  { id: 5, icon: 'moon',     name: '잠들기 전' },
  { id: 6, icon: 'coffee',   name: '카페에서' },
  { id: 7, icon: 'book',     name: '공부할 때' },
  { id: 8, icon: 'broom',    name: '청소할 때' },
]

export const CONCEPTS = [
  { id: 1, icon: 'sparkle',  name: '신나게' },
  { id: 2, icon: 'sprout',   name: '새로운' },
  { id: 4, icon: 'rain',     name: '슬프게' },
  { id: 5, icon: 'cassette', name: '추억돋는' },
  { id: 7, icon: 'wave',     name: '잔잔하게' },
  { id: 9, icon: 'hug',      name: '위로받고' },
]

export const SONGS = songsJson as { id: number; title: string; artist: string; youtubeUrl: string; thumbnailUrl: string }[]
export const SONG_TAGS = songTagsJson as { songId: number; situationId: number; conceptId: number }[]
