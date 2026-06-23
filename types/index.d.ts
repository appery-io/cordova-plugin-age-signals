export interface CheckAgeSignalsOptions {
  ageGates?: number[];
}

export interface CheckAgeSignalsResult {
  userStatus: UserStatus;
  ageLower?: number;
  ageUpper?: number;
  mostRecentApprovalDate?: string;
  installId?: string;
  ageRangeDeclaration?: AgeRangeDeclaration;
}

export interface CheckEligibilityResult {
  isEligible: boolean;
}

export interface SetUseFakeManagerOptions {
  useFake: boolean;
}

export interface SetNextAgeSignalsResultOptions {
  userStatus: UserStatus;
  ageLower?: number;
  ageUpper?: number;
  mostRecentApprovalDate?: string;
  installId?: string;
}

export interface SetNextAgeSignalsExceptionOptions {
  errorCode: ErrorCode;
}

export enum UserStatus {
  Verified = 'VERIFIED',
  Supervised = 'SUPERVISED',
  SupervisedApprovalPending = 'SUPERVISED_APPROVAL_PENDING',
  SupervisedApprovalDenied = 'SUPERVISED_APPROVAL_DENIED',
  Unknown = 'UNKNOWN',
  Declared = 'DECLARED',
  Empty = 'EMPTY'
}

export enum AgeRangeDeclaration {
  SelfDeclared = 'SELF_DECLARED',
  GuardianDeclared = 'GUARDIAN_DECLARED',
  Confirmed = 'CONFIRMED'
}

export enum ErrorCode {
  ApiNotAvailable = 'API_NOT_AVAILABLE',
  PlayStoreNotFound = 'PLAY_STORE_NOT_FOUND',
  NetworkError = 'NETWORK_ERROR',
  PlayServicesNotFound = 'PLAY_SERVICES_NOT_FOUND',
  CannotBindToService = 'CANNOT_BIND_TO_SERVICE',
  PlayStoreVersionOutdated = 'PLAY_STORE_VERSION_OUTDATED',
  PlayServicesVersionOutdated = 'PLAY_SERVICES_VERSION_OUTDATED',
  ClientTransientError = 'CLIENT_TRANSIENT_ERROR',
  AppNotOwned = 'APP_NOT_OWNED',
  InternalError = 'INTERNAL_ERROR',
  SdkVersionOutdated = 'SDK_VERSION_OUTDATED'
}

export interface AgeSignalsPlugin {
  checkAgeSignals(options?: CheckAgeSignalsOptions): Promise<CheckAgeSignalsResult>;
  checkEligibility(): Promise<CheckEligibilityResult>;
  setUseFakeManager(options: SetUseFakeManagerOptions): Promise<void>;
  setNextAgeSignalsResult(options: SetNextAgeSignalsResultOptions): Promise<void>;
  setNextAgeSignalsException(options: SetNextAgeSignalsExceptionOptions): Promise<void>;
}

declare const AgeSignals: AgeSignalsPlugin;
export default AgeSignals;
