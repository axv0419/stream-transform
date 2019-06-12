package com.dexcom.streamtransform;

public class StreamRecord {
    private byte [] originalContent;
    private String convertedString;
    private boolean valid;
    private String validationFailureReason;

    public byte[] getOriginalContent() {
        return originalContent;
    }

    public String getConvertedString() {
        return convertedString;
    }

    public boolean isValid() {
        return valid;
    }

    public String getValidationFailureReason() {
        return validationFailureReason;
    }

    public StreamRecord(byte[] originalContent, String convertedString, boolean valid, String validationFailureReason) {
        this.originalContent = originalContent;
        this.convertedString = convertedString;
        this.valid = valid;
        this.validationFailureReason = validationFailureReason;
    }
}
