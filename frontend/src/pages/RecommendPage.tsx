import { useEffect, useState, useCallback, useRef } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { recommend, saveSong, unsaveSong, recordPlay, getSituations, getConcepts, getHistory, getSaved } from '../api/songApi'
import type { Song, Situation, Concept } from '../api/songApi'
import '../styles/Recommend.css'

type Tab = 'recommend' | 'history'

function getVideoId(url: string) {
  return url.match(/[?&]v=([^&]+)/)?.[1] ?? ''
}

function shuffle<T>(arr: T[]): T[] {
  const a = [...arr]
  for (let i = a.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    ;[a[i], a[j]] = [a[j], a[i]]
  }
  return a
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
  const [historySongs, setHistorySongs] = useState<Song[]>([])
  const [loading, setLoading]     = useState(false)
  const [error, setError]         = useState(false)
  const [playingId, setPlayingId] = useState<number | null>(null)
  const [excludePlayed, setExcludePlayed] = useState(false)
  const [autoPlay, setAutoPlay]   = useState(false)
  const [savingIds, setSavingIds] = useState<Set<number>>(new Set())
  const [savedCount, setSavedCount] = useState(0)
  const [toast, setToast]         = useState('')
  const autoplaySongsRef = useRef<Song[]>([])

  const showToast = (msg: string) => {
    setToast(msg)
    setTimeout(() => setToast(''), 2000)
  }

  useEffect(() => {
    if (!situationId || !conceptId) { navigate('/'); return }
    getSituations().then(res => setSituation(res.data.find(s => s.id === situationId) ?? null))
    getConcepts().then(res => setConcept(res.data.find(c => c.id === conceptId) ?? null))
    getSaved().then(res => setSavedCount(res.data.length)).catch(() => {})
    loadRecommend()
    loadHistory()
  }, [situationId, conceptId])

  const loadRecommend = async (exclude = excludePlayed) => {
    setPlayingId(null)
    setLoading(true)
    setError(false)
    try {
      const res = await recommend(situationId, conceptId, exclude)
      setSongs(shuffle(res.data))
    } catch {
      setError(true)
      setSongs([])
    } finally {
      setLoading(false)
    }
  }

  const loadHistory = async () => { const r = await getHistory(); setHistorySongs(r.data) }

  const handleShuffle = () => setSongs(prev => shuffle(prev))

  const handleSave = async (song: Song) => {
    if (savingIds.has(song.id)) return
    setSavingIds(prev => new Set(prev).add(song.id))
    try {
      if (song.saved) {
        await unsaveSong(song.id)
        setSavedCount(c => Math.max(0, c - 1))
      } else {
        await saveSong(song.id, situationId, conceptId)
        setSavedCount(c => c + 1)
      }

      const toggle = (list: Song[]) =>
        list.map(s => s.id === song.id ? { ...s, saved: !s.saved } : s)
      setSongs(toggle)
      setHistorySongs(toggle)
      showToast(song.saved ? '저장 해제됐어요' : '저장됐어요 ♥')
    } catch {
      showToast('저장에 실패했어요. 다시 시도해주세요.')
    } finally {
      setSavingIds(prev => { const n = new Set(prev); n.delete(song.id); return n })
    }
  }

  const currentSongs = tab === 'recommend' ? songs : historySongs

  const handlePlay = useCallback((song: Song) => {
    const isOpening = playingId !== song.id
    setPlayingId(prev => prev === song.id ? null : song.id)
    if (isOpening) {
      autoplaySongsRef.current = currentSongs
      recordPlay(song.id, situationId, conceptId)
      setHistorySongs(prev => [song, ...prev.filter(s => s.id !== song.id)].slice(0, 20))
      setTimeout(() => {
        document.getElementById(`song-${song.id}`)?.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
      }, 50)
    }
  }, [playingId, situationId, conceptId, currentSongs])

  useEffect(() => {
    if (!autoPlay || !playingId) return
    const handleMessage = (e: MessageEvent) => {
      try {
        const data = JSON.parse(e.data)
        if (data.event === 'onStateChange' && data.info === 0) {
          const list = autoplaySongsRef.current
          const idx = list.findIndex(s => s.id === playingId)
          if (idx >= 0 && idx < list.length - 1) handlePlay(list[idx + 1])
          else { setPlayingId(null); showToast('재생 목록이 끝났어요') }
        }
      } catch {}
    }
    window.addEventListener('message', handleMessage)
    return () => window.removeEventListener('message', handleMessage)
  }, [autoPlay, playingId, handlePlay])

  const handleIframeLoad = useCallback((e: React.SyntheticEvent<HTMLIFrameElement>) => {
    e.currentTarget.contentWindow?.postMessage(
      JSON.stringify({ event: 'command', func: 'playVideo', args: [] }),
      'https://www.youtube.com'
    )
  }, [])

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
    loadRecommend(next)
  }

  const playingSong = [...songs, ...historySongs].find(s => s.id === playingId) ?? null

  return (
    <div className="recommend-container">
      {toast && <div className="toast">{toast}</div>}

      <header className="main-header">
        <button className="back-btn" onClick={() => navigate(`/concept?situationId=${situationId}`)}>← 뒤로</button>
        <div className="logo" style={{ cursor: 'pointer' }} onClick={() => navigate('/')}>🎵 멜로디약국</div>
        <button className="saved-link-btn" onClick={() => navigate('/saved')}>
          ♥{savedCount > 0 ? ` ${savedCount}` : ''}
        </button>
      </header>

      <div className="recommend-info">
        <span className="tag tag-link" onClick={() => navigate('/')}>
          {situation?.icon} {situation?.name}
        </span>
        <span className="tag tag-link" onClick={() => navigate(`/concept?situationId=${situationId}`)}>
          {concept?.icon} {concept?.name} ✎
        </span>
      </div>

      {playingSong && (
        <div className="now-playing">
          <div className="now-playing-row">
            {playingSong.thumbnailUrl && (
              <img src={playingSong.thumbnailUrl} alt="" className="now-playing-thumb" />
            )}
            <div className="now-playing-info">
              <p className="now-playing-title">{playingSong.title}</p>
              <p className="now-playing-artist">{playingSong.artist}</p>
            </div>
            <button className="now-playing-stop" onClick={() => setPlayingId(null)}>■ 정지</button>
          </div>
          <div className="youtube-embed">
            <iframe
              key={playingSong.id}
              src={`https://www.youtube.com/embed/${getVideoId(playingSong.youtubeUrl)}?enablejsapi=1`}
              title={playingSong.title}
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
              onLoad={handleIframeLoad}
            />
          </div>
        </div>
      )}

      <div className="tab-bar">
        <button className={tab === 'recommend' ? 'active' : ''} onClick={() => setTab('recommend')}>
          💊 처방전
        </button>
        <button className={tab === 'history' ? 'active' : ''} onClick={() => setTab('history')}>
          최근 재생
        </button>
      </div>

      <div className="autoplay-row">
        <button
          className={`autoplay-btn ${autoPlay ? 'active' : ''}`}
          onClick={() => setAutoPlay(p => !p)}
        >⏭ 자동재생 {autoPlay ? 'ON' : 'OFF'}</button>
      </div>

      {tab === 'recommend' && !loading && songs.length > 0 && (
        <p className="song-count">총 {songs.length}곡의 처방전</p>
      )}

      {tab === 'recommend' && (
        <div className="recommend-controls">
          <button className="refresh-btn" disabled={loading} onClick={() => loadRecommend()}>🔄 새 처방전 받기</button>
          <button className="refresh-btn" disabled={loading || songs.length === 0} onClick={handleShuffle}>🔀 순서 섞기</button>
          <button
            className={`exclude-btn ${excludePlayed ? 'active' : ''}`}
            onClick={handleToggleExclude}
          >
            <span className="toggle-track"><span className="toggle-thumb" /></span>
            들은 곡 제외
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
                : <><p>추천할 노래가 없어요.</p><button className="empty-btn" onClick={() => navigate(`/concept?situationId=${situationId}`)}>다른 느낌으로 바꿔보기</button></>
            )}
            {tab === 'history' && (
              <><p>아직 들은 노래가 없어요.</p><button className="empty-btn" onClick={() => setTab('recommend')}>처방전 보기</button></>
            )}
          </div>
        )}
        {currentSongs.map(song => (
          <div
            key={song.id}
            id={`song-${song.id}`}
            className={`song-card ${playingId === song.id ? 'playing' : ''}`}
            onClick={() => handlePlay(song)}
          >
            <div className="song-row">
              {song.thumbnailUrl && (
                <img src={song.thumbnailUrl} alt={song.title} className="song-thumbnail" />
              )}
              <div className="song-info">
                <p className="song-title">{song.title}</p>
                <p className="song-artist">{song.artist}</p>
              </div>
              <div className="song-actions">
                <button className={`play-btn ${playingId === song.id ? 'playing' : ''}`}>
                  {playingId === song.id ? '■' : '▶'}
                </button>
                <button
                  className={`save-btn ${song.saved ? 'saved' : ''}`}
                  onClick={e => { e.stopPropagation(); handleSave(song) }}
                  disabled={savingIds.has(song.id)}
                >
                  {song.saved ? '♥' : '♡'}
                </button>
                <button className="share-btn" onClick={e => { e.stopPropagation(); handleShare(song) }}>📤</button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
