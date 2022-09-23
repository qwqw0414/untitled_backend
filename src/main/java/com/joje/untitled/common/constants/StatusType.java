package com.joje.untitled.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusType {

//    요청 성공
    SUCCESS("C2000", "정상적으로 처리 완료"),

//    서비스 에러
    HTTP_REQUEST_FAILED("C3090", "HTTP 요청 실패"),
    NOT_FOUND_DATA("C3010", "일치하는 데이터가 없음"),
    FAILED_SIGNON("C3100", "로그인 실패"),

    //    HTTP 에러
    BAD_REQUEST("C4000", "잘못된 요청"),
    UNAUTHORIZED("C4010", "사용자가 승인되지 않음"),
    INVALID_TOKEN("C4011", "유효하지 않은 토큰"),
    EXPIRED_TOKEN("C4012", "만료된 토큰"),
    EXPIRED_ACCESS_TOKEN("C4013", "만료된 액세스 토큰"),
    EXPIRED_REFRESH_TOKEN("C4014", "만료된 리프레시 토큰"),
    FORBIDDEN("C4030", "해당 컨텐츠에 접근할 권한이 없음"),
    METHOD_NOT_SUPPORTED("C4050", "올바르지 않은 메소드 형식"),

//    기타 에러
    SERVER_ERROR("C5000", "서버 에러");

    private final String code;
    private final String message;

}
