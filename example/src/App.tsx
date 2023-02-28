import * as React from 'react';

import { Alert, Button, StyleSheet, View } from 'react-native';
import { openIntent } from 'react-native-sibs-intent';

export default function App() {
  return (
    <View style={styles.container}>
      <Button
        title="Open App"
        onPress={() =>
          openIntent('abc', '1', 'pt.sibs.android.mpos.sibsPagamentosQly')
            .then((value) => console.log(value))
            .catch((err) => Alert.alert(`${err}`))
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
