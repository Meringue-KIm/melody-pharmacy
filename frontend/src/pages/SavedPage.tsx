import { useEffect, useState, useCallback, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { getSaved, getHistory, saveSong, unsaveSong, recordPlay } from '../api/songApi'
import { guestGetSaved, guestGetHistory, guestSaveSong, guestUnsaveSong, guestRecordPlay } from '../api/guestApi'
import { isGuest } from '../utils/guestMode'
import type { Song } from '../api/songApi'
import AppHeader from '../components/AppHeader'
import mascotLab from '../assets/mascot-lab.png'
import MascotIllustration from '../components/MascotIllustration'

type Tab = 'saved' | 'history'

function getVideoId(url: string) {
  return url.match(/[?&]v=([^&]+)/)?.[1] ?? ''
}

export default function SavedPage() {
  const navigate = useNavigate()
  const [tab, setTab] = useState<Tab>('saved')
  const [filterSit, setFilterSit] = useState<string | null>(() =>
    sessionStorage.getItem('savedFilterSit') || null
  )
  const updateFilter = (sit: string | null) => {
    setFilterSit(sit)
    if (sit) sessionStorage.setItem('savedFilterSit', sit)
    else sessionStorage.removeItem('savedFilterSit')
  }

  const [savedSongs, setSavedSongs] = useState<Song[]>([])
  const [historySongs, setHistorySongs] = useState<Song[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)
  const [playingId, setPlayingId] = useState<number | null>(null)
  const [autoPlay, setAutoPlay] = useState(false)
  const [savingIds, setSavingIds] = useState<Set<number>>(new Set())
  const [toast, setToast] = useState('')
  const [undoSong, setUndoSong] = useState<Song | null>(null)
  const autoplaySongsRef = useRef<Song[]>([])
  const undoTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null)

  const showToast = (msg: string, undo?: Song) => {
    setToast(msg)
    setUndoSong(undo ?? null)
    if (undoTimerRef.current) clearTimeout(undoTimerRef.current)
    undoTimerRef.current = setTimeout(() => { setToast(''); setUndoSong(null) }, 3000)
  }

  const load = () => {
    setLoading(true)
    setError(false)
    Promise.all(isGuest() ? [guestGetSaved(), guestGetHistory()] : [getSaved(), getHistory()])
      .then(([s, h]) => { setSavedSongs(s.data); setHistorySongs(h.data) })
      .catch(() => setError(true))
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  // 저장 목록이 바뀌어 현재 필터 상황이 사라지면 전체 보기로 초기화
  useEffect(() => {
    if (!filterSit) return
    const situations = new Set(savedSongs.map(s => s.savedSituationName).filter(Boolean))
    if (!situations.has(filterSit)) updateFilter(null)
  }, [savedSongs])

  const handleThumbError = (e: React.SyntheticEvent<HTMLImageElement>) => {
    const img = e.currentTarget
    if (img.src.includes('hqdefault')) img.src = img.src.replace('hqdefault', 'mqdefault')
    else if (img.src.includes('mqdefault')) img.src = img.src.replace('mqdefault', 'default')
    else img.style.display = 'none'
  }

  const currentSongs = tab === 'saved' ? savedSongs : historySongs
  const playingSong = [...savedSongs, ...historySongs].find(s => s.id === playingId) ?? null

  const handlePlay = useCallback((song: Song) => {
    const isOpening = playingId !== song.id
    setPlayingId(prev => prev === song.id ? null : song.id)
    if (isOpening) {
      autoplaySongsRef.current = currentSongs
      isGuest() ? guestRecordPlay(song.id) : recordPlay(song.id)
      setHistorySongs(prev => [song, ...prev.filter(s => s.id !== song.id)])
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

  const handleSave = async (song: Song, e: React.MouseEvent) => {
    e.stopPropagation()
    if (savingIds.has(song.id)) return
    setSavingIds(prev => new Set(prev).add(song.id))
    try {
      if (song.saved) {
        await (isGuest() ? guestUnsaveSong(song.id) : unsaveSong(song.id))
        setSavedSongs(prev => {
          const next = prev.filter(s => s.id !== song.id)
          window.dispatchEvent(new CustomEvent('savedCountChanged', { detail: next.length }))
          return next
        })
        setHistorySongs(prev => prev.map(s => s.id === song.id ? { ...s, saved: false } : s))
        showToast('저장 해제됐어요', song)
      } else {
        await (isGuest() ? guestSaveSong(song.id, song.savedSituationId, song.savedConceptId) : saveSong(song.id, song.savedSituationId, song.savedConceptId))
        setSavedSongs(prev => {
          const next = [...prev, { ...song, saved: true }]
          window.dispatchEvent(new CustomEvent('savedCountChanged', { detail: next.length }))
          return next
        })
        setHistorySongs(prev => prev.map(s => s.id === song.id ? { ...s, saved: true } : s))
        showToast('저장됐어요 ♥')
      }
    } catch (err: any) {
      if (err.response?.status === 409) showToast('이미 저장된 곡이에요.')
      else if (!err.response) showToast('네트워크 오류예요. 연결을 확인해주세요.')
      else showToast('저장에 실패했어요.')
    } finally {
      setSavingIds(prev => { const n = new Set(prev); n.delete(song.id); return n })
    }
  }

  const handleShare = async (song: Song, e: React.MouseEvent) => {
    e.stopPropagation()
    const text = `🎵 ${song.title} - ${song.artist}`
    try {
      if (navigator.share) await navigator.share({ title: text, url: song.youtubeUrl })
      else { await navigator.clipboard.writeText(song.youtubeUrl); showToast('링크가 복사됐어요!') }
    } catch {
      showToast('공유에 실패했어요.')
    }
  }

  const handleUndo = async () => {
    if (!undoSong) return
    setUndoSong(null)
    setToast('')
    try {
      await (isGuest()
        ? guestSaveSong(undoSong.id, undoSong.savedSituationId, undoSong.savedConceptId)
        : saveSong(undoSong.id, undoSong.savedSituationId, undoSong.savedConceptId))
      setSavedSongs(prev => {
        const next = [...prev, { ...undoSong, saved: true }]
        window.dispatchEvent(new CustomEvent('savedCountChanged', { detail: next.length }))
        return next
      })
      setHistorySongs(prev => prev.map(s => s.id === undoSong.id ? { ...s, saved: true } : s))
      showToast('다시 저장됐어요 ♥')
    } catch {
      showToast('복구에 실패했어요.')
    }
  }

  return (
    <div className="frame" data-screen="saved">
      {toast && (
        <div className="toast" style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <span>{toast}</span>
          {undoSong && (
            <button onClick={handleUndo} style={{ background: 'none', border: '1px solid rgba(255,255,255,0.5)', color: 'inherit', borderRadius: 6, padding: '2px 8px', cursor: 'pointer', fontSize: 12, flexShrink: 0 }}>
              되돌리기
            </button>
          )}
        </div>
      )}

      <AppHeader />

      <section className="hero">
        <p className="eyebrow">My Cabinet · 약장</p>
        <h1 className="h1">내 약장</h1>
        <p className="subtitle">{loading ? '불러오는 중…' : `저장한 처방전 ${savedSongs.length}곡`}</p>
      </section>

      {/* 플레이어 */}
      {playingSong && (
        <div className="player">
          <div className="player-main">
            <div className="song-thumb" style={{ width: 48, height: 48, flexShrink: 0 }}>
              {playingSong.thumbnailUrl && <img src={playingSong.thumbnailUrl} alt="" onError={handleThumbError} />}
            </div>
            <div className="player-info">
              <p className="player-title">{playingSong.title}</p>
              <p className="player-artist">{playingSong.artist}</p>
            </div>
            <button className="player-btn" onClick={() => setPlayingId(null)}>■</button>
          </div>
          <div className="youtube-embed">
            <iframe
              key={playingSong.id}
              src={`https://www.youtube.com/embed/${getVideoId(playingSong.youtubeUrl)}?enablejsapi=1&autoplay=1&origin=${encodeURIComponent(window.location.origin)}`}
              title={playingSong.title}
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
              onLoad={handleIframeLoad}
            />
          </div>
        </div>
      )}

      {/* 탭 */}
      <div className="tabs">
        <button className={`tab ${tab === 'saved' ? 'active' : ''}`} onClick={() => setTab('saved')}>
          ♥ 저장소 {savedSongs.length > 0 && <span className="badge">{savedSongs.length}</span>}
        </button>
        <button className={`tab ${tab === 'history' ? 'active' : ''}`} onClick={() => setTab('history')}>
          ⏱ 최근 재생 {historySongs.length > 0 && <span className="badge">{historySongs.length}</span>}
        </button>
      </div>

      {(tab === 'history' ? historySongs.length > 0 : savedSongs.length > 0) && (
        <div className="controls">
          <button className={`toggle ${autoPlay ? 'on' : ''}`} onClick={() => setAutoPlay(p => !p)}>
            <span className="toggle-track"><span className="toggle-thumb" /></span>
            자동재생
          </button>
        </div>
      )}

      {error && (
        <div className="page-error">
          불러오기 실패했어요<br />
          <button onClick={load}>다시 시도</button>
        </div>
      )}

      {/* 저장 목록 */}
      {tab === 'saved' && loading && (
        <div className="song-list">
          {Array.from({ length: 4 }).map((_, i) => <div key={i} className="skel skel-song" />)}
        </div>
      )}
      {tab === 'saved' && !loading && !error && savedSongs.length === 0 ? (
        <div className="empty empty-lg">
          <MascotIllustration src={mascotLab} size={130} alt="약사 캐릭터" />
          <p>아직 약장이 비어있어요.</p>
          <button className="btn" onClick={() => navigate('/')}>처방전 받으러 가기</button>
        </div>
      ) : tab === 'saved' && !loading && (() => {
        const situations = [...new Set(savedSongs.map(s => s.savedSituationName).filter(Boolean))] as string[]
        const filtered = filterSit ? savedSongs.filter(s => s.savedSituationName === filterSit) : savedSongs
        return (
        <>
          {situations.length > 1 && (
            <div className="chip-row" style={{ marginBottom: 14 }}>
              <button
                className="chip"
                style={{ background: !filterSit ? 'var(--accent)' : undefined, color: !filterSit ? 'white' : undefined }}
                onClick={() => updateFilter(null)}
              >전체 {savedSongs.length}</button>
              {situations.map(sit => (
                <button
                  key={sit}
                  className="chip"
                  style={{ background: filterSit === sit ? 'var(--accent)' : undefined, color: filterSit === sit ? 'white' : undefined }}
                  onClick={() => updateFilter(sit)}
                >{sit}</button>
              ))}
            </div>
          )}
          <div className="shelf">
            <p className="shelf-title">상비약 · ALWAYS ON HAND</p>
            <div className="song-list">
              {filtered.map((song, idx) => (
              <button
                key={song.id}
                id={`song-${song.id}`}
                className={`song-card ${playingId === song.id ? 'playing' : ''}`}
                onClick={() => handlePlay(song)}
              >
                <span className="song-num">{String(idx + 1).padStart(2, '0')}</span>
                <div className="song-thumb">
                  {song.thumbnailUrl && <img src={song.thumbnailUrl} alt={song.title} onError={handleThumbError} />}
                </div>
                <div className="song-info">
                  <p className="song-title">{song.title}</p>
                  <p className="song-artist">{song.artist}</p>
                  {song.savedSituationName && (
                    <span className="dose-chip" style={{ marginTop: 4 }}>
                      {song.savedSituationIcon} {song.savedSituationName} · {song.savedConceptIcon} {song.savedConceptName}
                    </span>
                  )}
                </div>
                <div className="song-actions">
                  <span className={`play-btn ${playingId === song.id ? 'playing' : ''}`}>
                    {playingId === song.id ? '❚❚' : '▶'}
                  </span>
                  <button
                    type="button"
                    className="save-btn saved"
                    onClick={e => handleSave(song, e)}
                    style={savingIds.has(song.id) ? { opacity: 0.4, cursor: 'wait' } : undefined}
                    aria-label="저장 해제"
                  >
                    {savingIds.has(song.id) ? '…' : '♥'}
                  </button>
                  <button type="button" className="share-btn" onClick={e => handleShare(song, e)} aria-label="공유">📤</button>
                </div>
              </button>
            ))}
          </div>
        </div>
        </>
        )
      })()}

      {/* 미니 플레이어 — 스크롤해도 하단 고정 */}
      {playingSong && (
        <div className="mini-player">
          {playingSong.thumbnailUrl && (
            <img src={playingSong.thumbnailUrl} alt="" className="mini-player-thumb" onError={handleThumbError} />
          )}
          <div className="mini-player-info">
            <p className="mini-player-title">{playingSong.title}</p>
            <p className="mini-player-artist">{playingSong.artist}</p>
          </div>
          <button className="mini-player-stop" onClick={() => setPlayingId(null)} aria-label="재생 중지">■</button>
        </div>
      )}

      {/* 히스토리 탭 */}
      {tab === 'history' && (
        <div className="song-list">
          {loading && Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="skel skel-song" />
          ))}
          {!loading && historySongs.length === 0 && (
            <div className="empty">
              <p>아직 들은 노래가 없어요.</p>
              <button className="btn-ghost-sm" onClick={() => navigate('/')}>노래 추천받기</button>
            </div>
          )}
          {historySongs.map((song, idx) => (
            <button
              key={song.id}
              id={`song-${song.id}`}
              className={`song-card ${playingId === song.id ? 'playing' : ''}`}
              onClick={() => handlePlay(song)}
            >
              <span className="song-num">{String(idx + 1).padStart(2, '0')}</span>
              <div className="song-thumb">
                {song.thumbnailUrl && <img src={song.thumbnailUrl} alt={song.title} onError={handleThumbError} />}
              </div>
              <div className="song-info">
                <p className="song-title">{song.title}</p>
                <p className="song-artist">{song.artist}</p>
                {song.savedSituationName && (
                  <span className="dose-chip" style={{ marginTop: 4 }}>
                    {song.savedSituationIcon} {song.savedSituationName} · {song.savedConceptIcon} {song.savedConceptName}
                  </span>
                )}
              </div>
              <div className="song-actions">
                <span className={`play-btn ${playingId === song.id ? 'playing' : ''}`}>
                  {playingId === song.id ? '❚❚' : '▶'}
                </span>
                <button
                  type="button"
                  className={`save-btn ${song.saved ? 'saved' : ''}`}
                  onClick={e => handleSave(song, e)}
                  style={savingIds.has(song.id) ? { opacity: 0.4, cursor: 'wait' } : undefined}
                  aria-label={song.saved ? '저장 해제' : '저장'}
                >
                  {savingIds.has(song.id) ? '…' : song.saved ? '♥' : '♡'}
                </button>
                <button type="button" className="share-btn" onClick={e => handleShare(song, e)} aria-label="공유">📤</button>
              </div>
            </button>
          ))}
        </div>
      )}
    </div>
  )
}
