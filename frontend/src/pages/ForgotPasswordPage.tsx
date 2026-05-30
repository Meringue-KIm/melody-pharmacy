import { useState } from 'react'
import { Link } from 'react-router-dom'
import { resetPassword } from '../api/authApi'
import mascotLab from '../assets/mascot-lab.png'

export default function ForgotPasswordPage() {
  const [form, setForm] = useState({ email: '', newPassword: '', confirm: '' })
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const [loading, setLoading] = useState(false)
  const [showPw, setShowPw] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (form.newPassword !== form.confirm) { setError('비밀번호가 일치하지 않아요.'); return }
    if (form.newPassword.length < 6) { setError('비밀번호는 6자 이상이어야 해요.'); return }
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
      <div className="frame" data-screen="forgot">
        <div className="auth-box" style={{ textAlign: 'center' }}>
          <div className="auth-header">
            <div className="brand brand-lg" style={{ justifyContent: 'center' }}>
              <span className="rx">Rx</span>
              <span>멜로디약국</span>
            </div>
            <p className="auth-sub" style={{ marginTop: 8 }}>비밀번호가 변경됐어요!</p>
          </div>
          <p style={{ color: 'var(--muted)', fontSize: 14, marginBottom: 28 }}>새 비밀번호로 로그인해주세요.</p>
          <Link to="/login" className="btn btn-block" style={{ display: 'flex' }}>
            로그인하러 가기 →
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="frame" data-screen="forgot">
      <div className="auth-box">
        <div className="auth-mascot">
          <img src={mascotLab} alt="멜로디약국 약사" style={{ width: 120, height: 120, objectFit: 'contain' }} />
        </div>
        <div className="auth-header">
          <div className="brand brand-lg" style={{ justifyContent: 'center' }}>
            <span>멜로디약국</span>
          </div>
          <p className="auth-sub">비밀번호를 재설정해드릴게요</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <input
            className="input"
            type="email"
            placeholder="가입한 이메일"
            value={form.email}
            onChange={e => { setForm(p => ({ ...p, email: e.target.value })); setError('') }}
            required
          />
          <div className="pw-wrap">
            <input
              className="input"
              type={showPw ? 'text' : 'password'}
              placeholder="새 비밀번호 (6자 이상)"
              value={form.newPassword}
              onChange={e => { setForm(p => ({ ...p, newPassword: e.target.value })); setError('') }}
              required
            />
            <button type="button" className="pw-toggle" onClick={() => setShowPw(p => !p)}>
              {showPw ? '🙈' : '👁'}
            </button>
          </div>
          <input
            className="input"
            type="password"
            placeholder="새 비밀번호 확인"
            value={form.confirm}
            onChange={e => { setForm(p => ({ ...p, confirm: e.target.value })); setError('') }}
            required
          />
          {error && <p className="auth-error">{error}</p>}
          <button type="submit" className="btn btn-block" disabled={loading}>
            {loading ? '변경 중…' : '비밀번호 변경'}
          </button>
        </form>

        <p className="auth-link" style={{ marginTop: 20 }}>
          <Link to="/login">← 로그인으로 돌아가기</Link>
        </p>
      </div>
    </div>
  )
}
