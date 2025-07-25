package com.bootcampbox.server.exception;

/**
 * 검색어 유효성 검사 실패 시 발생하는 예외
 */
public class InvalidSearchKeywordException extends RuntimeException {
    
    public InvalidSearchKeywordException(String message) {
        super(message);
    }
    
    public InvalidSearchKeywordException(String message, Throwable cause) {
        super(message, cause);
    }
} 