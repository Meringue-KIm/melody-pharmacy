import { useState } from 'react'
import { Link } from 'react-router-dom'
import { resetPassword } from '../api/authApi'
import mascotLab from '../assets/mascot-lab.png'

export default function ForgotPasswordPage() {
  const [step, setStep] = useState<'form' | 'done'>('form')
  const [form, setForm] = useState({ email: '', newPassword: '', confirm: '' })
  const [showPw, setShowPw] = useState(false)
  const [showConfirm, setShowConfirm] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (form.newPassword.length < 6) { setError('새 비밀번호는 6자 이상이어야 해요.'); return }
    if (form.newPassword !== form.confirm) { setError('비밀번호가 일치하지 않아요.'); return }
    setLoading(true)
    setError('')
    try {
      await resetPassword(form.email, form.newPassword)
      setStep('done')
    } catch (err: any) {
      if (err.response?.status === 404) setError('가입되지 않은 이메일이에요.')
      else setError('비밀번호 재설정에 실패했어요. 잠시 후 다시 시도해주세요.')
    } finally {
      setLoading(false)
    }
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
          <p className="auth-sub">비밀번호 재설정</p>
        </div>

        {step === 'done' ? (
          <div style={{ textAlign: 'center' }}>
            <p style={{ fontSize: 15, color: 'var(--good)', marginBottom: 20 }}>
              비밀번호가 변경됐어요!
            </p>
            <Link to="/login" className="btn btn-block" style={{ display: 'flex', justifyContent: 'center' }}>
              로그인하러 가기
            </Link>
          </div>
        ) : (
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
            <div className="pw-wrap">
              <input
                className="input"
                type={showConfirm ? 'text' : 'password'}
                placeholder="새 비밀번호 확인"
                value={form.confirm}
                onChange={e => { setForm(p => ({ ...p, confirm: e.target.value })); setError('') }}
                required
              />
              <button type="button" className="pw-toggle" onClick={() => setShowConfirm(p => !p)}>
                {showConfirm ? '🙈' : '👁'}
              </button>
            </div>
            {error && <p className="auth-error">{error}</p>}
            <button type="submit" className="btn btn-block" disabled={loading}>
              {loading ? '처리 중…' : '비밀번호 재설정'}
            </button>
          </form>
        )}

        <p className="auth-link" style={{ marginTop: 18 }}>
          <Link to="/login">← 로그인으로 돌아가기</Link>
        </p>
      </div>
    </div>
  )
}
