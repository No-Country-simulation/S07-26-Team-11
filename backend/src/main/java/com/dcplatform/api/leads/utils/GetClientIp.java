package com.dcplatform.api.leads.utils;

import jakarta.servlet.http.HttpServletRequest;

public class GetClientIp {

    public String getClienwtIp(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null || xfHeader.isEmpty()) {
        return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0].trim();
}
    
}
