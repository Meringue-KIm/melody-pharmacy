import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { recommend, saveSong, unsaveSong, getSaved, recordPlay, getSituations, getConcepts, getHistory } from '../api/songApi'
import type { Song, Situation, Concept } from '../api/songApi'
import '../styles/Recommend.css'

type Tab = 'recommend' | 'saved' | 'history'

function getVideoId(url: string) {
  return url.match(/[?&]v=([^&]+)/)?.[1] ?? ''
}

export default function RecommendPage() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const situationId = Number(searchParams.get('situationId'))
  const conceptId   = Number(searchParams.get('conceptId'))

  const [situation, setSituation] = useState<Situation | null>(null)
  const [concept,   setConcept]   = useState<Concept | null>(null)
  const [tab, setTab]             = useState<Tab>('recommend')
  const [songs, setSongs]         = useState<Song[]>([])
  const [savedSongs, setSavedSongs]   = useState<Song[]>([])
  const [historySongs, setHistorySongs] = useState<Song[]>([])
  const [loading, setLoading]     = useState(false)
  const [error, setError]         = useState(false)
  const [playingId, setPlayingId] = useState<number | null>(null)
  const [excludePlayed, setExcludePlayed] = useState(false)
  const [toast, setToast] = useState('')

  const showToast = (msg: string) => {
    setToast(msg)
    setTimeout(() => setToast(''), 2000)
  }

  useEffect(() => {
    if (!situationId || !conceptId) { navigate('/'); return }
    getSituations().then(res => setSituation(res.data.find(s => s.id === situationId) ?? null))
    getConcepts().then(res => setConcept(res.data.find(c => c.id === conceptId) ?? null))
    loadRecommend()
    loadSaved()
    loadHistory()
  }, [situationId, conceptId])

  const loadRecommend = async (exclude = excludePlayed) => {
    setLoading(true)
    setError(false)
    try {
      const res = await recommend(situationId, conceptId, exclude)
      setSongs(res.data)
    } catch {
      setError(true)
    } finally {
      setLoading(false)
    }
  }

  const loadSaved    = async () => { const r = await getSaved(situationId, conceptId); setSavedSongs(r.data) }
  const loadHistory  = async () => { const r = await getHistory(); setHistorySongs(r.data) }

  const handleSave = async (song: Song) => {
    if (song.saved) await unsaveSong(song.id)
    else await saveSong(song.id, situationId, conceptId)

    const toggleSaved = (list: Song[]) =>
      list.map(s => s.id === song.id ? { ...s, saved: !s.saved } : s)

    setSongs(toggleSaved)
    setHistorySongs(toggleSaved)

    if (song.saved) {
      setSavedSongs(prev => prev.filter(s => s.id !== song.id))
      showToast('저장 해제됐어요')
    } else {
      loadSaved()
      showToast('저장됐어요 ♥')
    }
  }

  const handlePlay = (song: Song) => {
    recordPlay(song.id, situationId, conceptId)
    const isOpening = playingId !== song.id
    setPlayingId(prev => prev === song.id ? null : song.id)
    if (isOpening) {
      setTimeout(() => {
        document.getElementById(`song-${song.id}`)?.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
      }, 50)
    }
  }

  const handleShare = async (song: Song) => {
    const text = `🎵 ${song.title} - ${song.artist}`
    if (navigator.share) {
      await navigator.share({ title: text, url: song.youtubeUrl })
    } else {
      await navigator.clipboard.writeText(song.youtubeUrl)
      showToast('링크가 복사됐어요!')
    }
  }

  const handleToggleExclude = () => {
    const next = !excludePlayed
    setExcludePlayed(next)
    setPlayingId(null)
    loadRecommend(next)
  }

  const currentSongs = tab === 'recommend' ? songs : tab === 'saved' ? savedSongs : historySongs

  return (
    <div className="recommend-container">
      {toast && <div className="toast">{toast}</div>}

      <header className="main-header">
        <button className="back-btn" onClick={() => navigate(`/concept?situationId=${situationId}`)}>← 뒤로</button>
        <div className="logo" style={{ cursor: 'pointer' }} onClick={() => navigate('/')}>🎵 멜로디약국</div>
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
        <div className="recommend-controls">
          <button className="refresh-btn" disabled={loading} onClick={() => { setPlayingId(null); loadRecommend() }}>🔄 다시 추천받기</button>
          <button
            className={`exclude-btn ${excludePlayed ? 'active' : ''}`}
            onClick={handleToggleExclude}
          >
            {excludePlayed ? '✅' : '⬜'} 들은 노래 제외
          </button>
        </div>
      )}

      {error && (
        <div className="page-error">
          불러오기 실패했어요 😢<br />
          <button onClick={() => loadRecommend()}>다시 시도</button>
        </div>
      )}

      <div className="song-list">
        {loading && Array.from({ length: 3 }).map((_, i) => (
          <div key={i} className="skeleton skeleton-song" />
        ))}
        {!loading && !error && currentSongs.length === 0 && (
          <div className="empty">
            {tab === 'recommend' && (
              excludePlayed
                ? <><p>들은 노래를 모두 제외했어요.</p><button className="empty-btn" onClick={handleToggleExclude}>전체 다시 보기</button></>
                : <p>추천할 노래가 없어요.</p>
            )}
            {tab === 'saved' && (
              <><p>아직 저장한 노래가 없어요.</p><button className="empty-btn" onClick={() => { setTab('recommend'); setPlayingId(null) }}>노래 추천받기</button></>
            )}
            {tab === 'history' && (
              <><p>아직 들은 노래가 없어요.</p><button className="empty-btn" onClick={() => { setTab('recommend'); setPlayingId(null) }}>노래 추천받기</button></>
            )}
          </div>
        )}
        {currentSongs.map(song => (
          <div key={song.id} id={`song-${song.id}`} className={`song-card ${playingId === song.id ? 'playing' : ''}`}>
            <div className="song-row">
              {song.thumbnailUrl && (
                <img src={song.thumbnailUrl} alt={song.title} className="song-thumbnail" />
              )}
              <div className="song-info">
                <p className="song-title">{song.title}</p>
                <p className="song-artist">{song.artist}</p>
                {tab === 'saved' && song.savedSituationName && (
                  <p className="song-context">
                    {song.savedSituationIcon} {song.savedSituationName} · {song.savedConceptIcon} {song.savedConceptName}
                  </p>
                )}
              </div>
              <div className="song-actions">
                <button className={`play-btn ${playingId === song.id ? 'playing' : ''}`} onClick={() => handlePlay(song)}>
                  {playingId === song.id ? '■' : '▶'}
                </button>
                <button className={`save-btn ${song.saved ? 'saved' : ''}`} onClick={() => handleSave(song)}>
                  {song.saved ? '♥' : '♡'}
                </button>
                <button className="share-btn" onClick={() => handleShare(song)}>📤</button>
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
