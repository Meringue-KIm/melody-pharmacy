import { useNavigate, useLocation } from 'react-router-dom'

export default function BottomNav() {
  const navigate = useNavigate()
  const { pathname } = useLocation()

  const isHome    = pathname === '/' || pathname.startsWith('/concept') || pathname.startsWith('/recommend')
  const isSaved   = pathname === '/saved'
  const isProfile = pathname === '/profile'

  return (
    <nav className="bottom-nav">
      <button className={`bottom-nav-item ${isHome ? 'active' : ''}`} onClick={() => navigate('/')}>
        <span className="bottom-nav-icon">💊</span>
        <span>처방받기</span>
      </button>
      <button className={`bottom-nav-item ${isSaved ? 'active' : ''}`} onClick={() => navigate('/saved')}>
        <span className="bottom-nav-icon">♥</span>
        <span>약장</span>
      </button>
      <button className={`bottom-nav-item ${isProfile ? 'active' : ''}`} onClick={() => navigate('/profile')}>
        <span className="bottom-nav-icon">🎵</span>
        <span>나의 기록</span>
      </button>
    </nav>
  )
}
