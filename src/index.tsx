import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-sibs-intent' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const SibsIntent = NativeModules.SibsIntent
  ? NativeModules.SibsIntent
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export async function multiply(a: number, b: number): Promise<number> {
  return SibsIntent.multiply(a, b);
}
export async function openIntent(
  packageId: string,
  amount: string,
  reference: string
): Promise<void> {
  if (!packageId || typeof packageId !== 'string')
    throw new Error('Invalid package id');

  try {
    await SibsIntent.openIntent(packageId, amount, reference);
  } catch (_e) {
    const e = _e as string;
    throw new Error(e);
  }
}

export async function createPendingIntent(
  packageId: string,
  amount: string,
  reference: string
): Promise<void> {
  if (!packageId || typeof packageId !== 'string')
    throw new Error('Invalid package id');

  try {
    return SibsIntent.createPendingIntent(reference, amount);
  } catch (_e) {
    const e = _e as string;
    throw new Error(e);
  }
}
