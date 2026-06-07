import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { signup, login } from '../api/authApi'
import { saveSong } from '../api/songApi'
import { isGuest, migrateGuestDataToServer } from '../utils/guestMode'
import mascotPresent from '../assets/mascot-present.png'

const KAKAO_CLIENT_ID = import.meta.env.VITE_KAKAO_CLIENT_ID
const KAKAO_REDIRECT_URI = import.meta.env.VITE_KAKAO_REDIRECT_URI || 'http://localhost:5173/oauth/kakao'

export default function SignupPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '', nickname: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [showPw, setShowPw] = useState(false)

  const handleKakaoLogin = () => {
    window.location.href =
      `https://kauth.kakao.com/oauth/authorize?client_id=${KAKAO_CLIENT_ID}&redirect_uri=${encodeURIComponent(KAKAO_REDIRECT_URI)}&response_type=code`
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (form.password.length < 6) { setError('비밀번호는 6자 이상이어야 합니다.'); return }
    if (form.nickname.trim().length < 2) { setError('닉네임은 2자 이상으로 입력해주세요.'); return }
    if (form.nickname.length > 20) { setError('닉네임은 20자 이하로 입력해주세요.'); return }
    setLoading(true)
    setError('')
    try {
      await signup(form)
      const res = await login({ email: form.email, password: form.password })
      localStorage.setItem('token', res.data.accessToken)
      localStorage.setItem('nickname', res.data.nickname)
      localStorage.setItem('provider', 'email')
      if (isGuest()) await migrateGuestDataToServer(saveSong)
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
    <div className="frame" data-screen="signup">
      <div className="auth-box">
        <div className="auth-mascot">
          <img src={mascotPresent} alt="멜로디약국 약사" style={{ width: 140, height: 140, objectFit: 'contain' }} />
        </div>
        <div className="auth-header">
          <div className="brand brand-lg" style={{ justifyContent: 'center' }}>
            <span>멜로디약국</span>
          </div>
          <p className="auth-sub">가입하고 나만의 처방전을 받아보세요</p>
        </div>

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
              placeholder="비밀번호 (6자 이상)"
              value={form.password}
              onChange={e => { setForm(p => ({ ...p, password: e.target.value })); setError('') }}
              required
            />
            <button type="button" className="pw-toggle" onClick={() => setShowPw(p => !p)}>
              {showPw ? '🙈' : '👁'}
            </button>
          </div>
          <input
            className="input"
            type="text"
            placeholder="닉네임 (2~20자)"
            value={form.nickname}
            onChange={e => { setForm(p => ({ ...p, nickname: e.target.value })); setError('') }}
            required
          />
          {error && <p className="auth-error">{error}</p>}
          <button type="submit" className="btn btn-block" disabled={loading}>
            {loading ? '가입 중…' : '회원가입'}
          </button>
        </form>

        <div className="divider"><span>또는</span></div>
        <button type="button" className="kakao-btn" onClick={handleKakaoLogin}>
          <span className="kakao-icon">💬</span> 카카오로 시작하기
        </button>

        <p className="auth-link">
          이미 계정이 있으신가요? <Link to="/login">로그인</Link>
        </p>
      </div>
    </div>
  )
}
