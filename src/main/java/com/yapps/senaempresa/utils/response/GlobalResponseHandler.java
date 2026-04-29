package com.yapps.senaempresa.utils.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Collection;

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Exclude methods that return the ErrorResponse directly or already wrapped GenericResponse
        Class<?> returnClass = returnType.getParameterType();
        return !returnClass.isAssignableFrom(ErrorResponse.class)
                && !returnClass.isAssignableFrom(GenericResponse.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        
        // Prevent wrapping exceptions managed by GlobalExceptionHandler or Spring's default error handling
        if (body instanceof ErrorResponse) {
            return body;
        }

        int statusCode = 200;
        if (response instanceof ServletServerHttpResponse servletResponse) {
            statusCode = servletResponse.getServletResponse().getStatus();
        }

        int length = 1;
        if (body == null) {
            length = 0;
        } else if (body instanceof Collection<?> collection) {
            length = collection.size();
        }

        GenericResponse.ResponseData<Object> responseData = GenericResponse.ResponseData.builder()
                .length(length)
                .statusCode(statusCode)
                .body(body)
                .build();

        return GenericResponse.builder().response(responseData).build();
    }
}
