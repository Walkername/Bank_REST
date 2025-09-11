package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Exception response DTO containing error details")
public class ExceptionResponse {

    @Schema(description = "Error message", example = "Insufficient funds")
    private String error;

    @Schema(description = "Timestamp when error occurred (milliseconds since epoch)", example = "1701427200000")
    private long timestamp;

    public ExceptionResponse(String error, long timestamp) {
        this.error = error;
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
