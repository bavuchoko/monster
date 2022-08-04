package com.example.monster.common.authenticatior.filter;

import com.example.monster.common.authenticatior.ErrorCode;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class JwtAuthenticationEntryPoint  implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorCode exception = (ErrorCode) request.getAttribute("exception");

        //토큰이 없으면
        if(exception == null) {
            setResponse(response, ErrorCode.NOACCESS_TOKEN);
            return;
        }
       //토큰만료
        if(exception.equals(ErrorCode.EXPIRED_TOKEN)) {
            setResponse(response, ErrorCode.EXPIRED_TOKEN);
            return;
        }

        //토큰 시그니쳐가 다름
        if(exception.equals(ErrorCode.ILLEGALARGU_TOKEN)) {
            setResponse(response, ErrorCode.ILLEGALARGU_TOKEN);
        }
    }

    @ExceptionHandler(value = { AccessDeniedException.class })
    public void commence(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex ) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().println("{ \"error\": \"" + ex.getMessage() + "\" }");

        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(
                 "\", \"code\" : \"" +  errorCode.getCode()
                + "{ \"message\" : \"" + errorCode.getMessage()
                + "\", \"status\" : " + HttpServletResponse.SC_UNAUTHORIZED
                + ", \"errors\" : [ ] }");
    }
}