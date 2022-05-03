import React from 'react';
import {View, Text, TouchableOpacity, Image} from 'react-native';
import KakaoSDK from '@actbase/react-kakaosdk';

export default function Login({navigation}) {
  const Login = async () => {
    try {
      await KakaoSDK.init('066f28139628e8b5440363889440f7be');
      const tokens = await KakaoSDK.login();
      console.log(tokens);
      const profile = await KakaoSDK.getProfile();
      console.log(profile);
      alert('회원가입을 위해 새로운 페이지로 이동합니다.');
      navigation.navigate('signin', {properties: profile.properties});
    } catch (e) {
      console.log(e);
    }
  };

  return (
    <View
      style={{
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
      }}>
      <TouchableOpacity onPress={Login}>
        <Image source={require('../assets/images/kakao_login.png')} />
      </TouchableOpacity>
    </View>
  );
}
