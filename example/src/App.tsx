import * as React from 'react';

import { Button, StyleSheet, View } from 'react-native';
import { openIntent } from 'react-native-sibs-intent';

export default function App() {
  return (
    <View style={styles.container}>
      <Button
        title="Open App"
        onPress={() =>
          openIntent(
            '123456789',
            '1000',
            'pt.sibs.android.mpos.sibsPagamentosQly'
          )
            .then((value) => console.log(value))
            .catch((err) => console.log('err', err))
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
