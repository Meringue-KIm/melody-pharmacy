import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { kakaoLogin } from '../api/authApi'
import MascotHead from '../components/MascotHead'

export default function KakaoCallbackPage() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const [error, setError] = useState('')

  useEffect(() => {
    const code = searchParams.get('code')
    if (!code) { navigate('/login'); return }

    kakaoLogin(code)
      .then(res => {
        localStorage.setItem('token', res.data.accessToken)
        localStorage.setItem('nickname', res.data.nickname)
        localStorage.setItem('provider', 'kakao')
        navigate('/')
      })
      .catch(() => setError('카카오 로그인에 실패했어요. 다시 시도해주세요.'))
  }, [])

  if (error) return (
    <div className="frame" data-screen="login">
      <div className="auth-box" style={{ textAlign: 'center' }}>
        <div className="auth-header">
          <div className="brand brand-lg" style={{ justifyContent: 'center' }}>
            <span>멜로디약국</span>
          </div>
        </div>
        <p className="auth-error" style={{ margin: '20px 0' }}>{error}</p>
        <button className="btn btn-block" onClick={() => navigate('/login')}>
          로그인으로 돌아가기
        </button>
      </div>
    </div>
  )

  return (
    <div className="frame" data-screen="login">
      <div className="auth-box" style={{ textAlign: 'center' }}>
        <div className="auth-mascot">
          <MascotHead size={100} />
        </div>
        <div className="auth-header">
          <div className="brand brand-lg" style={{ justifyContent: 'center' }}>
            <span>멜로디약국</span>
          </div>
          <p className="auth-sub">카카오 로그인 처리 중…</p>
        </div>
      </div>
    </div>
  )
}
