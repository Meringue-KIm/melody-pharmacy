import { useEffect, useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { recommend, saveSong, unsaveSong, getSaved, recordPlay } from '../api/songApi'
import type { Song, Situation, Concept } from '../api/songApi'
import '../styles/Recommend.css'

type Tab = 'recommend' | 'saved'

export default function RecommendPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const situation = location.state?.situation as Situation
  const concept = location.state?.concept as Concept

  const [tab, setTab] = useState<Tab>('recommend')
  const [songs, setSongs] = useState<Song[]>([])
  const [savedSongs, setSavedSongs] = useState<Song[]>([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!situation || !concept) { navigate('/'); return }
    loadRecommend()
    loadSaved()
  }, [])

  const loadRecommend = async () => {
    setLoading(true)
    try {
      const res = await recommend(situation.id, concept.id)
      setSongs(res.data)
    } finally {
      setLoading(false)
    }
  }

  const loadSaved = async () => {
    const res = await getSaved(situation.id, concept.id)
    setSavedSongs(res.data)
  }

  const handleSave = async (song: Song) => {
    if (song.saved) {
      await unsaveSong(song.id)
    } else {
      await saveSong(song.id, situation.id, concept.id)
    }
    loadRecommend()
    loadSaved()
  }

  const handlePlay = (song: Song) => {
    recordPlay(song.id, situation.id, concept.id)
    window.open(song.youtubeUrl, '_blank')
  }

  const currentSongs = tab === 'recommend' ? songs : savedSongs

  return (
    <div className="recommend-container">
      <header className="main-header">
        <button className="back-btn" onClick={() => navigate('/concept', { state: { situation } })}>← 뒤로</button>
        <div className="logo">🎵 멜로디약국</div>
        <div />
      </header>

      <div className="recommend-info">
        <span className="tag">{situation?.icon} {situation?.name}</span>
        <span className="tag">{concept?.icon} {concept?.name}</span>
      </div>

      <div className="tab-bar">
        <button className={tab === 'recommend' ? 'active' : ''} onClick={() => setTab('recommend')}>
          추천
        </button>
        <button className={tab === 'saved' ? 'active' : ''} onClick={() => setTab('saved')}>
          저장소 {savedSongs.length > 0 && <span className="badge">{savedSongs.length}</span>}
        </button>
      </div>

      {tab === 'recommend' && (
        <button className="refresh-btn" onClick={loadRecommend}>🔄 다시 추천받기</button>
      )}

      <div className="song-list">
        {loading && <p className="loading">처방 중... 💊</p>}
        {!loading && currentSongs.length === 0 && (
          <p className="empty">
            {tab === 'recommend' ? '추천할 노래가 없어요.' : '저장된 노래가 없어요.'}
          </p>
        )}
        {currentSongs.map((song) => (
          <div key={song.id} className="song-card">
            {song.thumbnailUrl && (
              <img src={song.thumbnailUrl} alt={song.title} className="song-thumbnail" />
            )}
            <div className="song-info">
              <p className="song-title">{song.title}</p>
              <p className="song-artist">{song.artist}</p>
            </div>
            <div className="song-actions">
              <button className="play-btn" onClick={() => handlePlay(song)}>▶</button>
              <button
                className={`save-btn ${song.saved ? 'saved' : ''}`}
                onClick={() => handleSave(song)}
              >
                {song.saved ? '♥' : '♡'}
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
