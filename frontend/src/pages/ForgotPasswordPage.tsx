import { useState } from 'react'
import { Link } from 'react-router-dom'
import { resetPassword } from '../api/authApi'
import '../styles/Auth.css'

export default function ForgotPasswordPage() {
  const [form, setForm] = useState({ email: '', newPassword: '', confirm: '' })
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const [loading, setLoading] = useState(false)
  const [showPw, setShowPw] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (form.newPassword !== form.confirm) {
      setError('비밀번호가 일치하지 않아요.')
      return
    }
    if (form.newPassword.length < 6) {
      setError('비밀번호는 6자 이상이어야 해요.')
      return
    }
    setLoading(true)
    setError('')
    try {
      await resetPassword(form.email, form.newPassword)
      setSuccess(true)
    } catch (err: any) {
      if (err.response?.status === 400) {
        setError(err.response.data?.message || '존재하지 않는 이메일이에요.')
      } else {
        setError('서버에 연결할 수 없어요. 잠시 후 다시 시도해주세요.')
      }
    } finally {
      setLoading(false)
    }
  }

  if (success) {
    return (
      <div className="auth-container">
        <div className="auth-box">
          <div className="auth-logo">🎵 멜로디약국</div>
          <p className="auth-subtitle" style={{ marginBottom: 24 }}>비밀번호가 변경됐어요!</p>
          <p style={{ color: 'rgba(255,255,255,0.6)', fontSize: 14, marginBottom: 28 }}>
            새 비밀번호로 로그인해주세요.
          </p>
          <Link to="/login" style={{ display: 'block', textAlign: 'center', color: '#a78bfa', fontSize: 15, fontWeight: 600 }}>
            로그인하러 가기 →
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="auth-container">
      <div className="auth-box">
        <div className="auth-logo">🎵 멜로디약국</div>
        <p className="auth-subtitle">비밀번호를 재설정해드릴게요</p>

        <form onSubmit={handleSubmit} className="auth-form">
          <input
            type="email"
            placeholder="가입한 이메일"
            value={form.email}
            onChange={e => setForm({ ...form, email: e.target.value })}
            required
          />
          <div className="password-wrap">
            <input
              type={showPw ? 'text' : 'password'}
              placeholder="새 비밀번호 (6자 이상)"
              value={form.newPassword}
              onChange={e => setForm({ ...form, newPassword: e.target.value })}
              required
            />
            <button type="button" className="pw-toggle" onClick={() => setShowPw(p => !p)}>
              {showPw ? '🙈' : '👁'}
            </button>
          </div>
          <input
            type="password"
            placeholder="새 비밀번호 확인"
            value={form.confirm}
            onChange={e => setForm({ ...form, confirm: e.target.value })}
            required
          />
          {error && <p className="auth-error">{error}</p>}
          <button type="submit" disabled={loading}>
            {loading ? '변경 중...' : '비밀번호 변경'}
          </button>
        </form>

        <p className="auth-link" style={{ marginTop: 20 }}>
          <Link to="/login">← 로그인으로 돌아가기</Link>
        </p>
      </div>
    </div>
  )
}
