interface Props { size?: number; style?: React.CSSProperties }

export default function MascotHead({ size = 64, style = {} }: Props) {
  return (
    <svg width={size} height={size} viewBox="0 0 80 80"
      style={{ display: 'inline-block', verticalAlign: 'middle', flexShrink: 0, ...style }}
      aria-label="멜로디약국 약사">
      {/* headphone band */}
      <path d="M14 38 Q14 13 40 12 Q66 13 66 38"
            fill="none" stroke="var(--mascot-band, #2D2218)" strokeWidth="3.4" strokeLinecap="round"/>
      {/* face */}
      <ellipse cx="40" cy="44" rx="20" ry="19"
               fill="var(--mascot-face, #FAF3E2)"
               stroke="var(--mascot-outline, #2D2218)" strokeWidth="2.4"/>
      {/* blush */}
      <ellipse cx="28" cy="50" rx="3" ry="2" fill="#E89A85" opacity="0.5"/>
      <ellipse cx="52" cy="50" rx="3" ry="2" fill="#E89A85" opacity="0.5"/>
      {/* eyes */}
      <ellipse cx="32" cy="44" rx="1.8" ry="2.6" fill="var(--mascot-outline, #2D2218)"/>
      <ellipse cx="48" cy="44" rx="1.8" ry="2.6" fill="var(--mascot-outline, #2D2218)"/>
      {/* eye gleams */}
      <circle cx="32.7" cy="43.2" r="0.6" fill="var(--mascot-face, #FAF3E2)"/>
      <circle cx="48.7" cy="43.2" r="0.6" fill="var(--mascot-face, #FAF3E2)"/>
      {/* smile */}
      <path d="M35 53 Q40 55.5 45 53" fill="none"
            stroke="var(--mascot-outline, #2D2218)" strokeWidth="2"
            strokeLinecap="round" strokeLinejoin="round"/>
      {/* headphone cushions */}
      <rect x="10" y="36" width="13" height="20" rx="6"
            fill="var(--mascot-phone, #2D2218)"/>
      <rect x="57" y="36" width="13" height="20" rx="6"
            fill="var(--mascot-phone, #2D2218)"/>
      {/* cushion inner */}
      <rect x="13" y="40" width="6" height="12" rx="3"
            fill="var(--mascot-phone-inner, #5A4A35)" opacity="0.7"/>
      <rect x="60" y="40" width="6" height="12" rx="3"
            fill="var(--mascot-phone-inner, #5A4A35)" opacity="0.7"/>
      {/* shadow */}
      <ellipse cx="40" cy="68" rx="14" ry="1.4"
               fill="var(--mascot-outline, #2D2218)" opacity="0.10"/>
    </svg>
  )
}
