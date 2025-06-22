package org.eolang;

import org.takes.Take;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.fork.RqRegex;
import org.takes.rs.RsText;

public final class TkCp implements Take {

    @Override
    public Response act(final Request req) {
        final String attr = ((RqRegex) req).matcher().group("attr");

        try {
            Phi current = Current.get();
            Phi copy = current.copy();

            Phi root = Current.getRootPhi();
            if (!(root instanceof Mocked)) {
                return new RsText("Root φ is not a mockable object.");
            }

            ((Mocked) root).addAttr(attr, new AtOnce(new AtComposite(root, r -> copy)));
            return new RsText("Copied " + Current.formatPathWithChildren() + " to @ φ." + attr);

        } catch (Exception ex) {
            return new RsText("Failed to copy: " + ex.getMessage());
        }
    }
}
