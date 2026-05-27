import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getSituations } from '../api/songApi'
import type { Situation } from '../api/songApi'
import '../styles/Main.css'

const LAST_KEY = 'lastSelection'

interface LastSelection {
  situationId: number
  situationIcon: string
  situationName: string
  conceptId: number
  conceptIcon: string
  conceptName: string
}

export default function MainPage() {
  const navigate = useNavigate()
  const nickname = localStorage.getItem('nickname') || '환자'
  const [situations, setSituations] = useState<Situation[]>([])
  const [last, setLast] = useState<LastSelection | null>(null)

  useEffect(() => {
    getSituations().then(res => setSituations(res.data))
    const saved = localStorage.getItem(LAST_KEY)
    if (saved) setLast(JSON.parse(saved))
  }, [])

  const handleSelect = (situation: Situation) => {
    navigate(`/concept?situationId=${situation.id}`)
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('nickname')
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
              {last.situationIcon} {last.situationName} &nbsp;·&nbsp; {last.conceptIcon} {last.conceptName}
              <span className="last-arrow">→</span>
            </button>
          </div>
        )}

        <h1 className="main-title">
          오늘 어떤 상황이신가요?<br />
          <span>상황을 선택하면 딱 맞는 노래를 처방해드려요 💊</span>
        </h1>

        <div className="situation-grid">
          {situations.map((s) => (
            <button key={s.id} className="situation-card" onClick={() => handleSelect(s)}>
              <span className="situation-icon">{s.icon}</span>
              <span className="situation-name">{s.name}</span>
            </button>
          ))}
        </div>
      </main>
    </div>
  )
}
