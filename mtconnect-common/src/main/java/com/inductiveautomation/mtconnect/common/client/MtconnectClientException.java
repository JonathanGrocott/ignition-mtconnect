package com.inductiveautomation.mtconnect.common.client;

public class MtconnectClientException extends RuntimeException {
    public MtconnectClientException(String message) {
        super(message);
    }

    public MtconnectClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
