import { useState, useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { login } from '../api/authApi'
import '../styles/Auth.css'

const KAKAO_CLIENT_ID = import.meta.env.VITE_KAKAO_CLIENT_ID
const KAKAO_REDIRECT_URI = import.meta.env.VITE_KAKAO_REDIRECT_URI || 'http://localhost:5173/oauth/kakao'

export default function LoginPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [showPw, setShowPw] = useState(false)
  const [expiredMsg, setExpiredMsg] = useState('')

  useEffect(() => {
    if (sessionStorage.getItem('sessionExpired')) {
      sessionStorage.removeItem('sessionExpired')
      setExpiredMsg('세션이 만료됐어요. 다시 로그인해주세요.')
    }
  }, [])

  const handleChange = (field: string, value: string) => {
    setForm(prev => ({ ...prev, [field]: value }))
    setError('')
  }

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
    <div className="auth-container">
      <div className="auth-box">
        <div className="auth-logo">🎵 멜로디약국</div>
        <p className="auth-subtitle">오늘 기분에 맞는 노래를 처방해드려요</p>

        <div className="auth-features">
          <span>💊 기분별 처방</span>
          <span>·</span>
          <span>🎵 50곡 큐레이션</span>
          <span>·</span>
          <span>❤️ 저장 &amp; 기록</span>
        </div>

        {expiredMsg && <p className="auth-expired">{expiredMsg}</p>}

        <form onSubmit={handleSubmit} className="auth-form">
          <input
            type="email"
            placeholder="이메일"
            value={form.email}
            onChange={e => handleChange('email', e.target.value)}
            required
          />
          <div className="password-wrap">
            <input
              type={showPw ? 'text' : 'password'}
              placeholder="비밀번호"
              value={form.password}
              onChange={e => handleChange('password', e.target.value)}
              required
            />
            <button type="button" className="pw-toggle" onClick={() => setShowPw(p => !p)}>
              {showPw ? '🙈' : '👁'}
            </button>
          </div>
          {error && <p className="auth-error">{error}</p>}
          <button type="submit" disabled={loading}>
            {loading ? '로그인 중...' : '로그인'}
          </button>
        </form>

        <div className="auth-divider"><span>또는</span></div>
        <button type="button" className="kakao-btn" onClick={handleKakaoLogin}>
          카카오로 로그인
        </button>

        <p className="auth-link">
          계정이 없으신가요? <Link to="/signup">회원가입</Link>
        </p>
      </div>
    </div>
  )
}
