package com.porong.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ResponseReceiveMessageDto implements Serializable {

    private Long messageId;
    private Long senderId; // 받는 사람
    private Long senderProfileUrl;
    private double latitude; // 제약 위치 조건
    private double longitude; // 제약 위치 조건
    private LocalDateTime dueTime; // 제약 시간
    private boolean isChecked; // 상대의 확인 여부 추가
    private LocalDateTime createdAt;
    private String title;

    public ResponseReceiveMessageDto() {

    }

}
