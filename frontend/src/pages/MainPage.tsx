import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getSituations } from '../api/songApi'
import type { Situation } from '../api/songApi'
import '../styles/Main.css'

interface LastSelection {
  situationId: number; situationIcon: string; situationName: string
  conceptId: number;   conceptIcon: string;   conceptName: string
}

export default function MainPage() {
  const navigate = useNavigate()
  const nickname = localStorage.getItem('nickname') || '환자'
  const [situations, setSituations] = useState<Situation[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)
  const [lastSelections, setLastSelections] = useState<LastSelection[]>([])
  const [showLogoutModal, setShowLogoutModal] = useState(false)

  useEffect(() => {
    const saved = localStorage.getItem('lastSelections')
    if (saved) {
      setLastSelections(JSON.parse(saved))
    } else {
      const old = localStorage.getItem('lastSelection')
      if (old) setLastSelections([JSON.parse(old)])
    }
    load()
  }, [])

  const load = () => {
    setLoading(true)
    setError(false)
    getSituations()
      .then(res => setSituations(res.data))
      .catch(() => setError(true))
      .finally(() => setLoading(false))
  }

  const handleSelect = (situation: Situation) => {
    navigate(`/concept?situationId=${situation.id}`)
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('nickname')
    localStorage.removeItem('lastSelections')
    localStorage.removeItem('lastSelection')
    localStorage.removeItem('provider')
    navigate('/login')
  }

  return (
    <div className="main-container">
      {showLogoutModal && (
        <div className="modal-overlay" onClick={() => setShowLogoutModal(false)}>
          <div className="modal-box" onClick={e => e.stopPropagation()}>
            <p className="modal-title">로그아웃</p>
            <p className="modal-desc">정말 로그아웃 하시겠어요?</p>
            <div className="modal-actions">
              <button className="modal-cancel" onClick={() => setShowLogoutModal(false)}>취소</button>
              <button className="modal-confirm" onClick={handleLogout}>로그아웃</button>
            </div>
          </div>
        </div>
      )}

      <header className="main-header">
        <div className="logo">🎵 멜로디약국</div>
        <div className="header-right">
          <button className="saved-link-btn" onClick={() => navigate('/saved')}>♥ 저장소</button>
          <button className="nickname-btn" onClick={() => navigate('/profile')}>{nickname}님</button>
          <button className="logout-btn" onClick={() => setShowLogoutModal(true)}>로그아웃</button>
        </div>
      </header>

      <main className="main-content">
        {lastSelections.length > 0 && (
          <div className="last-selection">
            <p className="last-label">지난번 선택</p>
            <div className="last-chips-row">
              {lastSelections.map((last, i) => (
                <button
                  key={i}
                  className="last-chip"
                  onClick={() => navigate(`/recommend?situationId=${last.situationId}&conceptId=${last.conceptId}`)}
                >
                  {last.situationIcon} {last.situationName} · {last.conceptIcon} {last.conceptName}
                </button>
              ))}
            </div>
          </div>
        )}

        <h1 className="main-title">
          오늘 어떤 상황이신가요?<br />
          <span>상황을 선택하면 딱 맞는 노래를 처방해드려요 💊</span>
        </h1>

        {error && (
          <div className="page-error">
            네트워크 오류가 발생했어요 😢<br />
            <button onClick={load}>다시 시도</button>
          </div>
        )}

        <div className="situation-grid">
          {loading
            ? Array.from({ length: 6 }).map((_, i) => (
                <div key={i} className="skeleton skeleton-card" />
              ))
            : situations.map(s => (
                <button key={s.id} className="situation-card" onClick={() => handleSelect(s)}>
                  <span className="situation-icon">{s.icon}</span>
                  <span className="situation-name">{s.name}</span>
                </button>
              ))
          }
        </div>
      </main>
    </div>
  )
}
