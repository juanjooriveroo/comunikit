/**
 * Decoded JWT Token payload
 */
export interface TokenPayload {
  sub: string;
  name: string;
  email: string;
  role: UserRole;
  iat: number;
  exp: number;
}

/**
 * User interface basado en el payload del token JWT
 */
export interface User {
  id: string;
  email: string;
  name: string;
  role: UserRole;
}

export enum UserRole {
  USER = 'USER',
  TUTOR = 'TUTOR',
  ADMIN = 'ADMIN'
}

/**
 * Requests y Responses para autenticación
 */
export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  rol: string;
  language: string;
}

export interface RegisterResponse {
  request: boolean;
}

export interface CreateUserRequest {
  name: string;
  language: string;
}

export interface CreateUserResponse {
  user: User;
  generatedPassword: string;
  board: {
    id: number;
    name: string;
  };
}
