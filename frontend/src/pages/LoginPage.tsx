import { useState, useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { login } from '../api/authApi'
import { enterGuestMode, isGuest, migrateGuestDataToServer } from '../utils/guestMode'
import { saveSong } from '../api/songApi'
import mascotPrescribe from '../assets/mascot-prescribe.png'

const KAKAO_CLIENT_ID = import.meta.env.VITE_KAKAO_CLIENT_ID
const KAKAO_REDIRECT_URI = import.meta.env.VITE_KAKAO_REDIRECT_URI || 'http://localhost:5173/oauth/kakao'

export default function LoginPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [migrating, setMigrating] = useState(false)
  const [showPw, setShowPw] = useState(false)
  const [expiredMsg, setExpiredMsg] = useState('')

  useEffect(() => {
    if (sessionStorage.getItem('sessionExpired')) {
      sessionStorage.removeItem('sessionExpired')
      setExpiredMsg('세션이 만료됐어요. 다시 로그인해주세요.')
    }
  }, [])

  const handleKakaoLogin = () => {
    window.location.href =
      `https://kauth.kakao.com/oauth/authorize?client_id=${KAKAO_CLIENT_ID}&redirect_uri=${encodeURIComponent(KAKAO_REDIRECT_URI)}&response_type=code`
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      const res = await login(form)
      localStorage.setItem('token', res.data.accessToken)
      localStorage.setItem('nickname', res.data.nickname)
      localStorage.setItem('provider', 'email')
      if (isGuest()) {
        setMigrating(true)
        await migrateGuestDataToServer(saveSong)
      }
      navigate('/')
    } catch (err: any) {
      if (err.response?.status === 400 || err.response?.status === 401) {
        setError('이메일 또는 비밀번호가 올바르지 않습니다.')
      } else {
        setError('서버에 연결할 수 없어요. 잠시 후 다시 시도해주세요.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="frame" data-screen="login">
      <div className="auth-box">
        <div className="auth-mascot">
          <img src={mascotPrescribe} alt="멜로디약국 약사" style={{ width: 150, height: 150, objectFit: 'contain' }} />
        </div>
        <div className="auth-header">
          <div className="brand brand-lg" style={{ justifyContent: 'center' }}>
            <span>멜로디약국</span>
          </div>
          <p className="auth-sub">오늘 기분에 맞는 노래를 처방해드려요</p>
        </div>

        <div className="auth-features">
          <span>💊 기분별 처방</span>
          <span className="dot">·</span>
          <span>♪ 200곡+ 큐레이션</span>
          <span className="dot">·</span>
          <span>♡ 저장 &amp; 기록</span>
        </div>

        {expiredMsg && <p className="auth-error">{expiredMsg}</p>}

        <form onSubmit={handleSubmit} className="auth-form">
          <input
            className="input"
            type="email"
            placeholder="이메일"
            value={form.email}
            onChange={e => { setForm(p => ({ ...p, email: e.target.value })); setError('') }}
            required
          />
          <div className="pw-wrap">
            <input
              className="input"
              type={showPw ? 'text' : 'password'}
              placeholder="비밀번호"
              value={form.password}
              onChange={e => { setForm(p => ({ ...p, password: e.target.value })); setError('') }}
              required
            />
            <button type="button" className="pw-toggle" onClick={() => setShowPw(p => !p)}>
              {showPw ? '🙈' : '👁'}
            </button>
          </div>
          {error && <p className="auth-error">{error}</p>}
          <button type="submit" className="btn btn-block" disabled={loading || migrating}>
            {migrating ? '저장 기록 옮기는 중…' : loading ? '처방전 받는 중…' : '로그인'}
          </button>
        </form>

        <p style={{ textAlign: 'right', marginTop: 8 }}>
          <Link to="/forgot-password" style={{ fontSize: 13, color: 'var(--muted)', borderBottom: '1px solid var(--line)' }}>
            비밀번호를 잊으셨나요?
          </Link>
        </p>

        <div className="divider"><span>또는</span></div>
        <button type="button" className="kakao-btn" onClick={handleKakaoLogin}>
          <span className="kakao-icon">💬</span> 카카오로 시작하기
        </button>

        <p className="auth-link">
          계정이 없으신가요? <Link to="/signup">회원가입</Link>
        </p>

        <div style={{ marginTop: 16, textAlign: 'center' }}>
          <button
            type="button"
            onClick={() => { enterGuestMode(); navigate('/') }}
            style={{ background: 'none', border: 'none', color: 'var(--muted)', fontSize: 13, cursor: 'pointer', textDecoration: 'underline', textUnderlineOffset: 3 }}
          >
            로그인 없이 둘러보기 →
          </button>
        </div>
      </div>
    </div>
  )
}
