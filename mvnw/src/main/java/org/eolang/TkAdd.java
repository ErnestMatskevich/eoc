package org.eolang;

import org.takes.Take;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.fork.RqRegex;
import org.takes.rs.RsText;

public final class TkAdd implements Take {
    
    @Override
    public Response act(final Request req) {
        final String name = ((RqRegex) req).matcher().group("attr");
        
        Phi current = Current.get();
        if (!(current instanceof Mocked)) {
            return new RsText("Current object is not mockable.");
        }
        
        Mocked mock = (Mocked) current;
        
        if (mock.attrNames().contains(name)) {
            return new RsText("Attribute '" + name + "' already exists.");
        }
        
        mock.addAttr(name, new AtVoid(name));
        return new RsText("Added attribute: '" + name + "'\n\n" + Current.formatPathWithChildren());
    }
}
