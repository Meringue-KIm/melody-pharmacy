import { useEffect, useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { getConcepts } from '../api/songApi'
import type { Concept, Situation } from '../api/songApi'
import '../styles/Main.css'

export default function ConceptPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const situation = location.state?.situation as Situation
  const [concepts, setConcepts] = useState<Concept[]>([])

  useEffect(() => {
    if (!situation) { navigate('/'); return }
    getConcepts().then((res: { data: Concept[] }) => setConcepts(res.data))
  }, [])

  const handleSelect = (concept: Concept) => {
    navigate('/recommend', { state: { situation, concept } })
  }

  return (
    <div className="main-container">
      <header className="main-header">
        <button className="back-btn" onClick={() => navigate('/')}>← 뒤로</button>
        <div className="logo">🎵 멜로디약국</div>
        <div />
      </header>

      <main className="main-content">
        <h1 className="main-title">
          {situation?.icon} {situation?.name}<br />
          <span>지금 어떤 느낌의 노래가 필요한가요?</span>
        </h1>

        <div className="concept-grid">
          {concepts.map((c) => (
            <button key={c.id} className="concept-card" onClick={() => handleSelect(c)}>
              <span className="concept-icon">{c.icon}</span>
              <span className="concept-name">{c.name}</span>
            </button>
          ))}
        </div>
      </main>
    </div>
  )
}
