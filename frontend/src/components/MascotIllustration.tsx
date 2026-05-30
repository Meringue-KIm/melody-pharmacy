import { useTheme } from '../context/ThemeContext'

interface Props {
  src: string
  size?: number
  alt?: string
}

const DARK_STYLES: Record<string, {
  background: string
  boxShadow: string
  filter: string
  padding: number
}> = {
  notebook: {
    background: 'radial-gradient(circle, rgba(142,111,224,0.22) 0%, rgba(142,111,224,0.06) 65%, transparent 100%)',
    boxShadow: '0 0 32px rgba(142,111,224,0.60), inset 0 1px 1px rgba(255,255,255,0.08)',
    filter: 'drop-shadow(0 0 7px rgba(197,176,240,0.95)) drop-shadow(0 0 20px rgba(142,111,224,0.65)) brightness(1.12) saturate(1.18)',
    padding: 18,
  },
  receipt: {
    background: 'radial-gradient(circle, rgba(232,121,74,0.20) 0%, transparent 70%)',
    boxShadow: '0 0 28px rgba(232,121,74,0.55), inset 0 1px 1px rgba(255,220,180,0.10)',
    filter: 'drop-shadow(0 0 7px rgba(245,181,145,0.90)) drop-shadow(0 0 18px rgba(232,121,74,0.60)) brightness(1.10) sepia(0.14)',
    padding: 18,
  },
  cassette: {
    background: 'radial-gradient(ellipse 80% 50% at 50% 100%, rgba(62,132,201,0.28) 0%, transparent 70%)',
    boxShadow: '0 14px 32px rgba(62,132,201,0.45), 0 0 0 1px rgba(156,208,245,0.18)',
    filter: 'drop-shadow(0 7px 12px rgba(62,132,201,0.70)) drop-shadow(0 0 22px rgba(156,208,245,0.40)) brightness(1.08)',
    padding: 18,
  },
  sticker: {
    background: 'radial-gradient(circle, rgba(181,139,232,0.18) 0%, rgba(181,139,232,0.05) 60%, transparent 100%)',
    boxShadow: 'inset 0 2px 12px rgba(255,255,255,0.09), inset 0 -2px 10px rgba(181,139,232,0.18), 0 0 28px rgba(181,139,232,0.48), 0 0 0 1.5px rgba(224,205,246,0.32)',
    filter: 'drop-shadow(0 0 6px rgba(224,205,246,0.90)) drop-shadow(0 0 18px rgba(181,139,232,0.55)) brightness(1.12) saturate(1.12)',
    padding: 20,
  },
}

export default function MascotIllustration({ src, size = 140, alt = '멜로디약국 약사' }: Props) {
  const { dark, theme } = useTheme()
  const ds = dark ? DARK_STYLES[theme] ?? DARK_STYLES.notebook : null

  return (
    <div style={{
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
      borderRadius: '50%',
      transition: 'background 0.3s, box-shadow 0.3s',
      ...(ds ? {
        padding: ds.padding,
        background: ds.background,
        boxShadow: ds.boxShadow,
      } : {}),
    }}>
      <img
        src={src}
        alt={alt}
        style={{
          width: size,
          height: size,
          objectFit: 'contain',
          transition: 'filter 0.3s',
          ...(ds ? { filter: ds.filter } : {}),
        }}
      />
    </div>
  )
}
