package org.eolang;

import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.RqRegex;
import org.takes.rs.RsText;

import java.util.regex.Pattern;

public final class TkPut implements Take {
    
    private static final Pattern HEX_PATTERN = Pattern.compile("^([0-9A-Fa-f]{2}(-[0-9A-Fa-f]{2})*)$");
    
    @Override
    public Response act(Request req) {
        String bytesStr = ((RqRegex) req).matcher().group("bytes");
        
        try {
            Phi current = Current.get();
            if (!(current instanceof Mocked)) {
                return new RsText("Current object is not Mock.");
            }
            Mocked mock = (Mocked) current;
            
            if (!HEX_PATTERN.matcher(bytesStr).matches()) {
                return new RsText("Invalid bytes format. Expected like: 48-65-6C-6C-6F");
            }
            
            byte[] bytes = parseHexBytes(bytesStr);
            PhiBytes phiBytes = new PhiBytes(bytes);
            
            // Кладём PhiBytes в Δ текущего объекта
            try {
                mock.put("Δ", phiBytes);
            } catch (Exception e) {
                mock.addAttr("Δ", new AtOnce(new AtComposite(mock, r -> phiBytes)));
            }
            
            return new RsText(Current.formatPathWithChildren());
        } catch (Exception e) {
            return new RsText("Failed to put bytes: " + e.getMessage());
        }
    }
    
    private static byte[] parseHexBytes(String hexStr) {
        String[] parts = hexStr.split("-");
        byte[] bytes = new byte[parts.length];
        for (int i = 0; i < parts.length; i++) {
            bytes[i] = (byte) Integer.parseInt(parts[i], 16);
        }
        return bytes;
    }
}
