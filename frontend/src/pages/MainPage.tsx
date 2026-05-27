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
  const [last, setLast] = useState<LastSelection | null>(null)

  useEffect(() => {
    const saved = localStorage.getItem('lastSelection')
    if (saved) setLast(JSON.parse(saved))
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
    if (!window.confirm('로그아웃 하시겠어요?')) return
    localStorage.removeItem('token')
    localStorage.removeItem('nickname')
    localStorage.removeItem('lastSelection')
    navigate('/login')
  }

  return (
    <div className="main-container">
      <header className="main-header">
        <div className="logo">🎵 멜로디약국</div>
        <div className="header-right">
          <span className="nickname">{nickname}님</span>
          <button className="logout-btn" onClick={handleLogout}>로그아웃</button>
        </div>
      </header>

      <main className="main-content">
        {last && (
          <div className="last-selection">
            <p className="last-label">지난번 선택</p>
            <button
              className="last-btn"
              onClick={() => navigate(`/recommend?situationId=${last.situationId}&conceptId=${last.conceptId}`)}
            >
              <span className="last-btn-text">{last.situationIcon} {last.situationName} · {last.conceptIcon} {last.conceptName}</span>
              <span className="last-arrow">→</span>
            </button>
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
            ? Array.from({ length: 4 }).map((_, i) => (
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
