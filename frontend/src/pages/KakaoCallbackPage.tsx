import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { kakaoLogin } from '../api/authApi'
import '../styles/Auth.css'

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
    <div className="auth-container">
      <div className="auth-box">
        <div className="auth-logo">🎵 멜로디약국</div>
        <p style={{ color: '#f87171', margin: '20px 0' }}>{error}</p>
        <button
          style={{ padding: '12px 24px', background: '#7c3aed', border: 'none', borderRadius: '10px', color: '#fff', cursor: 'pointer' }}
          onClick={() => navigate('/login')}
        >
          로그인으로 돌아가기
        </button>
      </div>
    </div>
  )

  return (
    <div className="auth-container">
      <div className="auth-box">
        <div className="auth-logo">🎵 멜로디약국</div>
        <p style={{ color: 'rgba(255,255,255,0.6)', marginTop: '20px' }}>카카오 로그인 처리 중...</p>
      </div>
    </div>
  )
}
