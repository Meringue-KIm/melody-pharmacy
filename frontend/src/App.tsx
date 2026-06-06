import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom'
import { ThemeProvider } from './context/ThemeContext'
import BottomNav from './components/BottomNav'
import LoginPage from './pages/LoginPage'
import SignupPage from './pages/SignupPage'
import ForgotPasswordPage from './pages/ForgotPasswordPage'
import MainPage from './pages/MainPage'
import ConceptPage from './pages/ConceptPage'
import RecommendPage from './pages/RecommendPage'
import SavedPage from './pages/SavedPage'
import ProfilePage from './pages/ProfilePage'
import KakaoCallbackPage from './pages/KakaoCallbackPage'
import AdminPage from './pages/AdminPage'
import { isGuest } from './utils/guestMode'

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const token = localStorage.getItem('token')
  return (token || isGuest()) ? <>{children}</> : <Navigate to="/login" replace />
}

function PublicOnlyRoute({ children }: { children: React.ReactNode }) {
  const token = localStorage.getItem('token')
  return token ? <Navigate to="/" replace /> : <>{children}</>
}

const NO_NAV = ['/login', '/signup', '/forgot-password', '/oauth/kakao', '/admin']

function AppLayout({ children }: { children: React.ReactNode }) {
  const { pathname } = useLocation()
  const showNav = !NO_NAV.some(p => pathname.startsWith(p))
  return (
    <>
      {children}
      {showNav && <BottomNav />}
    </>
  )
}

export default function App() {
  return (
    <ThemeProvider>
      <div className="app">
        <BrowserRouter>
          <AppLayout>
            <Routes>
              <Route path="/login" element={<PublicOnlyRoute><LoginPage /></PublicOnlyRoute>} />
              <Route path="/signup" element={<PublicOnlyRoute><SignupPage /></PublicOnlyRoute>} />
              <Route path="/forgot-password" element={<ForgotPasswordPage />} />
              <Route path="/" element={<PrivateRoute><MainPage /></PrivateRoute>} />
              <Route path="/concept" element={<PrivateRoute><ConceptPage /></PrivateRoute>} />
              <Route path="/recommend" element={<PrivateRoute><RecommendPage /></PrivateRoute>} />
              <Route path="/saved" element={<PrivateRoute><SavedPage /></PrivateRoute>} />
              <Route path="/profile" element={<PrivateRoute><ProfilePage /></PrivateRoute>} />
              <Route path="/oauth/kakao" element={<KakaoCallbackPage />} />
              <Route path="/admin" element={<AdminPage />} />
            </Routes>
          </AppLayout>
        </BrowserRouter>
      </div>
    </ThemeProvider>
  )
}
