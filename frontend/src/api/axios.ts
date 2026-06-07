import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8081',
})

api.interceptors.request.use((config: import('axios').InternalAxiosRequestConfig) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const wasLoggedIn = !!localStorage.getItem('token')
      localStorage.removeItem('token')
      localStorage.removeItem('nickname')
      localStorage.removeItem('lastSelection')
      localStorage.removeItem('lastSelections')
      localStorage.removeItem('provider')
      if (wasLoggedIn && !window.location.pathname.startsWith('/login')) {
        sessionStorage.setItem('sessionExpired', '1')
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default api
