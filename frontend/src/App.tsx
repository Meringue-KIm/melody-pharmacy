import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import SignupPage from './pages/SignupPage'
import MainPage from './pages/MainPage'
import ConceptPage from './pages/ConceptPage'
import RecommendPage from './pages/RecommendPage'
import KakaoCallbackPage from './pages/KakaoCallbackPage'

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const token = localStorage.getItem('token')
  return token ? <>{children}</> : <Navigate to="/login" replace />
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/" element={<PrivateRoute><MainPage /></PrivateRoute>} />
        <Route path="/concept" element={<PrivateRoute><ConceptPage /></PrivateRoute>} />
        <Route path="/recommend" element={<PrivateRoute><RecommendPage /></PrivateRoute>} />
        <Route path="/oauth/kakao" element={<KakaoCallbackPage />} />
      </Routes>
    </BrowserRouter>
  )
}
