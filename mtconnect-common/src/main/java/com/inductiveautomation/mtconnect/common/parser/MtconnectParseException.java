package com.inductiveautomation.mtconnect.common.parser;

public class MtconnectParseException extends RuntimeException {
    public MtconnectParseException(String message) {
        super(message);
    }

    public MtconnectParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
