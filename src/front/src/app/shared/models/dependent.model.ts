/**
 * Modelos para cuentas dependientes
 */

export interface Language {
  code: string;
  name: string;
}

export interface DependentAccount {
  id: string;
  name: string;
  username: string;
  language: Language | null;
  storage_used: number | null;
}

export interface DependentAccountListItem {
  id: string;
  name: string;
  username: string;
}

export interface GetAllDependentsResponse {
  accounts: DependentAccount[];
}

export interface EditProfileRequest {
  name: string;
  email?: string;
  language: string;
  userId?: string;
}

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
  userId?: string;
}

export interface DeleteAccountRequest {
  password: string;
  userId?: string;
}
