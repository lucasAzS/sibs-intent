import * as React from 'react';

import {
  Keyboard,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import {
  Data,
  intentEventEmitter,
  startActivityWithIntentMessage,
} from 'react-native-sibs-intent';

const EVENT_NAME = 'onIntentResponse';

export default function App() {
  const [value, setValue] = React.useState('1');
  const [reference, setReference] = React.useState('asd');
  const [result, setResult] = React.useState<Data>();

  const handleData = async (data: Data) => {
    try {
      setResult(data);
    } catch (error) {
      console.error('Error while getting JSON from native module:');
    }
  };

  const sendIntent = async () => {
    await startActivityWithIntentMessage(
      'pt.sibs.android.mpos.sibsPagamentosQly',
      'pt.sibs.android.mpos.activities.MainActivity',
      value,
      reference
    );
    Keyboard.dismiss();
    setValue('');
    setReference('');
  };

  React.useEffect(() => {
    intentEventEmitter.addListener(EVENT_NAME, handleData);

    return () => {
      intentEventEmitter.removeAllListeners(EVENT_NAME);
    };
  }, []);

  React.useEffect(() => {
    console.log(result);
  }, [result]);

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <View style={styles.box}>
        <TextInput
          keyboardType="numeric"
          style={styles.input}
          placeholder="Enter a value"
          value={value}
          onChangeText={setValue}
        />
        <TextInput
          style={styles.input}
          placeholder="Enter a reference"
          value={reference}
          onChangeText={setReference}
          autoCapitalize="none"
        />
      </View>
      <TouchableOpacity style={styles.btn} onPress={sendIntent}>
        <Text style={styles.btnText}>Press Here</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    marginBottom: 8,
  },
  input: {
    height: 40,
    width: 250,
    borderColor: 'gray',
    borderWidth: 1,
    borderRadius: 8,
    marginVertical: 10,
    paddingHorizontal: 10,
    marginBottom: 10,
    textAlign: 'center',
  },
  btn: {
    width: 200,
    height: 40,
    backgroundColor: 'royalblue',
    borderRadius: 8,
    justifyContent: 'center',
    alignItems: 'center',
  },
  btnText: {
    color: 'white',
    fontWeight: 'bold',
  },
});
