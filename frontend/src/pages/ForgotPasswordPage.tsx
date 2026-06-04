import { Link } from 'react-router-dom'
import mascotLab from '../assets/mascot-lab.png'

export default function ForgotPasswordPage() {
  return (
    <div className="frame" data-screen="forgot">
      <div className="auth-box" style={{ textAlign: 'center' }}>
        <div className="auth-mascot">
          <img src={mascotLab} alt="멜로디약국 약사" style={{ width: 120, height: 120, objectFit: 'contain' }} />
        </div>
        <div className="auth-header">
          <div className="brand brand-lg" style={{ justifyContent: 'center' }}>
            <span>멜로디약국</span>
          </div>
          <p className="auth-sub">비밀번호 재설정</p>
        </div>

        <div style={{
          background: 'var(--surface-2)',
          border: 'var(--border-width) var(--border-style) var(--line)',
          borderRadius: 'var(--r)',
          padding: '20px 24px',
          marginBottom: 24,
          textAlign: 'left'
        }}>
          <p style={{ fontSize: 14, color: 'var(--ink)', marginBottom: 8, fontWeight: 600 }}>
            🔧 이메일 인증 서비스 준비 중이에요
          </p>
          <p style={{ fontSize: 13, color: 'var(--ink-soft)', lineHeight: 1.6, margin: 0 }}>
            비밀번호를 잊으셨다면 아래 이메일로 문의해주세요.<br />
            확인 후 빠르게 도와드릴게요.
          </p>
          <a
            href="mailto:kimsung3879@gmail.com"
            style={{
              display: 'inline-block',
              marginTop: 12,
              fontSize: 13,
              color: 'var(--accent)',
              textDecoration: 'none',
              borderBottom: '1px solid var(--accent)'
            }}
          >
            kimsung3879@gmail.com
          </a>
        </div>

        <Link to="/login" className="btn btn-block" style={{ display: 'flex', justifyContent: 'center' }}>
          로그인으로 돌아가기
        </Link>
      </div>
    </div>
  )
}
