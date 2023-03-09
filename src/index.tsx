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

export async function openIntent(
  packageId: string,
  ammount: string,
  reference: string
): Promise<void> {
  if (!packageId) throw new Error('Invalid package id');
  try {
    await SibsIntent.openIntent(packageId, ammount, reference);
  } catch (_e) {
    const e = _e as string;
    throw new Error(e);
  }
}
