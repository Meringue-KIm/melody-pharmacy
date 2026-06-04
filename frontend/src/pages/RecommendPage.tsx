import { useEffect, useState, useCallback, useRef } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import html2canvas from 'html2canvas'
import { recommend, saveSong, unsaveSong, recordPlay, getSituations, getConcepts, getHistory, getSaved } from '../api/songApi'
import type { Song, Situation, Concept } from '../api/songApi'
import AppHeader from '../components/AppHeader'

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
  const [rxOpen, setRxOpen]       = useState(false)
  const [songs, setSongs]         = useState<Song[]>([])
  const [historySongs, setHistorySongs] = useState<Song[]>([])
  const [loading, setLoading]     = useState(false)
  const [error, setError]         = useState(false)
  const [playingId, setPlayingId] = useState<number | null>(null)
  const [excludePlayed, setExcludePlayed] = useState(false)
  const [autoPlay, setAutoPlay]   = useState(false)
  const [savingIds, setSavingIds] = useState<Set<number>>(new Set())
  const [, setSavedCount] = useState(0)
  const [toast, setToast]         = useState('')
  const autoplaySongsRef = useRef<Song[]>([])
  const shareCardRef = useRef<HTMLDivElement>(null)

  const today = new Date().toISOString().slice(0, 10).replace(/-/g, '.')
  const [nickname, setNickname] = useState(localStorage.getItem('nickname') || '환자')

  const rxTagline = (() => {
    if (!situation || !concept) return '지금 기분에 딱 맞는 음악 처방'
    const conName = concept.name
    const tagMap: Record<string, string> = {
      '잔잔': '포근하게 감싸주는 처방',
      '신나': '에너지 충전용 강력 처방',
      '감성': '감정을 살살 건드려주는 처방',
      '파워': '힘이 팍팍 넘치는 처방',
      '힐링': '마음의 피로를 씻어주는 처방',
      '집중': '잡념 차단 집중력 처방',
      '설레': '두근두근 설레게 하는 처방',
      '슬픔': '감정을 함께 나눠주는 처방',
      '슬픈': '감정을 함께 나눠주는 처방',
      '그루브': '몸이 절로 움직이는 처방',
    }
    const key = Object.keys(tagMap).find(k => conName.includes(k))
    return `${situation.name} 중 ${key ? tagMap[key] : conName + ' 처방'}`
  })()

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

  // 닉네임 변경 이벤트 수신
  useEffect(() => {
    const handler = (e: Event) => setNickname((e as CustomEvent<string>).detail)
    window.addEventListener('nicknameChanged', handler)
    return () => window.removeEventListener('nicknameChanged', handler)
  }, [])

  // 탭 전환 후 돌아올 때 저장 카운트 재조회
  useEffect(() => {
    const onVisible = () => {
      if (!document.hidden) getSaved().then(res => setSavedCount(res.data.length)).catch(() => {})
    }
    document.addEventListener('visibilitychange', onVisible)
    return () => document.removeEventListener('visibilitychange', onVisible)
  }, [])

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

  const loadHistory = async () => {
    try { const r = await getHistory(); setHistorySongs(r.data) } catch {}
  }

  const handleShuffle = () => setSongs(prev => shuffle(prev))

  const handleSave = async (song: Song, e: React.MouseEvent) => {
    e.stopPropagation()
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
    } catch (err: any) {
      if (err.response?.status === 409) showToast('이미 저장된 곡이에요.')
      else if (!err.response) showToast('네트워크 오류예요. 연결을 확인해주세요.')
      else showToast('저장에 실패했어요.')
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

  const handleShare = async (song: Song, e: React.MouseEvent) => {
    e.stopPropagation()
    const text = `🎵 ${song.title} - ${song.artist}`
    if (navigator.share) {
      await navigator.share({ title: text, url: song.youtubeUrl })
    } else {
      await navigator.clipboard.writeText(song.youtubeUrl)
      showToast('링크가 복사됐어요!')
    }
  }

  const handleThumbError = (e: React.SyntheticEvent<HTMLImageElement>) => {
    const img = e.currentTarget
    if (img.src.includes('hqdefault')) {
      img.src = img.src.replace('hqdefault', 'mqdefault')
    } else if (img.src.includes('mqdefault')) {
      img.src = img.src.replace('mqdefault', 'default')
    }
  }

  const handleSharePrescription = async () => {
    if (!shareCardRef.current) return
    showToast('처방전 이미지 생성 중…')
    try {
      const canvas = await html2canvas(shareCardRef.current, { scale: 2, useCORS: true, backgroundColor: null })
      const dataUrl = canvas.toDataURL('image/png')
      const blob = await (await fetch(dataUrl)).blob()
      const file = new File([blob], 'melody-prescription.png', { type: 'image/png' })
      if (navigator.canShare?.({ files: [file] })) {
        await navigator.share({ files: [file], title: '멜로디약국 처방전' })
      } else {
        const a = document.createElement('a')
        a.href = dataUrl
        a.download = 'melody-prescription.png'
        a.click()
        showToast('처방전 이미지 저장됐어요!')
      }
    } catch {
      showToast('이미지 생성에 실패했어요.')
    }
  }

  const handleToggleExclude = () => {
    const next = !excludePlayed
    setExcludePlayed(next)
    loadRecommend(next)
  }

  const playingSong = [...songs, ...historySongs].find(s => s.id === playingId) ?? null

  return (
    <div className="frame" data-screen="recommend">
      {toast && <div className="toast">{toast}</div>}

      <AppHeader
        leftSlot={
          <button className="iconbtn" onClick={() => navigate(`/concept?situationId=${situationId}`)}>
            ← 분위기 바꾸기
          </button>
        }
      />

      {/* 공유용 히든 카드 */}
      <div ref={shareCardRef} style={{
        position: 'fixed', left: '-9999px', top: 0, width: 360,
        background: 'var(--surface)', padding: 28, borderRadius: 20,
        fontFamily: 'system-ui, sans-serif'
      }}>
        <p style={{ fontSize: 13, color: 'var(--muted)', margin: '0 0 12px', letterSpacing: '0.1em' }}>🎵 MELODY PHARMACY · 멜로디약국</p>
        <p style={{ fontSize: 22, fontWeight: 700, margin: '0 0 4px', color: 'var(--ink)' }}>
          {situation?.icon} {situation?.name} × {concept?.icon} {concept?.name}
        </p>
        <p style={{ fontSize: 14, color: 'var(--ink-soft)', margin: '0 0 18px' }}>{rxTagline}</p>
        <div style={{ borderTop: '1px dashed var(--line)', paddingTop: 14, display: 'flex', flexDirection: 'column', gap: 8 }}>
          {songs.slice(0, 5).map((s, i) => (
            <div key={s.id} style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
              <span style={{ fontFamily: 'monospace', fontSize: 12, color: 'var(--muted)', minWidth: 20 }}>
                {String(i + 1).padStart(2, '0')}
              </span>
              <div>
                <p style={{ margin: 0, fontSize: 15, fontWeight: 600, color: 'var(--ink)' }}>{s.title}</p>
                <p style={{ margin: 0, fontSize: 12, color: 'var(--ink-soft)' }}>{s.artist}</p>
              </div>
            </div>
          ))}
          {songs.length > 5 && (
            <p style={{ margin: 0, fontSize: 12, color: 'var(--muted)', textAlign: 'center' }}>외 {songs.length - 5}곡 더</p>
          )}
        </div>
        <p style={{ fontSize: 11, color: 'var(--muted)', margin: '16px 0 0', textAlign: 'right' }}>{today}</p>
      </div>

      {/* 처방전 슬립 — 기본 접힘 */}
      <section className="rx-slip">
        <button
          className="rx-slip-head"
          style={{ width: '100%', cursor: 'pointer', background: 'none', border: 'none', padding: 0, textAlign: 'left' }}
          onClick={() => setRxOpen(p => !p)}
        >
          <div>
            <div className="brand" style={{ fontSize: 15 }}>
              <span className="rx" style={{ width: 24, height: 24, fontSize: 12 }}>Rx</span>
              <span>{situation?.icon} {situation?.name} · {concept?.icon} {concept?.name}</span>
            </div>
            <p style={{ margin: '4px 0 0', fontSize: 13, color: 'var(--ink-soft)', fontFamily: 'var(--font-body)' }}>
              {rxTagline}
            </p>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexShrink: 0 }}>
            {songs.length > 0 && (
              <button
                className="btn-ghost-sm"
                style={{ padding: '4px 10px', fontSize: 12 }}
                onClick={e => { e.stopPropagation(); handleSharePrescription() }}
                title="처방전 공유"
              >📤</button>
            )}
            <span style={{ fontFamily: 'var(--font-mono)', fontSize: 12, color: 'var(--muted)' }}>
              {songs.length}곡 {rxOpen ? '▲' : '▼'}
            </span>
          </div>
        </button>

        {rxOpen && (
          <>
            <div style={{ marginTop: 12 }}>
              <div className="rx-form">
                <div className="rx-form-cell head">환자명</div>
                <div className="rx-form-cell">{nickname} 님</div>
                <div className="rx-form-cell head">처방일</div>
                <div className="rx-form-cell">{today}</div>
                <div className="rx-form-cell head">진단</div>
                <div className="rx-form-cell">{situation?.icon} {situation?.name}</div>
                <div className="rx-form-cell head">처방</div>
                <div className="rx-form-cell">{concept?.icon} {concept?.name}</div>
              </div>
            </div>

            <div className="rp-header">
              <span className="rp-mark">℞</span>
              <div>
                <p className="rp-title">MEDICATION · 약품 명세</p>
                <p className="rp-sub">아래 곡들을 1일 1회, 기분이 나아질 때까지 복용하세요.</p>
              </div>
              <span className="rp-count">총 {songs.length}정</span>
            </div>

            <div className="rx-sig-block">
              <div className="rx-sig-text">
                <div><span className="label">조제자</span><strong>멜로디약국 약사</strong></div>
                <div><span className="label">용법용량</span>이어폰 착용 후 1일 1~3회</div>
                <div><span className="label">유효기간</span>기분이 좋아질 때까지</div>
              </div>
              <div className="pharmacy-stamp">
                <span className="pharmacy-stamp-line">MELODY</span>
                <span className="pharmacy-stamp-rx">℞</span>
                <span className="pharmacy-stamp-line">PHARMACY</span>
              </div>
            </div>
          </>
        )}
      </section>

      {/* 플레이어 */}
      {playingSong && (
        <div className="player">
          <div className="player-main">
            <div className="song-thumb" style={{ width: 48, height: 48, flexShrink: 0 }}>
              {playingSong.thumbnailUrl && (
                <img src={playingSong.thumbnailUrl} alt="" onError={handleThumbError} />
              )}
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
              src={`https://www.youtube.com/embed/${getVideoId(playingSong.youtubeUrl)}?enablejsapi=1&origin=${encodeURIComponent(window.location.origin)}`}
              title={playingSong.title}
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
              onLoad={handleIframeLoad}
            />
          </div>
        </div>
      )}

      {/* 탭 + 컨트롤 한 줄 */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 14, gap: 8, flexWrap: 'wrap' }}>
        <div className="tabs" style={{ marginBottom: 0 }}>
          <button className={`tab ${tab === 'recommend' ? 'active' : ''}`} onClick={() => setTab('recommend')}>
            💊 처방전
          </button>
          <button className={`tab ${tab === 'history' ? 'active' : ''}`} onClick={() => setTab('history')}>
            ⏱ 최근 {historySongs.length > 0 && <span className="badge">{historySongs.length}</span>}
          </button>
        </div>

        {tab === 'recommend' && (
          <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
            <button className="btn-ghost-sm" disabled={loading} onClick={() => loadRecommend()} title="새 처방전">🔄</button>
            <button className="btn-ghost-sm" disabled={loading || songs.length === 0} onClick={handleShuffle} title="순서 섞기">🔀</button>
            <button className={`toggle ${autoPlay ? 'on' : ''}`} onClick={() => setAutoPlay(p => !p)}>
              <span className="toggle-track"><span className="toggle-thumb" /></span>
              자동
            </button>
            <button className={`toggle ${excludePlayed ? 'on' : ''}`} onClick={handleToggleExclude}>
              <span className="toggle-track"><span className="toggle-thumb" /></span>
              들은 곡 제외
            </button>
          </div>
        )}
      </div>

      {error && (
        <div className="page-error">
          불러오기 실패했어요<br />
          <button onClick={() => loadRecommend()}>다시 시도</button>
        </div>
      )}

      {/* 노래 목록 */}
      <div className="song-list">
        {loading && Array.from({ length: 5 }).map((_, i) => (
          <div key={i} className="skel skel-song" />
        ))}
        {!loading && !error && currentSongs.length === 0 && (
          <div className="empty">
            {tab === 'recommend' ? (
              excludePlayed ? (
                <>
                  <p>들은 노래를 모두 제외했어요.</p>
                  <button className="btn-ghost-sm" onClick={handleToggleExclude}>전체 다시 보기</button>
                </>
              ) : (
                <>
                  <p>추천할 노래가 없어요.</p>
                  <button className="btn-ghost-sm" onClick={() => navigate(`/concept?situationId=${situationId}`)}>
                    다른 느낌으로 바꿔보기
                  </button>
                </>
              )
            ) : (
              <>
                <p>아직 들은 노래가 없어요.</p>
                <button className="btn-ghost-sm" onClick={() => setTab('recommend')}>처방전 보기</button>
              </>
            )}
          </div>
        )}
        {!loading && currentSongs.map((song, idx) => (
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
              <span className="dose-chip" style={{ marginTop: 4 }}>처방 {String(idx + 1).padStart(2, '0')}번</span>
            </div>
            <div className="song-actions">
              <span className={`play-btn ${playingId === song.id ? 'playing' : ''}`}>
                {playingId === song.id ? '❚❚' : '▶'}
              </span>
              <span
                className={`save-btn ${song.saved ? 'saved' : ''}`}
                onClick={e => handleSave(song, e)}
                style={savingIds.has(song.id) ? { opacity: 0.4, cursor: 'wait' } : undefined}
              >
                {savingIds.has(song.id) ? '…' : song.saved ? '♥' : '♡'}
              </span>
              <span className="share-btn" onClick={e => handleShare(song, e)}>📤</span>
            </div>
          </button>
        ))}
      </div>
    </div>
  )
}
