package com.bootcampbox.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AdminSecurityConfig {
    
    @Value("${local.admin.security.enabled:false}")
    private boolean localAdminSecurityEnabled;
    
    @Value("${prod.admin.security.enabled:true}")
    private boolean prodAdminSecurityEnabled;
    
    public boolean isAdminSecurityEnabled() {
        // 현재 활성화된 프로필에 따라 보안 설정 반환
        String activeProfile = System.getProperty("spring.profiles.active", "local");
        
        if ("prod".equals(activeProfile)) {
            return prodAdminSecurityEnabled;
        } else {
            return localAdminSecurityEnabled;
        }
    }
} 