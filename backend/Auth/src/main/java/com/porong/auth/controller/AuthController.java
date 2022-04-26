package com.porong.auth.controller;

import com.porong.auth.domain.Member;
import com.porong.auth.dto.LoginResultInfo;
import com.porong.auth.dto.SignUpInfo;
import com.porong.auth.dto.TokenInfo;
import com.porong.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
@CrossOrigin("*")
public class AuthController {

    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";

    @Autowired
    private AuthService authService;

    /**
     * 카카오 로그인 테스트를 위한 홈 화면
     * @return
     */
    @GetMapping("/")
    public String oauthTest(){
        return "index";
    }

    /**
     * 로그인 리다이렉트 후 code 받아와서 accessToken 발급, 발급 받은 토큰으로 사용자 조회
     * @param code
     * @return
     * @throws IOException
     */
    @GetMapping("/login/response")
    public ResponseEntity<Map<String, Object>> loginResponse(@RequestParam String code) throws IOException {

        // code 잘 받아오나 테스트
        // System.out.println("controller get code : " + code);
        // token 잘 받아오나 테스트
        // System.out.println("controller get token : " + authService.getAccessToken(code).getMember().getAccessToken());

        Map<String, Object> result = new HashMap<>();
        LoginResultInfo loginResultInfo = null;

        if(code == null){
            result.put("result",FAIL);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        else { // code != null : 로그인에 성공했다면 토큰을 발급 받아 해당 사용자 조회
            loginResultInfo = authService.getAccessToken(code);
            if (loginResultInfo == null){
                result.put("result",FAIL);
                return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            }
            System.out.println(loginResultInfo.toString());
        }
        result.put("result", SUCCESS);
        result.put("kakaoId", loginResultInfo.getMember().getKakaoId());
        result.put("memberId", loginResultInfo.getMember().getMemberId());
        result.put("AccessToken", loginResultInfo.getMember().getAccessToken());
        result.put("first_check", loginResultInfo.isFirstCheck());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 최초 로그인 한 사용자 정보 저장 (진짜 회원 가입은 아님)
     * @param signUpInfo
     * @return
     */
    @PostMapping("/signup")
    public HttpStatus signUp(@RequestBody SignUpInfo signUpInfo){

        System.out.println("start signUp Controller : " + signUpInfo.toString());
        HttpStatus status = HttpStatus.ACCEPTED; // 202 : 요청 접수 및 처리 중

        // kakaoId 를 이용한 회원 조회가 되는지 여부 판단
        boolean result = authService.signUp(signUpInfo);
        if (result) {
            System.out.println("정상적으로 signup 이 완료되어 DB에 저장되었습니다");
            status = HttpStatus.OK; // 200 : 요청 정상 처리
        }
        else {
            status = HttpStatus.BAD_REQUEST; // 400 : 요청 부적절
        }

        return status;
    }

    /** 
     * 최조 로그인 한 사용자 인지 아닌지 확인
     * @param tokenInfo
     * @return
     */
    @PostMapping("/access/check")
    public ResponseEntity<Map<String, Object>> accessTokenCheck (@RequestBody TokenInfo tokenInfo){

        System.out.println("start accessTokenCheck Controller : " + tokenInfo.toString());
        HttpStatus status = HttpStatus.ACCEPTED;

        Map<String, Object> result = new HashMap<>();
        Member member = null;

        try {
            member = authService.getUserInfo(tokenInfo.getAccessToken());
            if (authService.checkExist(member.getKakaoId())){
                System.out.println("DB에 저장된 사용자 입니다");
                result.put("result", SUCCESS);
                result.put("kakaoId", member.getKakaoId());
            }
            else {
                result.put("result", FAIL);
            }
        }
        catch (IOException e){
            result.put("result", "EXCEPTION");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
