package com.dexcom.streamtransform;

public class StreamRecord {
    private byte [] originalContent;

    public StreamRecord(byte[] originalContent, String convertedString, boolean valid) {
        this.originalContent = originalContent;
        this.convertedString = convertedString;
        this.valid = valid;
    }

    public byte[] getOriginalContent() {
        return originalContent;
    }

    public String getConvertedString() {
        return convertedString;
    }

    public boolean isValid() {
        return valid;
    }

    private String convertedString;
    private boolean valid;
}
