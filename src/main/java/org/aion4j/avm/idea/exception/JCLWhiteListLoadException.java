package org.aion4j.avm.idea.exception;

public class JCLWhiteListLoadException extends RuntimeException {

    public JCLWhiteListLoadException(String message) {
        super(message);
    }

    public JCLWhiteListLoadException(String message, Exception e) {
        super(message, e);
    }
}
