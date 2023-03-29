# React Native SIBS Intent (Alpha)

`react-native-sibs-intent` is an alpha-stage React Native library that simplifies
communication between your React Native application and the MPOS SIBS app on Android.
This library provides a native module, `SibsIntentModule`, which handles the
communication between your React Native JavaScript code and the Android native side
when launching the MPOS SIBS app with custom parameters.

## Please be aware that this module is currently in its alpha stage and will be provided as-is for the time being.

## Installation

```sh
npm install react-native-sibs-intent
```

## Usage

First, import the `startActivityWithIntentMessage` function and `intentEventEmitter` from the library;

```js
import {
  startActivityWithIntentMessage,
  intentEventEmitter,
} from 'react-native-sibs-intent';

// ...

async function openExternalActivity() {
  const packageId = 'com.example.package';
  const className = 'com.example.package.ExternalActivity';
  const amount = '100.0';
  const reference = 'transaction123';

  try {
    await startActivityWithIntentMessage(
      packageId,
      className,
      amount,
      reference
    );
    console.log('Activity launched');
  } catch (error) {
    console.log('Error launching activity:', error);
  }
}
```

To receive the activity result, add an event listener for the onActivityResult event using intentEventEmitter:

```js
function handleActivityResult(event) {
  console.log('Received activity result:', event);
}


// Add the event listener when the component mounts
useEffect(() => {
  intentEventEmitter.addListener('onIntentResponse', handleActivityResult);

  // Remove the event listener when the component unmounts

  return intentEventEmitter.removeAllListeners(EVENT_NAME);

}), [])


```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
