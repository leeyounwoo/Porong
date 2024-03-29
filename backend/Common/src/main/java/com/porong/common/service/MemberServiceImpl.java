package com.porong.common.service;

import com.porong.common.domain.Follow;
import com.porong.common.domain.Member;
import com.porong.common.dto.*;
import com.porong.common.repository.FollowRepository;
import com.porong.common.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository MEMBER_REPOSITORY;
    private final FollowRepository FOLLOW_REPOSITORY;
    private final DefaultMessageService messageService = NurigoApp.INSTANCE.initialize("NCSOD4MYUWGPQGGT", "CFK0T3ASXGJBC9MFTW9EP1FO0IKNNNWT", "https://api.coolsms.co.kr");
    private final ConcurrentHashMap<Long, String> authenticationNumber = new ConcurrentHashMap<>();

    @Override
    public void signup(SignupDto signupDto) throws Exception {
        if(!MEMBER_REPOSITORY.existsByPhoneNumber(signupDto.getPhoneNumber())) {
            try {
                Member member = new Member();
                member.setKakaoId(signupDto.getKakaoId());
                member.setName(signupDto.getName());
                member.setEmail(signupDto.getEmail());
                member.setPhoneNumber(signupDto.getPhoneNumber());
                member.setProfileUrl(signupDto.getProfileUrl());

                MEMBER_REPOSITORY.save(member);
            }
            catch (Exception e) {
                throw e;
            }
        }
    }

    @Override
    public void authenticate(AuthenticateDto authenticateDto) throws Exception {
        try {
            Random randPackage = new Random();
            String randomNum = "";

            for(int i=0; i<6; i++) {
                int random = randPackage.nextInt(10);
                randomNum += String.valueOf(random);
            }

            Message message = new Message();
            message.setFrom("01041084206");
            message.setTo(authenticateDto.getPhoneNumber());
            message.setText("마음 사서함 인증번호는 " + randomNum + " 입니다.");

            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
            System.out.println(response);

            if(authenticationNumber.containsKey(authenticateDto.getKakaoId())) authenticationNumber.remove(authenticateDto.getKakaoId());
            authenticationNumber.put(authenticateDto.getKakaoId(), randomNum);
        }
        catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void verify(VerifyDto verifyDto) throws Exception {
        Exception e = new Exception();
        if(authenticationNumber.get(verifyDto.getKakaoId()).equals(verifyDto.getNumber())) return;
        else throw e;
    }

    @Override
    public void follow(FollowDto followDto) throws Exception {
        Member fromMember = MEMBER_REPOSITORY.findByMemberId(followDto.getFromMemberId());
        Member toMember = MEMBER_REPOSITORY.findByMemberId(followDto.getFromMemberId());

        Follow newFollow = new Follow();
        newFollow.setFollowing(fromMember);
        newFollow.setFollowing(toMember);

        FOLLOW_REPOSITORY.save(newFollow);
    }

    @Override
    public List<PhoneBookDto> fetchContact(List<String> phoneList) throws Exception {
        List<PhoneBookDto> phoneBookList = new ArrayList<>();

        for(int i=0; i<phoneList.size(); i++) {
            PhoneBookDto tempBookDto = new PhoneBookDto();

            String phoneNumber = phoneList.get(i);
            boolean isFind = MEMBER_REPOSITORY.existsByPhoneNumber(phoneNumber);

            if(isFind) { // 가입된 번호일 경우
                Member signUpEdMember = MEMBER_REPOSITORY.findByPhoneNumber(phoneNumber);
                tempBookDto.setSignup(true);
                tempBookDto.setMemberId(signUpEdMember.getMemberId());
                tempBookDto.setName(signUpEdMember.getName());
                tempBookDto.setPhoneNumber(signUpEdMember.getPhoneNumber());
                tempBookDto.setProfileUrl(signUpEdMember.getProfileUrl());
                tempBookDto.setEmail(signUpEdMember.getEmail());
                phoneBookList.add(tempBookDto);
            }
        }
        return phoneBookList;
    }

    @Override
    public void updateFCMToken(UpdateFCMTokenDto updateFCMTokenDto) throws Exception {
        if(!MEMBER_REPOSITORY.existsByMemberId(updateFCMTokenDto.getMemberId())) throw new Exception();

        Member member = MEMBER_REPOSITORY.getById(updateFCMTokenDto.getMemberId());
        member.setFcmToken(updateFCMTokenDto.getFcmToken());

        MEMBER_REPOSITORY.save(member);
    }

    @Override
    public void recommend(String phoneNumber) throws Exception {
        Message message = new Message();
        message.setFrom("01041084206");
        message.setTo(phoneNumber);
        message.setText("마음 사서함을 다운 받아 주세요.");

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response);
    }

    @Override
    public InquireMemberDto inquire(long memberId) throws Exception {
        if(!MEMBER_REPOSITORY.existsByMemberId(memberId)) throw new Exception();

        Member member = MEMBER_REPOSITORY.findByMemberId(memberId);
        InquireMemberDto inquireMemberDto = new InquireMemberDto();

        inquireMemberDto.setName(member.getName());
        inquireMemberDto.setProfileUrl(member.getProfileUrl());

        return inquireMemberDto;
    }
}
