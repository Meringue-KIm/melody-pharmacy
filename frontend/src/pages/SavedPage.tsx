import { useEffect, useState, useCallback, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { getSaved, getHistory, saveSong, unsaveSong, recordPlay } from '../api/songApi'
import type { Song } from '../api/songApi'
import '../styles/Recommend.css'

type Tab = 'saved' | 'history'

function getVideoId(url: string) {
  return url.match(/[?&]v=([^&]+)/)?.[1] ?? ''
}

export default function SavedPage() {
  const navigate = useNavigate()
  const [tab, setTab] = useState<Tab>('saved')
  const [savedSongs, setSavedSongs] = useState<Song[]>([])
  const [historySongs, setHistorySongs] = useState<Song[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)
  const [playingId, setPlayingId] = useState<number | null>(null)
  const [autoPlay, setAutoPlay] = useState(false)
  const [savingIds, setSavingIds] = useState<Set<number>>(new Set())
  const [toast, setToast] = useState('')
  const autoplaySongsRef = useRef<Song[]>([])

  const showToast = (msg: string) => {
    setToast(msg)
    setTimeout(() => setToast(''), 2000)
  }

  const load = () => {
    setLoading(true)
    setError(false)
    Promise.all([getSaved(), getHistory()])
      .then(([s, h]) => { setSavedSongs(s.data); setHistorySongs(h.data) })
      .catch(() => setError(true))
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const currentSongs = tab === 'saved' ? savedSongs : historySongs
  const playingSong = [...savedSongs, ...historySongs].find(s => s.id === playingId) ?? null

  const handlePlay = useCallback((song: Song) => {
    const isOpening = playingId !== song.id
    setPlayingId(prev => prev === song.id ? null : song.id)
    if (isOpening) {
      autoplaySongsRef.current = currentSongs
      recordPlay(song.id)
      setHistorySongs(prev => [song, ...prev.filter(s => s.id !== song.id)].slice(0, 20))
      setTimeout(() => {
        document.getElementById(`song-${song.id}`)?.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
      }, 50)
    }
  }, [playingId, currentSongs])

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

  const handleSave = async (song: Song) => {
    if (savingIds.has(song.id)) return
    setSavingIds(prev => new Set(prev).add(song.id))
    try {
      if (song.saved) {
        await unsaveSong(song.id)
        setSavedSongs(prev => prev.filter(s => s.id !== song.id))
        setHistorySongs(prev => prev.map(s => s.id === song.id ? { ...s, saved: false } : s))
        showToast('저장 해제됐어요')
      } else {
        await saveSong(song.id)
        setSavedSongs(prev => [...prev, { ...song, saved: true }])
        setHistorySongs(prev => prev.map(s => s.id === song.id ? { ...s, saved: true } : s))
        showToast('저장됐어요 ♥')
      }
    } catch {
      showToast('저장에 실패했어요.')
    } finally {
      setSavingIds(prev => { const n = new Set(prev); n.delete(song.id); return n })
    }
  }

  const handleShare = async (song: Song) => {
    const text = `🎵 ${song.title} - ${song.artist}`
    if (navigator.share) await navigator.share({ title: text, url: song.youtubeUrl })
    else { await navigator.clipboard.writeText(song.youtubeUrl); showToast('링크가 복사됐어요!') }
  }

  return (
    <div className="recommend-container">
      {toast && <div className="toast">{toast}</div>}

      <header className="main-header">
        <button className="back-btn" onClick={() => navigate('/')}>← 뒤로</button>
        <div className="logo" style={{ cursor: 'pointer' }} onClick={() => navigate('/')}>🎵 멜로디약국</div>
        <div />
      </header>

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
        <button className={tab === 'saved' ? 'active' : ''} onClick={() => setTab('saved')}>
          저장소 {savedSongs.length > 0 && <span className="badge">{savedSongs.length}</span>}
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

      {error && (
        <div className="page-error">
          불러오기 실패했어요 😢<br />
          <button onClick={load}>다시 시도</button>
        </div>
      )}

      <div className="song-list">
        {loading && Array.from({ length: 4 }).map((_, i) => (
          <div key={i} className="skeleton skeleton-song" />
        ))}
        {!loading && !error && currentSongs.length === 0 && (
          <div className="empty">
            {tab === 'saved'
              ? <><p>아직 저장한 노래가 없어요.</p><button className="empty-btn" onClick={() => navigate('/')}>노래 추천받기</button></>
              : <><p>아직 들은 노래가 없어요.</p><button className="empty-btn" onClick={() => navigate('/')}>노래 추천받기</button></>
            }
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
                {tab === 'saved' && song.savedSituationName && (
                  <p className="song-context">
                    {song.savedSituationIcon} {song.savedSituationName} · {song.savedConceptIcon} {song.savedConceptName}
                  </p>
                )}
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
