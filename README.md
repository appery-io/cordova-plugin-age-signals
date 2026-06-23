# cordova-plugin-age-signals

Cordova plugin for requesting age signals from the platform:

- Android: [Google Play Age Signals API](https://developer.android.com/google/play/age-signals/overview)
- iOS: [DeclaredAgeRange](https://developer.apple.com/documentation/declaredagerange)

The plugin is based on the Capawesome Capacitor Age Signals plugin API and exposes the native implementation through the global `AgeSignals` object.

## Supported Platforms

- Android
- iOS

## Requirements

- Cordova `>= 9.0.0`
- cordova-android `>= 9.0.0`
- cordova-ios `>= 6.0.0`
- Android min SDK `24`

## Installation

From a local Cordova project:

```bash
cordova plugin add ../cordova-plugin-age-signals
```

If the plugin was already installed and you changed plugin source files, reinstall it or recreate the platform so Cordova copies the updated native files:

```bash
cordova plugin rm cordova-plugin-age-signals
cordova plugin add ../cordova-plugin-age-signals
```

or:

```bash
cordova platform rm ios
cordova platform add ios
```

Use the same approach for Android if native source files were changed after plugin installation.

## Android Configuration

The plugin adds the Google Play Age Signals dependency:

```xml
<framework src="com.google.android.play:age-signals:$ANDROID_PLAY_AGE_SIGNALS_VERSION" />
```

Default version:

```xml
ANDROID_PLAY_AGE_SIGNALS_VERSION=0.0.3
```

You can override it when installing the plugin:

```bash
cordova plugin add ../cordova-plugin-age-signals --variable ANDROID_PLAY_AGE_SIGNALS_VERSION=0.0.3
```

The plugin also sets:

```xml
<preference name="android-minSdkVersion" value="24" />
```

## iOS Configuration

The plugin automatically adds the Declared Age Range entitlement to the generated iOS entitlements file:

```xml
<key>com.apple.developer.declared-age-range</key>
<true/>
```

You normally do not need to add this key manually to the Cordova app project.

However, the Apple provisioning profile used to sign the app must also allow this entitlement. If the app binary contains `com.apple.developer.declared-age-range`, but the provisioning profile does not, signing or App Store validation can fail.

Recommended steps:

1. Open Apple Developer portal.
2. Go to Certificates, Identifiers & Profiles.
3. Open the App ID / Identifier for the application.
4. Enable the Declared Age Range / age assurance capability if available.
5. Regenerate and download the Development and Distribution provisioning profiles.
6. Update the profiles used by Xcode, CI, Jenkins, or App Store builds.

Some Apple entitlements may require Apple account permissions or an Apple approval flow. If the capability is not visible in the Developer portal, check Apple's current DeclaredAgeRange documentation and account eligibility.

### Xcode / SDK Note

The iOS implementation is compatible with Xcode 26.3. It intentionally does not reference `AgeRangeService.AgeRangeDeclaration.confirmed` directly because that symbol is not present in the iOS 26.3 SDK. Verification methods such as `governmentIDChecked` and `paymentChecked` are mapped to the plugin's own `CONFIRMED` value.

## JavaScript Usage

The plugin clobbers the global object:

```js
window.AgeSignals
```

Basic example:

```js
async function checkAgeSignals() {
  const result = await AgeSignals.checkAgeSignals();

  console.log('User status:', result.userStatus);
  console.log('Age lower:', result.ageLower);
  console.log('Age upper:', result.ageUpper);
  console.log('Age range declaration:', result.ageRangeDeclaration);
}
```

iOS eligibility check:

```js
async function checkEligibility() {
  const result = await AgeSignals.checkEligibility();
  console.log('Is eligible:', result.isEligible);
}
```

`checkEligibility()` is only available on iOS.

## API

### checkAgeSignals(options?)

Requests the user's age signals.

```ts
checkAgeSignals(options?: CheckAgeSignalsOptions): Promise<CheckAgeSignalsResult>
```

Options:

```ts
interface CheckAgeSignalsOptions {
  ageGates?: number[];
}
```

`ageGates` is only used on iOS. It must contain 2 or 3 ages. The default is:

```js
[13, 15, 18]
```

Result:

```ts
interface CheckAgeSignalsResult {
  userStatus: UserStatus;
  ageLower?: number;
  ageUpper?: number;
  mostRecentApprovalDate?: string;
  installId?: string;
  ageRangeDeclaration?: AgeRangeDeclaration;
}
```

### checkEligibility()

Checks whether the user is eligible for age-related platform obligations.

```ts
checkEligibility(): Promise<CheckEligibilityResult>
```

Only available on iOS.

Result:

```ts
interface CheckEligibilityResult {
  isEligible: boolean;
}
```

### setUseFakeManager(options)

Enables or disables the Android fake manager for testing.

```ts
setUseFakeManager(options: { useFake: boolean }): Promise<void>
```

Only available on Android.

### setNextAgeSignalsResult(options)

Sets the next age signals result returned by the Android fake manager.

```ts
setNextAgeSignalsResult(options: SetNextAgeSignalsResultOptions): Promise<void>
```

Only available on Android.

### setNextAgeSignalsException(options)

Sets the next exception returned by the Android fake manager.

```ts
setNextAgeSignalsException(options: SetNextAgeSignalsExceptionOptions): Promise<void>
```

Only available on Android.

## Enums

### UserStatus

```ts
enum UserStatus {
  Verified = 'VERIFIED',
  Supervised = 'SUPERVISED',
  SupervisedApprovalPending = 'SUPERVISED_APPROVAL_PENDING',
  SupervisedApprovalDenied = 'SUPERVISED_APPROVAL_DENIED',
  Unknown = 'UNKNOWN',
  Declared = 'DECLARED',
  Empty = 'EMPTY'
}
```

Meaning:

- `VERIFIED`: The user is verified as an adult.
- `SUPERVISED`: The user has a supervised account.
- `SUPERVISED_APPROVAL_PENDING`: Parent or guardian approval is pending.
- `SUPERVISED_APPROVAL_DENIED`: Parent or guardian approval was denied.
- `UNKNOWN`: The user's age status is unknown.
- `DECLARED`: The user declared an age status. Android only.
- `EMPTY`: No age signal is available or the user is outside applicable regions.

### AgeRangeDeclaration

```ts
enum AgeRangeDeclaration {
  SelfDeclared = 'SELF_DECLARED',
  GuardianDeclared = 'GUARDIAN_DECLARED',
  Confirmed = 'CONFIRMED'
}
```

Only available on iOS when the platform provides age range declaration details.

### ErrorCode

```ts
enum ErrorCode {
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
```

## Android Testing With Fake Responses

The Android implementation supports `FakeAgeSignalsManager`.

Example: simulate a parent approval denied response:

```js
await AgeSignals.setUseFakeManager({ useFake: true });

await AgeSignals.setNextAgeSignalsResult({
  userStatus: 'SUPERVISED_APPROVAL_DENIED',
  ageLower: 13,
  ageUpper: 17,
  mostRecentApprovalDate: '2025-02-01',
  installId: 'fake_install_id'
});

const result = await AgeSignals.checkAgeSignals();
console.log(result.userStatus);
```

Example: simulate pending approval:

```js
await AgeSignals.setUseFakeManager({ useFake: true });

await AgeSignals.setNextAgeSignalsResult({
  userStatus: 'SUPERVISED_APPROVAL_PENDING',
  ageLower: 13,
  ageUpper: 17,
  mostRecentApprovalDate: '2025-02-01',
  installId: 'fake_install_id'
});

const result = await AgeSignals.checkAgeSignals();
console.log(result.userStatus);
```

Disable fake manager:

```js
await AgeSignals.setUseFakeManager({ useFake: false });
```

The fake manager methods are Android-only. On iOS they return an error because the platform does not provide this testing API.

## Example: Blocking App Access

Example application-level logic:

```js
const blockingMessages = {
  SUPERVISED_APPROVAL_PENDING:
    'Access to this app is waiting for approval from your parent or guardian. Please ask them to approve it and then try again.',
  SUPERVISED_APPROVAL_DENIED:
    'Your parent or guardian has not allowed you to use this app. Please contact them if you think this is a mistake.'
};

async function enforceAgeSignals() {
  const result = await AgeSignals.checkAgeSignals();
  const message = blockingMessages[result.userStatus];

  if (!message) {
    return false;
  }

  // Show a non-dismissible modal or alert here.
  console.log(message);
  return true;
}
```

## Platform Availability Summary

| Method | Android | iOS |
| --- | --- | --- |
| `checkAgeSignals()` | Yes | Yes |
| `checkEligibility()` | No | Yes |
| `setUseFakeManager()` | Yes | No |
| `setNextAgeSignalsResult()` | Yes | No |
| `setNextAgeSignalsException()` | Yes | No |

## Troubleshooting

### `window.AgeSignals` is undefined

Make sure the plugin is installed and the platform was prepared:

```bash
cordova plugin add ../cordova-plugin-age-signals
cordova prepare
```

If the plugin was added after the platform already existed, reinstall the plugin or recreate the platform.

### iOS signing fails because of `com.apple.developer.declared-age-range`

The entitlement exists in the app, but the provisioning profile does not allow it. Enable the capability for the App ID in Apple Developer portal and regenerate the provisioning profile.

### iOS build fails with `.confirmed` missing

Build with the current plugin source. The iOS implementation must not reference `AgeRangeService.AgeRangeDeclaration.confirmed` directly when compiling with Xcode 26.3.

If the error appears inside `platforms/ios/.../Plugins/cordova-plugin-age-signals`, reinstall the plugin or recreate the iOS platform so Cordova copies the updated source.

### Android fake manager does not return the expected response

Make sure fake mode is enabled before setting the next result:

```js
await AgeSignals.setUseFakeManager({ useFake: true });
await AgeSignals.setNextAgeSignalsResult({ userStatus: 'SUPERVISED_APPROVAL_DENIED' });
```

Then call:

```js
await AgeSignals.checkAgeSignals();
```

## License

MIT
