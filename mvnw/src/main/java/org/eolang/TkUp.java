package org.eolang;

import org.takes.Take;
import org.takes.Request;
import org.takes.Response;
import org.takes.rs.RsText;

import java.util.ArrayList;
import java.util.List;

public final class TkUp implements Take {
    @Override
    public Response act(final Request req) {
        List<String> originalPath = new ArrayList<>(Current.getPath());
        
        if (originalPath.size() <= 1) {
            return new RsText("Already at top level, cannot go up.");
        }
        
        List<String> newPath = originalPath.subList(0, originalPath.size() - 1);
        
        try {
            Phi obj = Current.getRootPhi();
            for (int i = 1; i < newPath.size(); i++) {
                obj = obj.take(newPath.get(i));
            }
            Current.set(obj);
            Current.resetPath(new ArrayList<>(newPath));
            return new RsText(Current.formatPathWithChildren());
        } catch (final Exception ex) {
            return new RsText("Failed to navigate up: " + ex.getMessage());
        }
    }
}
