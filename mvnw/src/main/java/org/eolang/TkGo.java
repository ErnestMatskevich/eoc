package org.eolang;

import org.takes.Take;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.fork.RqRegex;
import org.takes.rs.RsText;

import java.io.IOException;

/**
 * Take to handle /go/{attr} requests, moving current to attribute.
 */
public final class TkGo implements Take {
    
    @Override
    public Response act(Request req) throws IOException {
        final String attr = ((RqRegex) req).matcher().group("attr");
        
        try {
            Phi current = Current.get();
            Phi next = current.take(attr);
            Current.set(next);
            Current.addToPath(attr);
            return new RsText(Current.formatPathWithChildren());
        } catch (Exception ex) {
            return new RsText("Error: attribute '" + attr + "' not found or not accessible.\n" + ex.getMessage());
        }
    }
}
