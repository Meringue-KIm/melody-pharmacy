import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { getConcepts, getSituations } from '../api/songApi'
import type { Concept, Situation } from '../api/songApi'
import '../styles/Main.css'

export default function ConceptPage() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const situationId = Number(searchParams.get('situationId'))

  const [situation, setSituation] = useState<Situation | null>(null)
  const [concepts, setConcepts] = useState<Concept[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)

  useEffect(() => {
    if (!situationId) { navigate('/'); return }
    load()
  }, [situationId])

  const load = () => {
    setLoading(true)
    setError(false)
    Promise.all([getSituations(), getConcepts()])
      .then(([sitRes, conRes]) => {
        const found = sitRes.data.find(s => s.id === situationId)
        if (!found) { navigate('/'); return }
        setSituation(found)
        setConcepts(conRes.data)
      })
      .catch(() => setError(true))
      .finally(() => setLoading(false))
  }

  const handleSelect = (concept: Concept) => {
    if (situation) {
      localStorage.setItem('lastSelection', JSON.stringify({
        situationId: situation.id, situationIcon: situation.icon, situationName: situation.name,
        conceptId: concept.id,     conceptIcon: concept.icon,     conceptName: concept.name,
      }))
    }
    navigate(`/recommend?situationId=${situationId}&conceptId=${concept.id}`)
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
          {situation ? `${situation.icon} ${situation.name}` : ' '}<br />
          <span>지금 어떤 느낌의 노래가 필요한가요?</span>
        </h1>

        {error && (
          <div className="page-error">
            불러오기 실패했어요 😢<br />
            <button onClick={load}>다시 시도</button>
          </div>
        )}

        <div className="concept-grid">
          {loading
            ? Array.from({ length: 6 }).map((_, i) => (
                <div key={i} className="skeleton skeleton-concept" />
              ))
            : concepts.map(c => (
                <button key={c.id} className="concept-card" onClick={() => handleSelect(c)}>
                  <span className="concept-icon">{c.icon}</span>
                  <span className="concept-name">{c.name}</span>
                </button>
              ))
          }
        </div>
      </main>
    </div>
  )
}
