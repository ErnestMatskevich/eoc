package org.eolang;

import org.takes.Take;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.fork.RqRegex;
import org.takes.rs.RsText;

import java.util.List;

/**
 * Handles deletion of an attribute from the current EO object.
 * Usage: /rm/foo or -foo in CLI.
 */
public final class TkRm implements Take {
    
    @Override
    public Response act(final Request req) {
        final String name = ((RqRegex) req).matcher().group("attr");
        
        Phi current = Current.get();
        if (!(current instanceof Mocked)) {
            return new RsText("Current object is not mockable.");
        }
        
        Mocked mock = (Mocked) current;
        List<String> attrs = mock.attrNames();
        
        if (!attrs.contains(name)) {
            return new RsText("Attribute '" + name + "' does not exist.");
        }
        
        // Удалим из PhDefault и из списка attrNames
        mock.removeAttr(name);
        
        return new RsText("Removed attribute: '" + name + "'\n\n" + Current.formatPathWithChildren());
    }
}
