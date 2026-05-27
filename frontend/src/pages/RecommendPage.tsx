import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { recommend, saveSong, unsaveSong, getSaved, recordPlay, getSituations, getConcepts, getHistory } from '../api/songApi'
import type { Song, Situation, Concept } from '../api/songApi'
import '../styles/Recommend.css'

type Tab = 'recommend' | 'saved' | 'history'

function getVideoId(youtubeUrl: string): string {
  const match = youtubeUrl.match(/[?&]v=([^&]+)/)
  return match ? match[1] : ''
}

export default function RecommendPage() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const situationId = Number(searchParams.get('situationId'))
  const conceptId   = Number(searchParams.get('conceptId'))

  const [situation, setSituation] = useState<Situation | null>(null)
  const [concept,   setConcept]   = useState<Concept | null>(null)
  const [tab, setTab] = useState<Tab>('recommend')
  const [songs, setSongs] = useState<Song[]>([])
  const [savedSongs, setSavedSongs] = useState<Song[]>([])
  const [loading, setLoading] = useState(false)
  const [historySongs, setHistorySongs] = useState<Song[]>([])
  const [playingId, setPlayingId] = useState<number | null>(null)

  useEffect(() => {
    if (!situationId || !conceptId) { navigate('/'); return }
    getSituations().then(res => setSituation(res.data.find(s => s.id === situationId) ?? null))
    getConcepts().then(res => setConcept(res.data.find(c => c.id === conceptId) ?? null))
    loadRecommend()
    loadSaved()
    loadHistory()
  }, [situationId, conceptId])

  const loadRecommend = async () => {
    setLoading(true)
    try {
      const res = await recommend(situationId, conceptId)
      setSongs(res.data)
    } finally {
      setLoading(false)
    }
  }

  const loadSaved = async () => {
    const res = await getSaved(situationId, conceptId)
    setSavedSongs(res.data)
  }

  const loadHistory = async () => {
    const res = await getHistory()
    setHistorySongs(res.data)
  }

  const handleSave = async (song: Song) => {
    if (song.saved) {
      await unsaveSong(song.id)
    } else {
      await saveSong(song.id, situationId, conceptId)
    }
    loadRecommend()
    loadSaved()
  }

  const handlePlay = (song: Song) => {
    recordPlay(song.id, situationId, conceptId)
    if (playingId === song.id) {
      setPlayingId(null)
    } else {
      setPlayingId(song.id)
    }
  }

  const currentSongs = tab === 'recommend' ? songs : tab === 'saved' ? savedSongs : historySongs

  return (
    <div className="recommend-container">
      <header className="main-header">
        <button className="back-btn" onClick={() => navigate(`/concept?situationId=${situationId}`)}>← 뒤로</button>
        <div className="logo">🎵 멜로디약국</div>
        <div />
      </header>

      <div className="recommend-info">
        <span className="tag">{situation?.icon} {situation?.name}</span>
        <span className="tag">{concept?.icon} {concept?.name}</span>
      </div>

      <div className="tab-bar">
        <button className={tab === 'recommend' ? 'active' : ''} onClick={() => { setTab('recommend'); setPlayingId(null) }}>
          추천
        </button>
        <button className={tab === 'saved' ? 'active' : ''} onClick={() => { setTab('saved'); setPlayingId(null) }}>
          저장소 {savedSongs.length > 0 && <span className="badge">{savedSongs.length}</span>}
        </button>
        <button className={tab === 'history' ? 'active' : ''} onClick={() => { setTab('history'); setPlayingId(null) }}>
          최근
        </button>
      </div>

      {tab === 'recommend' && (
        <button className="refresh-btn" onClick={() => { setPlayingId(null); loadRecommend() }}>🔄 다시 추천받기</button>
      )}

      <div className="song-list">
        {loading && <p className="loading">처방 중... 💊</p>}
        {!loading && currentSongs.length === 0 && (
          <p className="empty">
            {tab === 'recommend' ? '추천할 노래가 없어요.'
             : tab === 'saved' ? '저장된 노래가 없어요.'
             : '아직 들은 노래가 없어요.'}
          </p>
        )}
        {currentSongs.map((song) => (
          <div key={song.id} className={`song-card ${playingId === song.id ? 'playing' : ''}`}>
            <div className="song-row">
              {song.thumbnailUrl && (
                <img src={song.thumbnailUrl} alt={song.title} className="song-thumbnail" />
              )}
              <div className="song-info">
                <p className="song-title">{song.title}</p>
                <p className="song-artist">{song.artist}</p>
              </div>
              <div className="song-actions">
                <button
                  className={`play-btn ${playingId === song.id ? 'playing' : ''}`}
                  onClick={() => handlePlay(song)}
                >
                  {playingId === song.id ? '■' : '▶'}
                </button>
                <button
                  className={`save-btn ${song.saved ? 'saved' : ''}`}
                  onClick={() => handleSave(song)}
                >
                  {song.saved ? '♥' : '♡'}
                </button>
              </div>
            </div>

            {playingId === song.id && (
              <div className="youtube-embed">
                <iframe
                  src={`https://www.youtube.com/embed/${getVideoId(song.youtubeUrl)}?autoplay=1`}
                  title={song.title}
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                  allowFullScreen
                />
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}
