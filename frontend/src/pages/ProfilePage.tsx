import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { changePassword, updateNickname } from '../api/authApi'
import '../styles/Auth.css'

export default function ProfilePage() {
  const navigate = useNavigate()
  const [nickname, setNickname] = useState(localStorage.getItem('nickname') || '환자')
  const isKakao = localStorage.getItem('provider') === 'kakao'

  const [nicknameInput, setNicknameInput] = useState(nickname)
  const [nicknameError, setNicknameError] = useState('')
  const [nicknameSuccess, setNicknameSuccess] = useState(false)
  const [nicknameSaving, setNicknameSaving] = useState(false)

  const [form, setForm] = useState({ oldPassword: '', newPassword: '', confirm: '' })
  const [showOld, setShowOld] = useState(false)
  const [showNew, setShowNew] = useState(false)
  const [showConfirm, setShowConfirm] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const [loading, setLoading] = useState(false)

  const handleChange = (field: string, value: string) => {
    setForm(prev => ({ ...prev, [field]: value }))
    setError('')
  }

  const handleNicknameSave = async () => {
    const trimmed = nicknameInput.trim()
    if (trimmed.length < 2 || trimmed.length > 20) {
      setNicknameError('닉네임은 2~20자로 입력해주세요.')
      return
    }
    setNicknameSaving(true)
    setNicknameError('')
    setNicknameSuccess(false)
    try {
      await updateNickname(trimmed)
      localStorage.setItem('nickname', trimmed)
      setNickname(trimmed)
      setNicknameSuccess(true)
      setTimeout(() => setNicknameSuccess(false), 2000)
    } catch (err: any) {
      setNicknameError(err.response?.data?.message || '닉네임 변경에 실패했어요.')
    } finally {
      setNicknameSaving(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (form.newPassword !== form.confirm) {
      setError('새 비밀번호가 일치하지 않아요.')
      return
    }
    if (form.newPassword.length < 6) {
      setError('새 비밀번호는 6자 이상이어야 해요.')
      return
    }
    setLoading(true)
    setError('')
    try {
      await changePassword(form.oldPassword, form.newPassword)
      setSuccess(true)
    } catch (err: any) {
      setError(err.response?.data?.message || '비밀번호 변경에 실패했어요.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-box">
        <div className="auth-logo">🎵 멜로디약국</div>
        <p className="auth-subtitle">{nickname}님의 설정</p>

        {/* 닉네임 변경 */}
        <div className="profile-section">
          <p className="profile-section-label">닉네임 변경</p>
          <div style={{ display: 'flex', gap: 8 }}>
            <input
              type="text"
              placeholder="새 닉네임 (2~20자)"
              value={nicknameInput}
              onChange={e => { setNicknameInput(e.target.value); setNicknameError('') }}
              style={{ flex: 1, padding: '12px 14px', borderRadius: 10, border: '1px solid rgba(255,255,255,0.15)',
                       background: 'rgba(255,255,255,0.07)', color: '#fff', fontSize: 14 }}
            />
            <button
              type="button"
              onClick={handleNicknameSave}
              disabled={nicknameSaving}
              style={{ padding: '12px 16px', borderRadius: 10, border: 'none',
                       background: '#7c3aed', color: '#fff', fontSize: 14,
                       fontWeight: 600, cursor: 'pointer', whiteSpace: 'nowrap' }}
            >
              {nicknameSaving ? '...' : '변경'}
            </button>
          </div>
          {nicknameError && <p className="auth-error" style={{ marginTop: 6 }}>{nicknameError}</p>}
          {nicknameSuccess && <p style={{ color: '#a78bfa', fontSize: 13, marginTop: 6 }}>닉네임이 변경됐어요!</p>}
        </div>

        <div className="profile-divider" />

        {/* 비밀번호 변경 */}
        <div className="profile-section">
          <p className="profile-section-label">비밀번호 변경</p>
          {isKakao ? (
            <p style={{ color: 'rgba(255,255,255,0.45)', fontSize: 14, lineHeight: 1.6 }}>
              카카오 로그인 계정은<br />비밀번호를 변경할 수 없어요.
            </p>
          ) : success ? (
            <div style={{ textAlign: 'center' }}>
              <p style={{ color: 'rgba(255,255,255,0.8)', fontSize: 15, marginBottom: 20 }}>
                비밀번호가 변경됐어요!
              </p>
              <button
                onClick={() => navigate('/')}
                style={{ width: '100%', padding: 14, borderRadius: 12, border: 'none',
                         background: '#7c3aed', color: '#fff', fontSize: 15,
                         fontWeight: 600, cursor: 'pointer' }}
              >홈으로 가기</button>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="auth-form" style={{ marginTop: 0 }}>
              <div className="password-wrap">
                <input
                  type={showOld ? 'text' : 'password'}
                  placeholder="현재 비밀번호"
                  value={form.oldPassword}
                  onChange={e => handleChange('oldPassword', e.target.value)}
                  required
                />
                <button type="button" className="pw-toggle" onClick={() => setShowOld(p => !p)}>
                  {showOld ? '🙈' : '👁'}
                </button>
              </div>
              <div className="password-wrap">
                <input
                  type={showNew ? 'text' : 'password'}
                  placeholder="새 비밀번호 (6자 이상)"
                  value={form.newPassword}
                  onChange={e => handleChange('newPassword', e.target.value)}
                  required
                />
                <button type="button" className="pw-toggle" onClick={() => setShowNew(p => !p)}>
                  {showNew ? '🙈' : '👁'}
                </button>
              </div>
              <div className="password-wrap">
                <input
                  type={showConfirm ? 'text' : 'password'}
                  placeholder="새 비밀번호 확인"
                  value={form.confirm}
                  onChange={e => handleChange('confirm', e.target.value)}
                  required
                />
                <button type="button" className="pw-toggle" onClick={() => setShowConfirm(p => !p)}>
                  {showConfirm ? '🙈' : '👁'}
                </button>
              </div>
              {error && <p className="auth-error">{error}</p>}
              <button type="submit" disabled={loading}>
                {loading ? '변경 중...' : '비밀번호 변경'}
              </button>
            </form>
          )}
        </div>

        <p className="auth-link" style={{ marginTop: 20 }}>
          <span
            style={{ cursor: 'pointer', color: 'rgba(255,255,255,0.4)', fontSize: 13 }}
            onClick={() => navigate('/')}
          >← 홈으로 돌아가기</span>
        </p>
      </div>
    </div>
  )
}
