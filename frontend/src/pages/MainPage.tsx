import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getSituations } from '../api/songApi'
import type { Situation } from '../api/songApi'
import AppHeader from '../components/AppHeader'
import Doodle from '../components/Doodle'

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

  const today = new Date().toISOString().slice(2, 10).replace(/-/g, '.')
  const rxNo = String(Date.now()).slice(-6)

  useEffect(() => {
    const saved = localStorage.getItem('lastSelections')
    if (saved) {
      try { setLastSelections(JSON.parse(saved)) } catch {}
    } else {
      const old = localStorage.getItem('lastSelection')
      if (old) { try { setLastSelections([JSON.parse(old)]) } catch {} }
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

  return (
    <div className="frame" data-screen="main">
      <AppHeader />

      <div className="bag-strip">
        <span>Rx · {today}</span>
        <span className="bag-strip-mid">
          <span className="brand-mini">멜로디약국</span>
        </span>
        <span>{nickname}님</span>
      </div>

      {lastSelections.length > 0 && (
        <section className="last-row">
          <p className="eyebrow last-label">지난 처방전</p>
          <div className="chip-row">
            {lastSelections.map((last, i) => (
              <button
                key={i}
                className="chip chip-last"
                onClick={() => navigate(`/recommend?situationId=${last.situationId}&conceptId=${last.conceptId}`)}
              >
                <Doodle name={last.situationIcon} size={16} />
                <span>{last.situationName}</span>
                <span className="dot">·</span>
                <Doodle name={last.conceptIcon} size={16} />
                <span>{last.conceptName}</span>
              </button>
            ))}
          </div>
        </section>
      )}

      <section className="hero">
        <p className="eyebrow">TODAY'S Rx · 오늘의 처방전</p>
        <h1 className="h1">오늘 어떤 상황이신가요?</h1>
        <p className="subtitle">상황을 골라주시면 어울리는 노래를 처방해드릴게요.</p>
      </section>

      {error && (
        <div className="page-error">
          네트워크 오류가 발생했어요<br />
          <button onClick={load}>다시 시도</button>
        </div>
      )}

      <section className="section">
        <div className="section-head">
          <h2 className="h2">상황</h2>
          <span className="rx-stamp">No. {rxNo}</span>
        </div>
        <div className="grid grid-3 grid-4">
          {loading
            ? Array.from({ length: 7 }).map((_, i) => (
                <div key={i} className="skel" style={{ height: 120, borderRadius: 'var(--r)' }} />
              ))
            : situations.map(s => (
                <button
                  key={s.id}
                  className="tile"
                  onClick={() => navigate(`/concept?situationId=${s.id}`)}
                >
                  <Doodle name={s.icon} size={52} />
                  <span className="tile-name">{s.name}</span>
                </button>
              ))
          }
        </div>
      </section>
    </div>
  )
}
