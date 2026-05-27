import api from './axios'

export interface SignupRequest {
  email: string
  password: string
  nickname: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface TokenResponse {
  accessToken: string
  nickname: string
}

export const signup = (data: SignupRequest) =>
  api.post('/api/auth/signup', data)

export const login = (data: LoginRequest) =>
  api.post<TokenResponse>('/api/auth/login', data)

export const kakaoLogin = (code: string) =>
  api.get<TokenResponse>('/api/auth/kakao', { params: { code } })
