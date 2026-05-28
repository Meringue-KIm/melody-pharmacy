import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { signup, login } from '../api/authApi'
import '../styles/Auth.css'

const KAKAO_CLIENT_ID = import.meta.env.VITE_KAKAO_CLIENT_ID
const KAKAO_REDIRECT_URI = import.meta.env.VITE_KAKAO_REDIRECT_URI || 'http://localhost:5173/oauth/kakao'

export default function SignupPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '', nickname: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [showPw, setShowPw] = useState(false)

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
    if (form.password.length < 6) {
      setError('비밀번호는 6자 이상이어야 합니다.')
      return
    }
    if (form.nickname.trim().length < 2) {
      setError('닉네임은 2자 이상으로 입력해주세요.')
      return
    }
    if (form.nickname.length > 20) {
      setError('닉네임은 20자 이하로 입력해주세요.')
      return
    }
    setLoading(true)
    setError('')
    try {
      await signup(form)
      const res = await login({ email: form.email, password: form.password })
      localStorage.setItem('token', res.data.accessToken)
      localStorage.setItem('nickname', res.data.nickname)
      localStorage.setItem('provider', 'email')
      navigate('/')
    } catch (err: any) {
      if (err.response?.status === 400) {
        setError('이미 사용 중인 이메일입니다.')
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
        <p className="auth-subtitle">가입하고 나만의 처방전을 받아보세요</p>

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
              placeholder="비밀번호 (6자 이상)"
              value={form.password}
              onChange={e => handleChange('password', e.target.value)}
              required
            />
            <button type="button" className="pw-toggle" onClick={() => setShowPw(p => !p)}>
              {showPw ? '🙈' : '👁'}
            </button>
          </div>
          <input
            type="text"
            placeholder="닉네임 (2~20자)"
            value={form.nickname}
            onChange={e => handleChange('nickname', e.target.value)}
            required
          />
          {error && <p className="auth-error">{error}</p>}
          <button type="submit" disabled={loading}>
            {loading ? '가입 중...' : '회원가입'}
          </button>
        </form>

        <div className="auth-divider"><span>또는</span></div>
        <button type="button" className="kakao-btn" onClick={handleKakaoLogin}>
          카카오로 시작하기
        </button>

        <p className="auth-link">
          이미 계정이 있으신가요? <Link to="/login">로그인</Link>
        </p>
      </div>
    </div>
  )
}
