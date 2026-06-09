import { useEffect, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { getConcepts, getSituations, getComboCounts } from '../api/songApi'
import { guestGetConcepts, guestGetSituations, guestGetComboCounts } from '../api/guestApi'
import { isGuest } from '../utils/guestMode'
import type { Concept, Situation } from '../api/songApi'
import AppHeader from '../components/AppHeader'
import Doodle from '../components/Doodle'

export default function ConceptPage() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const situationId = Number(searchParams.get('situationId'))

  const [situation, setSituation] = useState<Situation | null>(null)
  const [concepts, setConcepts] = useState<Concept[]>([])
  const [comboCounts, setComboCounts] = useState<Record<number, number>>({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)
  const isAdmin = localStorage.getItem('nickname') === '관리자'

  useEffect(() => {
    if (!situationId) { navigate('/'); return }
    load()
  }, [situationId])

  const load = () => {
    setLoading(true)
    setError(false)
    Promise.all(isGuest()
      ? [guestGetSituations(), guestGetConcepts(), guestGetComboCounts(situationId)]
      : [getSituations(), getConcepts(), getComboCounts(situationId)]
    )
      .then(([sitRes, conRes, countRes]) => {
        const found = sitRes.data.find(s => s.id === situationId)
        if (!found) { navigate('/'); return }
        setSituation(found)
        setConcepts(conRes.data)
        setComboCounts(countRes.data)
      })
      .catch(() => setError(true))
      .finally(() => setLoading(false))
  }

  const handleSelect = (concept: Concept) => {
    if (situation) {
      const entry = {
        situationId: situation.id, situationIcon: situation.icon, situationName: situation.name,
        conceptId: concept.id,     conceptIcon: concept.icon,     conceptName: concept.name,
      }
      const prev = JSON.parse(localStorage.getItem('lastSelections') || '[]')
      const deduped = [entry, ...prev.filter((s: typeof entry) =>
        !(s.situationId === entry.situationId && s.conceptId === entry.conceptId)
      )]
      localStorage.setItem('lastSelections', JSON.stringify(deduped.slice(0, 3)))
    }
    navigate(`/recommend?situationId=${situationId}&conceptId=${concept.id}`)
  }

  return (
    <div className="frame" data-screen="concept">
      <AppHeader
        leftSlot={
          <button className="iconbtn" onClick={() => navigate('/')}>← 상황 바꾸기</button>
        }
      />

      <section className="hero">
        <p className="eyebrow">STEP 2 · 느낌 선택</p>
        {situation && (
          <p className="hero-tag">
            <Doodle name={situation.icon} size={28} style={{ marginRight: 8, verticalAlign: 'middle' }} />
            {situation.name}
          </p>
        )}
        <h1 className="h1">지금 어떤 느낌이 좋을까요?</h1>
        <p className="subtitle">선택한 느낌에 맞춰 처방전을 만들어드릴게요.</p>
      </section>

      {error && (
        <div className="page-error">
          불러오기 실패했어요<br />
          <button onClick={load}>다시 시도</button>
        </div>
      )}

      <section className="section">
        <div className="grid grid-3">
          {loading
            ? Array.from({ length: 9 }).map((_, i) => (
                <div key={i} className="skel" style={{ height: 120, borderRadius: 'var(--r)' }} />
              ))
            : concepts.map(c => {
                const count = comboCounts[c.id]
                const empty = count !== undefined && count === 0
                return (
                  <button
                    key={c.id}
                    className="tile"
                    disabled={empty}
                    onClick={() => handleSelect(c)}
                  >
                    <Doodle name={c.icon} size={52} />
                    <span className="tile-name">{c.name}</span>
                    {empty && (
                      <span style={{ fontSize: 11, color: 'var(--muted)', marginTop: 2 }}>준비 중</span>
                    )}
                    {isAdmin && count !== undefined && !empty && (
                      <span style={{ fontSize: 11, color: 'var(--muted)', marginTop: 2 }}>{count}곡</span>
                    )}
                  </button>
                )
              })
          }
        </div>
      </section>
    </div>
  )
}
