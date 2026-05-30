import { createContext, useContext, useEffect, useState } from 'react'

export type ThemeName = 'notebook' | 'receipt' | 'cassette' | 'sticker'

interface ThemeCtx {
  theme: ThemeName
  dark: boolean
  setTheme: (t: ThemeName) => void
  setDark: (d: boolean) => void
}

const Ctx = createContext<ThemeCtx>({
  theme: 'notebook', dark: false,
  setTheme: () => {}, setDark: () => {},
})

export function ThemeProvider({ children }: { children: React.ReactNode }) {
  const [theme, setThemeState] = useState<ThemeName>(() =>
    (localStorage.getItem('melody-theme') as ThemeName) || 'notebook'
  )
  const [dark, setDarkState] = useState(() =>
    localStorage.getItem('melody-dark') === 'true'
  )

  useEffect(() => {
    const html = document.documentElement
    if (theme === 'notebook') {
      html.removeAttribute('data-theme')
    } else {
      html.setAttribute('data-theme', theme)
    }
    html.setAttribute('data-dark', String(dark))
  }, [theme, dark])

  const setTheme = (t: ThemeName) => {
    setThemeState(t)
    localStorage.setItem('melody-theme', t)
  }
  const setDark = (d: boolean) => {
    setDarkState(d)
    localStorage.setItem('melody-dark', String(d))
  }

  return <Ctx.Provider value={{ theme, dark, setTheme, setDark }}>{children}</Ctx.Provider>
}

export const useTheme = () => useContext(Ctx)
