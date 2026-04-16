package com.yapps.senaempresa.utils.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessResult<T> {
    private Long resultCode;
    private String message;
    private T result;
    private StringBuilder log;
}
