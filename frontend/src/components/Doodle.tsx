const PATHS: Record<string, React.ReactNode> = {
  subway: (
    <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M16 14 Q14 14 14 18 V44 Q14 48 18 48 H46 Q50 48 50 44 V18 Q50 14 46 14 Z" />
      <line x1="14" y1="32" x2="50" y2="32" />
      <circle cx="22" cy="22" r="1.5" fill="currentColor" />
      <circle cx="42" cy="22" r="1.5" fill="currentColor" />
      <line x1="22" y1="40" x2="22" y2="42" /><line x1="42" y1="40" x2="42" y2="42" />
      <path d="M18 50 L14 56" /><path d="M46 50 L50 56" /><path d="M28 56 L36 56" />
    </g>
  ),
  dumbbell: (
    <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
      <line x1="22" y1="32" x2="42" y2="32" />
      <rect x="12" y="24" width="8" height="16" rx="2" /><rect x="44" y="24" width="8" height="16" rx="2" />
      <path d="M8 28 V36" /><path d="M56 28 V36" />
      <path d="M48 16 L52 14" /><path d="M52 18 L56 16" />
    </g>
  ),
  book: (
    <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M12 16 Q14 14 18 14 H32 V48 H18 Q14 48 12 50 Z" />
      <path d="M52 16 Q50 14 46 14 H32 V48 H46 Q50 48 52 50 Z" />
      <path d="M18 22 H28" /><path d="M18 28 H26" />
      <path d="M36 22 H46" /><path d="M36 28 H44" />
    </g>
  ),
  umbrella: (
    <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M10 30 Q14 16 32 14 Q50 16 54 30 Z" />
      <path d="M20 30 Q22 24 32 22 Q42 24 44 30" />
      <line x1="32" y1="30" x2="32" y2="50" /><path d="M32 50 Q30 54 26 53" />
      <path d="M18 38 L16 42" /><path d="M46 38 L48 42" />
      <path d="M22 46 L20 50" /><path d="M44 46 L46 50" />
    </g>
  ),
  moon: (
    <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M40 14 Q26 14 22 28 Q22 44 38 48 Q24 44 24 30 Q26 18 40 14 Z" />
      <text x="44" y="22" fontFamily="serif" fontSize="11" fill="currentColor" stroke="none">z</text>
      <text x="50" y="16" fontFamily="serif" fontSize="8" fill="currentColor" stroke="none">z</text>
      <circle cx="14" cy="22" r="1" fill="currentColor" />
      <circle cx="50" cy="38" r="1" fill="currentColor" />
    </g>
  ),
  car: (
    <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M8 36 L12 26 Q14 22 18 22 H46 Q50 22 52 26 L56 36 V42 H50 V46 H44 V42 H20 V46 H14 V42 H8 Z" />
      <path d="M16 28 H30 V36 H14 Z" /><path d="M34 28 H48 L50 36 H34 Z" />
      <circle cx="18" cy="42" r="3" fill="currentColor" />
      <circle cx="46" cy="42" r="3" fill="currentColor" />
    </g>
  ),
  coffee: (
    <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M16 24 H44 V42 Q44 50 32 50 Q20 50 20 42 Z" />
      <path d="M44 28 Q52 28 52 34 Q52 40 44 40" />
      <path d="M24 14 Q22 18 24 22" /><path d="M30 12 Q28 16 30 20" /><path d="M36 14 Q34 18 36 22" />
    </g>
  ),
  broom: (
    <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
      <line x1="14" y1="50" x2="42" y2="22" />
      <path d="M40 18 L48 26 L40 36 L30 32 Z" />
      <path d="M40 30 L36 36" /><path d="M44 32 L40 38" /><path d="M48 28 L46 34" />
      <path d="M14 50 L12 54" />
      <circle cx="52" cy="14" r="1.5" fill="currentColor" />
      <path d="M18 14 L20 16 M22 14 L20 16" />
    </g>
  ),
  wave: (
    <g fill="none" stroke="currentColor" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round">
      <path d="M8 24 Q16 16 24 24 T40 24 T56 24" />
      <path d="M8 36 Q16 28 24 36 T40 36 T56 36" />
      <path d="M14 46 Q22 40 30 46 T46 46" />
    </g>
  ),
  sparkle: (
    <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M32 12 L34 28 L50 32 L34 36 L32 52 L30 36 L14 32 L30 28 Z" />
      <path d="M48 16 L50 20 L54 22 L50 24 L48 28" />
      <path d="M14 44 L16 46 L20 48" />
    </g>
  ),
  rain: (
    <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M14 26 Q14 18 22 18 Q24 12 32 14 Q42 12 44 22 Q52 22 52 30 Q52 36 44 36 H20 Q14 36 14 30 Z" />
      <path d="M22 42 L18 50" /><path d="M32 42 L28 52" /><path d="M42 42 L38 50" />
    </g>
  ),
  hug: (
    <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M32 50 Q14 40 14 26 Q14 16 24 16 Q28 16 32 22 Q36 16 40 16 Q50 16 50 26 Q50 40 32 50 Z" />
      <path d="M20 28 Q22 24 26 26" /><path d="M38 26 Q42 24 44 28" />
    </g>
  ),
  cassette: (
    <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
      <rect x="8" y="18" width="48" height="32" rx="4" />
      <circle cx="22" cy="32" r="6" /><circle cx="42" cy="32" r="6" />
      <circle cx="22" cy="32" r="1.5" fill="currentColor" />
      <circle cx="42" cy="32" r="1.5" fill="currentColor" />
      <line x1="14" y1="44" x2="50" y2="44" />
      <line x1="14" y1="24" x2="20" y2="24" /><line x1="44" y1="24" x2="50" y2="24" />
    </g>
  ),
  sprout: (
    <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M32 52 V32" />
      <path d="M32 38 Q22 36 18 28 Q28 30 32 36" />
      <path d="M32 32 Q40 24 50 22 Q46 32 32 36" />
      <path d="M24 52 L26 50 M40 52 L38 50" />
      <path d="M14 18 L16 20 M18 16 L16 20" />
    </g>
  ),
}

interface Props {
  name: string
  size?: number
  color?: string
  style?: React.CSSProperties
}

export default function Doodle({ name, size = 56, color, style = {} }: Props) {
  const content = PATHS[name]
  if (!content) return <span style={{ fontSize: size * 0.7, lineHeight: 1 }}>?</span>
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 64 64"
      style={{ color: color || 'currentColor', display: 'inline-block', verticalAlign: 'middle', flexShrink: 0, ...style }}
    >
      {content}
    </svg>
  )
}
