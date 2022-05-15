import React, {useRef, useState} from 'react';
import {StyleSheet, View, Text, Image} from 'react-native';

export default function TimeSatisfactionAlert({
  senderNickname,
  place,
  isChecked,
}) {
  return (
    <View style={styles.alarmcompletion}>
      <View style={styles.alarmcontainer}>
        <View style={styles.profilebox}>
          <Image
            source={{uri: 'https://reactjs.org/logo-og.png'}}
            style={styles.profileimage}
          />
        </View>
        <View style={styles.textbox}>
          <Text style={styles.text}>
            <Text>지금부터 </Text>
            <Text style={styles.textbold}>{`${senderNickname}`}</Text>
            <Text> 님이 보낸 메세지를 </Text>
            <Text style={styles.textbold}>{`[${place}]`}</Text>
            <Text> 에서 확인할 수 있습니다.</Text>
          </Text>
        </View>
        {!isChecked && <View style={styles.circle}></View>}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  alarmcompletion: {
    paddingLeft: 8,
    paddingRight: 8,
  },
  alarmcontainer: {
    paddingLeft: 8,
    paddingRight: 20,
    flexDirection: 'row',
    flexWrap: 'nowrap',
    justifyContent: 'space-evenly',
    alignItems: 'center',
  },
  profilebox: {
    width: 70,
    backgroundColor: 'blue',
    height: 70,
    borderRadius: 70,
    marginLeft: 12,
    overflow: 'hidden',
  },
  profileimage: {
    width: '100%',
    height: '100%',
    'object-fit': 'cover',
  },
  textbox: {
    width: '80%',
    padding: 20,
    marginHorizontal: 20,
  },
  text: {
    whiteSpace: 'nowrap',
  },
  textbold: {
    fontWeight: 'bold',
    color: '#FF9292',
  },
  circle: {
    width: 10,
    height: 10,
    borderRadius: 50,
    backgroundColor: '#FF9292',
    alignItems: 'center',
  },
});