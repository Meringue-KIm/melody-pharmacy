import { Link } from 'react-router-dom'
import mascotLab from '../assets/mascot-lab.png'

export default function ForgotPasswordPage() {
  return (
    <div className="frame" data-screen="forgot">
      <div className="auth-box">
        <div className="auth-mascot">
          <img src={mascotLab} alt="멜로디약국 약사" style={{ width: 120, height: 120, objectFit: 'contain' }} />
        </div>
        <div className="auth-header">
          <div className="brand brand-lg" style={{ justifyContent: 'center' }}>
            <span>멜로디약국</span>
          </div>
          <p className="auth-sub">비밀번호 재설정</p>
        </div>

        <div style={{ textAlign: 'center' }}>
          <p style={{ fontSize: 15, color: 'var(--ink-soft)', marginBottom: 10, lineHeight: 1.7 }}>
            현재 이메일 인증 서비스를 준비 중이에요.
          </p>
          <p style={{ fontSize: 13, color: 'var(--muted)', marginBottom: 24, lineHeight: 1.6 }}>
            카카오 로그인을 이용하거나<br />새 계정으로 가입해보세요.
          </p>
          <Link to="/login" className="btn btn-block" style={{ display: 'flex', justifyContent: 'center' }}>
            로그인으로 돌아가기
          </Link>
        </div>

        <p className="auth-link" style={{ marginTop: 18 }}>
          <Link to="/signup">회원가입하기</Link>
        </p>
      </div>
    </div>
  )
}
