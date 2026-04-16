package com.yapps.senaempresa.utils.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {

    private ResponseData<T> response;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseData<T> {
        private int length;
        private int statusCode;
        private T body;
    }

    public static <T> GenericResponse<T> of(int statusCode, T body) {
        int length = 1;
        if (body instanceof Collection<?> collection) {
            length = collection.size();
        } else if (body == null) {
            length = 0;
        }

        ResponseData<T> responseData = ResponseData.<T>builder()
                .length(length)
                .statusCode(statusCode)
                .body(body)
                .build();

        return GenericResponse.<T>builder()
                .response(responseData)
                .build();
    }
}
