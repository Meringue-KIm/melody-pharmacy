import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTheme, type ThemeName } from '../context/ThemeContext'
import MascotHead from './MascotHead'

interface Props {
  leftSlot?: React.ReactNode
}

const THEMES: { key: ThemeName; color: string; label: string }[] = [
  { key: 'notebook', color: '#8E6FE0', label: '노트' },
  { key: 'receipt',  color: '#E8794A', label: '영수증' },
  { key: 'cassette', color: '#3E84C9', label: '카세트' },
  { key: 'sticker',  color: '#B58BE8', label: '스티커' },
]

export default function AppHeader({ leftSlot }: Props) {
  const navigate = useNavigate()
  const { theme, dark, setTheme, setDark } = useTheme()
  const [open, setOpen] = useState(false)

  const current = THEMES.find(t => t.key === theme) ?? THEMES[0]

  return (
    <div style={{ position: 'relative' }}>
      <header className="appheader">
        {leftSlot || (
          <div className="brand" onClick={() => navigate('/')} style={{ cursor: 'pointer' }}>
            <MascotHead size={40} />
            <span>멜로디약국</span>
          </div>
        )}
        <button
          className="iconbtn"
          onClick={() => setOpen(p => !p)}
          style={{ padding: '0 10px', gap: 6 }}
          title="테마 변경"
        >
          <span style={{
            width: 12, height: 12, borderRadius: '50%',
            background: current.color, display: 'inline-block', flexShrink: 0
          }} />
          {dark ? '🌙' : '☀'}
        </button>
      </header>

      {open && (
        <>
          <div
            style={{ position: 'fixed', inset: 0, zIndex: 199 }}
            onClick={() => setOpen(false)}
          />
          <div style={{
            position: 'absolute', top: '100%', right: 0, zIndex: 200,
            background: 'var(--surface)', border: 'var(--border-width) var(--border-style) var(--line)',
            borderRadius: 'var(--r)', boxShadow: 'var(--shadow-card)',
            padding: '12px 14px', minWidth: 180, display: 'flex', flexDirection: 'column', gap: 10
          }}>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4,1fr)', gap: 8 }}>
              {THEMES.map(t => (
                <button
                  key={t.key}
                  onClick={() => { setTheme(t.key); setOpen(false) }}
                  style={{
                    display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4,
                    padding: '8px 4px', borderRadius: 'var(--r-sm)', cursor: 'pointer',
                    border: theme === t.key ? '2px solid var(--accent)' : '2px solid var(--line)',
                    background: 'var(--surface-2)'
                  }}
                >
                  <span style={{ width: 24, height: 24, borderRadius: '50%', background: t.color, display: 'block' }} />
                  <span style={{ fontSize: 10, fontFamily: 'var(--font-mono)', color: 'var(--ink-soft)' }}>{t.label}</span>
                </button>
              ))}
            </div>
            <button
              className={`toggle ${dark ? 'on' : ''}`}
              style={{ width: 'fit-content' }}
              onClick={() => setDark(!dark)}
            >
              <span className="toggle-track"><span className="toggle-thumb" /></span>
              다크 모드
            </button>
          </div>
        </>
      )}
    </div>
  )
}
