package org.eolang;

import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.RqRegex;
import org.takes.rs.RsText;

public final class TkTo implements Take {
    @Override
    public Response act(final Request req) {
        final String pathAttr = ((RqRegex) req).matcher().group("attr");
        try {
            final Phi current = Current.get();
            final Phi root = Current.getRootPhi();
            
            if (!(root instanceof Mocked) || !(current instanceof Mocked)) {
                return new RsText("Root or current object is not mockable");
            }
            
            Phi target = root;
            for (String part : pathAttr.split("\\.")) {
                target = target.take(part);
            }
            
            Mocked mockedCurrent = (Mocked) current;
            String lastPart = pathAttr.contains(".")
                ? pathAttr.substring(pathAttr.lastIndexOf('.') + 1)
                : pathAttr;
            
            if (mockedCurrent.attrNames().contains(lastPart)) {
                return new RsText("Attribute '" + lastPart + "' already exists");
            }
            
            Phi finalTarget = target;
            mockedCurrent.addAttr(lastPart, new AtOnce(new AtComposite(mockedCurrent, r -> finalTarget)));
            
            return new RsText("Attached @ φ." + pathAttr + " to current object");
        } catch (Exception e) {
            return new RsText("Failed to attach: " + e.getMessage());
        }
    }
}
