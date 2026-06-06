import { useState, useEffect } from 'react'
import api from '../api/axios'
import { SITUATIONS, CONCEPTS } from '../data/guestData'

const ADMIN_TOKEN_KEY = 'adminToken'

interface PlaylistVideo {
  id: number
  youtubeVideoId: string
  title: string
  thumbnailUrl: string
}

export default function AdminPage() {
  const [token, setToken] = useState(sessionStorage.getItem(ADMIN_TOKEN_KEY) || '')
  const [tokenInput, setTokenInput] = useState('')
  const [authed, setAuthed] = useState(!!sessionStorage.getItem(ADMIN_TOKEN_KEY))

  const [selectedSit, setSelectedSit] = useState(SITUATIONS[0].id)
  const [selectedCon, setSelectedCon] = useState(CONCEPTS[0].id)
  const [playlists, setPlaylists] = useState<PlaylistVideo[]>([])
  const [loading, setLoading] = useState(false)

  const [urlInput, setUrlInput] = useState('')
  const [titleInput, setTitleInput] = useState('')
  const [adding, setAdding] = useState(false)
  const [msg, setMsg] = useState('')

  const adminApi = api.create ? api : api
  const headers = { 'X-Admin-Token': token }

  const login = async () => {
    try {
      await api.get('/api/playlists/all', { headers: { 'X-Admin-Token': tokenInput } })
      setToken(tokenInput)
      sessionStorage.setItem(ADMIN_TOKEN_KEY, tokenInput)
      setAuthed(true)
    } catch {
      setMsg('토큰이 올바르지 않아요.')
    }
  }

  const loadPlaylists = async () => {
    setLoading(true)
    try {
      const res = await api.get<PlaylistVideo[]>('/api/playlists', {
        params: { situationId: selectedSit, conceptId: selectedCon }
      })
      setPlaylists(res.data)
    } catch {
      setPlaylists([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { if (authed) loadPlaylists() }, [selectedSit, selectedCon, authed])

  const handleAdd = async () => {
    if (!urlInput.trim() || !titleInput.trim()) { setMsg('URL과 제목을 모두 입력해주세요.'); return }
    setAdding(true)
    setMsg('')
    try {
      await api.post('/api/playlists', {
        situationId: selectedSit, conceptId: selectedCon,
        youtubeUrl: urlInput.trim(), title: titleInput.trim()
      }, { headers })
      setUrlInput('')
      setTitleInput('')
      setMsg('추가됐어요!')
      loadPlaylists()
    } catch (e: any) {
      setMsg(e.response?.data || '추가 실패')
    } finally {
      setAdding(false)
    }
  }

  const handleDelete = async (id: number) => {
    if (!confirm('삭제할까요?')) return
    try {
      await api.delete(`/api/playlists/${id}`, { headers })
      loadPlaylists()
    } catch {
      setMsg('삭제 실패')
    }
  }

  const sit = SITUATIONS.find(s => s.id === selectedSit)
  const con = CONCEPTS.find(c => c.id === selectedCon)

  if (!authed) {
    return (
      <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#f5f5f5' }}>
        <div style={{ background: 'white', padding: 32, borderRadius: 16, width: 320, boxShadow: '0 4px 20px rgba(0,0,0,0.1)' }}>
          <h2 style={{ margin: '0 0 20px', fontSize: 20 }}>🔑 Admin 로그인</h2>
          <input
            type="password"
            placeholder="Admin 토큰"
            value={tokenInput}
            onChange={e => setTokenInput(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && login()}
            style={{ width: '100%', padding: '10px 12px', border: '1px solid #ddd', borderRadius: 8, fontSize: 14, boxSizing: 'border-box', marginBottom: 12 }}
          />
          {msg && <p style={{ color: 'red', fontSize: 13, margin: '0 0 12px' }}>{msg}</p>}
          <button
            onClick={login}
            style={{ width: '100%', padding: '11px 0', background: '#6c47ff', color: 'white', border: 'none', borderRadius: 8, fontSize: 15, cursor: 'pointer' }}
          >
            로그인
          </button>
        </div>
      </div>
    )
  }

  return (
    <div style={{ maxWidth: 700, margin: '0 auto', padding: '24px 16px', fontFamily: 'system-ui, sans-serif' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <h1 style={{ margin: 0, fontSize: 22 }}>🎵 플레이리스트 관리</h1>
        <button
          onClick={() => { sessionStorage.removeItem(ADMIN_TOKEN_KEY); setAuthed(false); setToken('') }}
          style={{ background: 'none', border: '1px solid #ddd', borderRadius: 8, padding: '6px 14px', cursor: 'pointer', fontSize: 13 }}
        >로그아웃</button>
      </div>

      {/* 조합 선택 */}
      <div style={{ background: '#f8f8f8', borderRadius: 12, padding: 16, marginBottom: 20 }}>
        <p style={{ margin: '0 0 12px', fontWeight: 600, fontSize: 14 }}>조합 선택</p>
        <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
          <div style={{ flex: 1, minWidth: 140 }}>
            <label style={{ fontSize: 12, color: '#666', display: 'block', marginBottom: 4 }}>상황</label>
            <select
              value={selectedSit}
              onChange={e => setSelectedSit(Number(e.target.value))}
              style={{ width: '100%', padding: '8px 10px', border: '1px solid #ddd', borderRadius: 8, fontSize: 14 }}
            >
              {SITUATIONS.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
            </select>
          </div>
          <div style={{ flex: 1, minWidth: 140 }}>
            <label style={{ fontSize: 12, color: '#666', display: 'block', marginBottom: 4 }}>분위기</label>
            <select
              value={selectedCon}
              onChange={e => setSelectedCon(Number(e.target.value))}
              style={{ width: '100%', padding: '8px 10px', border: '1px solid #ddd', borderRadius: 8, fontSize: 14 }}
            >
              {CONCEPTS.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </div>
        </div>
        <p style={{ margin: '12px 0 0', fontSize: 13, color: '#888' }}>
          현재: <strong>{sit?.name} × {con?.name}</strong> — 플레이리스트 {playlists.length}개
        </p>
      </div>

      {/* 플레이리스트 추가 */}
      <div style={{ background: 'white', border: '1px solid #e8e8e8', borderRadius: 12, padding: 16, marginBottom: 20 }}>
        <p style={{ margin: '0 0 12px', fontWeight: 600, fontSize: 14 }}>➕ 플레이리스트 추가</p>
        <input
          placeholder="YouTube URL (예: https://www.youtube.com/watch?v=xxxxx)"
          value={urlInput}
          onChange={e => setUrlInput(e.target.value)}
          style={{ width: '100%', padding: '9px 12px', border: '1px solid #ddd', borderRadius: 8, fontSize: 13, boxSizing: 'border-box', marginBottom: 8 }}
        />
        <input
          placeholder="영상 제목 (예: 혼자 집에 가는 길에 듣는 팝송)"
          value={titleInput}
          onChange={e => setTitleInput(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleAdd()}
          style={{ width: '100%', padding: '9px 12px', border: '1px solid #ddd', borderRadius: 8, fontSize: 13, boxSizing: 'border-box', marginBottom: 10 }}
        />
        {msg && <p style={{ color: msg.includes('추가') ? 'green' : 'red', fontSize: 13, margin: '0 0 8px' }}>{msg}</p>}
        <button
          onClick={handleAdd}
          disabled={adding}
          style={{ background: '#6c47ff', color: 'white', border: 'none', borderRadius: 8, padding: '10px 20px', fontSize: 14, cursor: 'pointer', opacity: adding ? 0.6 : 1 }}
        >
          {adding ? '추가 중…' : '추가하기'}
        </button>
      </div>

      {/* 등록된 플레이리스트 */}
      <div>
        <p style={{ fontWeight: 600, fontSize: 14, margin: '0 0 12px' }}>📋 등록된 플레이리스트</p>
        {loading && <p style={{ color: '#999', fontSize: 14 }}>불러오는 중…</p>}
        {!loading && playlists.length === 0 && (
          <div style={{ textAlign: 'center', padding: '32px 0', color: '#aaa', fontSize: 14 }}>
            아직 등록된 플레이리스트가 없어요.
          </div>
        )}
        {playlists.map(pv => (
          <div key={pv.id} style={{ display: 'flex', gap: 12, alignItems: 'center', padding: '12px 0', borderBottom: '1px solid #f0f0f0' }}>
            <img
              src={pv.thumbnailUrl}
              alt={pv.title}
              style={{ width: 80, height: 56, objectFit: 'cover', borderRadius: 8, flexShrink: 0, background: '#eee' }}
            />
            <div style={{ flex: 1, minWidth: 0 }}>
              <p style={{ margin: '0 0 4px', fontSize: 14, fontWeight: 500, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{pv.title}</p>
              <a
                href={`https://www.youtube.com/watch?v=${pv.youtubeVideoId}`}
                target="_blank"
                rel="noreferrer"
                style={{ fontSize: 12, color: '#6c47ff' }}
              >
                {pv.youtubeVideoId}
              </a>
            </div>
            <button
              onClick={() => handleDelete(pv.id)}
              style={{ background: 'none', border: '1px solid #ffdddd', color: '#e55', borderRadius: 8, padding: '6px 12px', cursor: 'pointer', fontSize: 12, flexShrink: 0 }}
            >
              삭제
            </button>
          </div>
        ))}
      </div>
    </div>
  )
}
