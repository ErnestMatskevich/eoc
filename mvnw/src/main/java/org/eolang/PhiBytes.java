package org.eolang;

import java.util.Arrays;

/**
 * Phi objects represents bytes in HEX format .
 */
public final class PhiBytes extends PhDefault {
    
    //private final byte[] bytes;
    
    public PhiBytes(final byte[] bytes) {
        super(bytes != null ? Arrays.copyOf(bytes, bytes.length) : null);
    }
    
    @Override
    public String forma() {
        final byte[] bytes;
        try {
            bytes = this.delta();
        } catch (Exception e) {
            return "∅";
        }
        
        if (bytes.length == 0) {
            return "∅";
        }
        
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X-", b));
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
