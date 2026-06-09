import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { changePassword, updateNickname } from '../api/authApi'
import { getSaved, getHistory } from '../api/songApi'
import { isGuest, exitGuestMode, getGuestSaved, getGuestHistory } from '../utils/guestMode'
import { useTheme, type ThemeName } from '../context/ThemeContext'
import AppHeader from '../components/AppHeader'
import MascotHead from '../components/MascotHead'

const THEMES: { key: ThemeName; label: string; bg: string; accent: string; ink: string }[] = [
  { key: 'notebook', label: '노트', bg: '#F4EEFE', accent: '#8E6FE0', ink: '#2B1F44' },
  { key: 'receipt',  label: '영수증', bg: '#FFF4EC', accent: '#E8794A', ink: '#3A2418' },
  { key: 'cassette', label: '카세트', bg: '#E5F1FB', accent: '#3E84C9', ink: '#102740' },
  { key: 'sticker',  label: '스티커', bg: '#FFF8EE', accent: '#B58BE8', ink: '#2D2440' },
]

export default function ProfilePage() {
  const navigate = useNavigate()
  const { theme, dark, setTheme, setDark } = useTheme()
  const [nickname, setNickname] = useState(localStorage.getItem('nickname') || '환자')
  const isKakao = localStorage.getItem('provider') === 'kakao'

  const [nicknameInput, setNicknameInput] = useState(nickname)
  const [nicknameError, setNicknameError] = useState('')
  const [, setNicknameSuccess] = useState(false)
  const [nicknameSaving, setNicknameSaving] = useState(false)
  const [editingNickname, setEditingNickname] = useState(false)

  const [form, setForm] = useState({ oldPassword: '', newPassword: '', confirm: '' })
  const [showOld, setShowOld] = useState(false)
  const [showNew, setShowNew] = useState(false)
  const [showConfirm, setShowConfirm] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const [loading, setLoading] = useState(false)

  const [themeOpen, setThemeOpen] = useState(false)
  const [pwOpen, setPwOpen] = useState(false)

  const [savedCount, setSavedCount]   = useState(0)
  const [historyCount, setHistoryCount] = useState(0)
  const lastSelections = (() => { try { return JSON.parse(localStorage.getItem('lastSelections') || '[]') } catch { return [] } })()

  const [toast, setToast] = useState('')
  const showToast = (msg: string) => { setToast(msg); setTimeout(() => setToast(''), 3000) }
  const [confirmLogout, setConfirmLogout] = useState(false)

  const loadStats = () => {
    if (isGuest()) {
      setSavedCount(getGuestSaved().length)
      setHistoryCount(getGuestHistory().length)
    } else {
      getSaved().then(r => setSavedCount(r.data.length)).catch(() => {})
      getHistory().then(r => setHistoryCount(r.data.length)).catch(() => {})
    }
  }

  useEffect(() => {
    loadStats()
    const onVisible = () => { if (!document.hidden) loadStats() }
    document.addEventListener('visibilitychange', onVisible)
    const onSaved = () => loadStats()
    window.addEventListener('savedCountChanged', onSaved)
    return () => {
      document.removeEventListener('visibilitychange', onVisible)
      window.removeEventListener('savedCountChanged', onSaved)
    }
  }, [])

  const handleNicknameSave = async () => {
    const trimmed = nicknameInput.trim()
    if (trimmed.length < 2 || trimmed.length > 20) { setNicknameError('닉네임은 2~20자로 입력해주세요.'); return }
    setNicknameSaving(true)
    setNicknameError('')
    try {
      await updateNickname(trimmed)
      localStorage.setItem('nickname', trimmed)
      setNickname(trimmed)
      window.dispatchEvent(new CustomEvent('nicknameChanged', { detail: trimmed }))
      setEditingNickname(false)
      setNicknameSuccess(true)
      showToast('닉네임이 변경됐어요!')
      setTimeout(() => setNicknameSuccess(false), 2000)
    } catch (err: any) {
      setNicknameError(err.response?.data?.message || '닉네임 변경에 실패했어요.')
    } finally {
      setNicknameSaving(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (form.newPassword !== form.confirm) { setError('새 비밀번호가 일치하지 않아요.'); return }
    if (form.newPassword.length < 6) { setError('새 비밀번호는 6자 이상이어야 해요.'); return }
    setLoading(true)
    setError('')
    try {
      await changePassword(form.oldPassword, form.newPassword)
      setSuccess(true)
      showToast('비밀번호가 변경됐어요!')
      setTimeout(() => { setSuccess(false); setPwOpen(false); setForm({ oldPassword: '', newPassword: '', confirm: '' }) }, 2000)
    } catch (err: any) {
      setError(err.response?.data?.message || '비밀번호 변경에 실패했어요.')
    } finally {
      setLoading(false)
    }
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('nickname')
    localStorage.removeItem('lastSelections')
    localStorage.removeItem('lastSelection')
    localStorage.removeItem('provider')
    localStorage.removeItem('excludePlayed')
    localStorage.removeItem('savedFilterSit')
    exitGuestMode()
    navigate('/login')
  }

  if (isGuest()) {
    return (
      <div className="frame" data-screen="profile">
        <AppHeader />
        <section className="hero">
          <p className="eyebrow">Guest · 게스트</p>
          <h1 className="h1">둘러보는 중이에요</h1>
          <p className="subtitle">로그인하면 처방 기록이 저장되고<br />어디서든 약장을 확인할 수 있어요.</p>
        </section>
        <div className="section" style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          <div className="stat-grid">
            <div className="stat">
              <p className="stat-value">{historyCount}<span>곡</span></p>
              <p className="stat-label">들은 노래</p>
            </div>
            <div className="stat">
              <p className="stat-value">{savedCount}<span>곡</span></p>
              <p className="stat-label">저장한 노래</p>
            </div>
          </div>
          <button className="btn btn-block" onClick={() => { exitGuestMode(); navigate('/login') }}>
            로그인하고 기록 저장하기
          </button>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 4 }}>
            <span style={{ fontSize: 13, color: 'var(--muted)' }}>계정이 없으신가요?</span>
            <button
              className="btn-ghost-sm"
              onClick={() => navigate('/signup')}
            >
              회원가입
            </button>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="frame" data-screen="profile">
      {toast && <div className="toast">{toast}</div>}

      <AppHeader />

      <section className="hero">
        <p className="eyebrow">Membership Card · 회원증</p>
        <div className="profile-card">
          <div className="profile-avatar">
            <MascotHead size={60} />
          </div>
          <div className="profile-info">
            {editingNickname ? (
              <div className="profile-edit">
                <input
                  className="input input-inline"
                  value={nicknameInput}
                  onChange={e => { setNicknameInput(e.target.value); setNicknameError('') }}
                  maxLength={20}
                  autoFocus
                />
                <button className="btn-ghost-sm" onClick={handleNicknameSave} disabled={nicknameSaving}>
                  {nicknameSaving ? '…' : '저장'}
                </button>
                <button className="btn-ghost-sm" onClick={() => { setEditingNickname(false); setNicknameInput(nickname) }}>
                  취소
                </button>
              </div>
            ) : (
              <p className="profile-name">
                {nickname}<span className="profile-suffix">님</span>
                <button className="iconbtn iconbtn-mini" onClick={() => setEditingNickname(true)}>✎</button>
              </p>
            )}
            {nicknameError && <p style={{ color: 'var(--warn)', fontSize: 12, marginTop: 4 }}>{nicknameError}</p>}
            <p className="profile-meta">멜로디약국 회원 · {isKakao ? '카카오' : '이메일'}</p>
          </div>
          <div className="profile-stamp">
            <span className="rx-stamp">MEMBER</span>
          </div>
        </div>
      </section>

      {/* 처방 통계 */}
      <section className="section">
        <p className="eyebrow">처방 통계</p>
        <div className="stat-grid">
          <div className="stat">
            <p className="stat-value">{historyCount}<span>곡</span></p>
            <p className="stat-label">처방받은 노래</p>
          </div>
          <div className="stat">
            <p className="stat-value">{savedCount}<span>곡</span></p>
            <p className="stat-label">약장에 보관 중</p>
          </div>
          <div className="stat">
            <p className="stat-value">{lastSelections.length}<span>건</span></p>
            <p className="stat-label">최근 처방전</p>
          </div>
        </div>
      </section>

      {/* 설정 */}
      <section className="section">
        <p className="eyebrow">설정</p>
        <div className="menu">

          {/* 저장한 노래 */}
          <button className="menu-item" onClick={() => navigate('/saved')}>
            <span>♥ 저장한 노래</span>
            <span className="menu-meta">{savedCount}곡 →</span>
          </button>

          {/* 테마 선택 */}
          <button className="menu-item" onClick={() => setThemeOpen(p => !p)}>
            <span>🎨 테마</span>
            <span className="menu-meta">{THEMES.find(t => t.key === theme)?.label} {themeOpen ? '▲' : '▼'}</span>
          </button>
          {themeOpen && (
            <div className="theme-picker">
              <div className="theme-picker-grid">
                {THEMES.map(t => (
                  <button
                    key={t.key}
                    className={`theme-swatch ${theme === t.key ? 'active' : ''}`}
                    style={{ background: t.bg }}
                    onClick={() => setTheme(t.key)}
                  >
                    <span className="theme-swatch-dot" style={{ background: t.accent }} />
                    <span className="theme-swatch-label" style={{ color: t.ink }}>{t.label}</span>
                  </button>
                ))}
              </div>
              <button
                className={`toggle ${dark ? 'on' : ''}`}
                style={{ width: 'fit-content' }}
                onClick={() => setDark(!dark)}
              >
                <span className="toggle-track"><span className="toggle-thumb" /></span>
                다크 모드
              </button>
            </div>
          )}

          {/* 비밀번호 변경 */}
          {!isKakao && (
            <>
              <button className="menu-item" onClick={() => setPwOpen(p => !p)}>
                <span>🔑 비밀번호 변경</span>
                <span className="menu-meta">{pwOpen ? '▲' : '▼'}</span>
              </button>
              {pwOpen && (
                <div style={{
                  padding: '16px',
                  background: 'var(--surface-2)',
                  borderRadius: 'var(--r)',
                  border: 'var(--border-width) var(--border-style) var(--line)',
                  marginBottom: 6
                }}>
                  {success ? (
                    <p style={{ color: 'var(--good)', fontSize: 14 }}>비밀번호가 변경됐어요!</p>
                  ) : (
                    <form onSubmit={handleSubmit} className="auth-form">
                      <div className="pw-wrap">
                        <input className="input" type={showOld ? 'text' : 'password'} placeholder="현재 비밀번호"
                               value={form.oldPassword} onChange={e => { setForm(p => ({...p, oldPassword: e.target.value})); setError('') }} required />
                        <button type="button" className="pw-toggle" onClick={() => setShowOld(p => !p)}>
                          {showOld ? '🙈' : '👁'}
                        </button>
                      </div>
                      <div className="pw-wrap">
                        <input className="input" type={showNew ? 'text' : 'password'} placeholder="새 비밀번호 (6자 이상)"
                               value={form.newPassword} onChange={e => { setForm(p => ({...p, newPassword: e.target.value})); setError('') }} required />
                        <button type="button" className="pw-toggle" onClick={() => setShowNew(p => !p)}>
                          {showNew ? '🙈' : '👁'}
                        </button>
                      </div>
                      <div className="pw-wrap">
                        <input className="input" type={showConfirm ? 'text' : 'password'} placeholder="새 비밀번호 확인"
                               value={form.confirm} onChange={e => { setForm(p => ({...p, confirm: e.target.value})); setError('') }} required />
                        <button type="button" className="pw-toggle" onClick={() => setShowConfirm(p => !p)}>
                          {showConfirm ? '🙈' : '👁'}
                        </button>
                      </div>
                      {error && <p className="auth-error">{error}</p>}
                      <button type="submit" className="btn btn-block" disabled={loading}>
                        {loading ? '변경 중…' : '비밀번호 변경'}
                      </button>
                    </form>
                  )}
                </div>
              )}
            </>
          )}
          {isKakao && (
            <div className="menu-item" style={{ cursor: 'default', color: 'var(--muted)' }}>
              <span>🔑 비밀번호 변경</span>
              <span className="menu-meta">카카오 계정</span>
            </div>
          )}

          {/* 로그아웃 */}
          {confirmLogout ? (
            <div style={{ display: 'flex', gap: 8, alignItems: 'center', padding: '12px 16px', background: 'var(--surface)', border: 'var(--border-width) var(--border-style) var(--line)', borderRadius: 'var(--r)' }}>
              <span style={{ flex: 1, fontSize: 14, color: 'var(--ink)' }}>정말 로그아웃할까요?</span>
              <button className="btn-ghost-sm" style={{ color: 'var(--warn)', borderColor: 'var(--warn)' }} onClick={handleLogout}>로그아웃</button>
              <button className="btn-ghost-sm" onClick={() => setConfirmLogout(false)}>취소</button>
            </div>
          ) : (
            <button className="menu-item menu-danger" onClick={() => setConfirmLogout(true)}>
              <span>로그아웃</span>
            </button>
          )}
        </div>
      </section>
    </div>
  )
}
