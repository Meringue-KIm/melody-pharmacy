import { useEffect, useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { getSaved } from '../api/songApi'

export default function BottomNav() {
  const navigate = useNavigate()
  const { pathname } = useLocation()
  const [savedCount, setSavedCount] = useState(0)

  const isHome    = pathname === '/' || pathname.startsWith('/concept') || pathname.startsWith('/recommend')
  const isSaved   = pathname === '/saved'
  const isProfile = pathname === '/profile'

  useEffect(() => {
    getSaved().then(r => setSavedCount(r.data.length)).catch(() => {})
  }, [])

  useEffect(() => {
    const handler = (e: Event) => setSavedCount((e as CustomEvent<number>).detail)
    window.addEventListener('savedCountChanged', handler)
    return () => window.removeEventListener('savedCountChanged', handler)
  }, [])

  return (
    <nav className="bottom-nav">
      <button className={`bottom-nav-item ${isHome ? 'active' : ''}`} onClick={() => navigate('/')}>
        <span className="bottom-nav-icon">💊</span>
        <span>처방받기</span>
      </button>
      <button className={`bottom-nav-item ${isSaved ? 'active' : ''}`} onClick={() => navigate('/saved')}>
        <span className="bottom-nav-icon" style={{ position: 'relative', display: 'inline-block' }}>
          ♥
          {savedCount > 0 && (
            <span style={{
              position: 'absolute', top: -4, right: -8,
              background: 'var(--accent)', color: 'white',
              borderRadius: 99, fontSize: 10, fontWeight: 700,
              minWidth: 16, height: 16, lineHeight: '16px',
              textAlign: 'center', padding: '0 3px'
            }}>{savedCount > 99 ? '99+' : savedCount}</span>
          )}
        </span>
        <span>약장</span>
      </button>
      <button className={`bottom-nav-item ${isProfile ? 'active' : ''}`} onClick={() => navigate('/profile')}>
        <span className="bottom-nav-icon">🎵</span>
        <span>나의 기록</span>
      </button>
    </nav>
  )
}
