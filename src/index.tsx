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

export interface Data {
  status: string;
  erroCode: string;
}

/**
 * It opens the Sibs Intent with the given parameters
 * @param {string} packageId - The package id of your app. You can find it in your AndroidManifest.xml
 * file.
 * @param {string} className - The name of the class that will be opened when the user clicks on the
 * notification.
 * @param {string} ammount - The amount of money you want to send. The price is in cents. 1000 = $10.00.
 * @param {string} reference - The reference of the payment.
 */

export async function startActivityWithIntentMessage(
  packageId: string,
  className: string,
  ammount: string,
  reference: string
): Promise<void> {
  if (!packageId) throw new Error('Invalid package id');
  try {
    await SibsIntent.startActivityWithIntentMessage(
      packageId,
      className,
      ammount,
      reference
    );
  } catch (_e) {
    const e = _e;
    console.log(e);
  }
}
