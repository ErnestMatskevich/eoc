// TkDd.java — обработчик команды `dd foo Φ.bar`
package org.eolang;

import org.takes.Take;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.fork.RqRegex;
import org.takes.rs.RsText;

import java.util.Arrays;
import java.util.List;

public final class TkDd implements Take {
    
    @Override
    public Response act(final Request req) {
        final String attr = ((RqRegex) req).matcher().group("attr");
        final String pathAttr = ((RqRegex) req).matcher().group("path");
        
        try {
            Phi current = Current.get();
            Phi result = current.take(attr).copy().take("φ"); // без delta
            
            final Phi rootPhi = Current.getRootPhi();
            if (!(rootPhi instanceof Mocked)) {
                return new RsText("Root φ is not a mockable object.");
            }
            final Mocked root = (Mocked) rootPhi;
            
            final List<String> parts = Arrays.asList(pathAttr.split("\\."));
            if (parts.isEmpty()) {
                return new RsText("Invalid attribute path.");
            }
            
            // Переход по пути, создавая контейнеры при необходимости
            Mocked container = root;
            for (int i = 0; i < parts.size() - 1; i++) {
                final String part = parts.get(i);
                Phi next;
                try {
                    next = container.take(part).copy();
                } catch (Exception ex) {
                    Mocked newMock = new Mocked();
                    container.addAttr(part, new AtOnce(new AtComposite(container, r -> newMock)));
                    next = newMock;
                }
                if (!(next instanceof Mocked)) {
                    return new RsText("Attribute '" + part + "' is not a mockable object.");
                }
                container = (Mocked) next;
            }
            
            final String lastPart = parts.get(parts.size() - 1);
            if (container.attrNames().contains(lastPart)) {
                return new RsText("Attribute '" + lastPart + "' already exists.\n" +
                    "Choose a different name or remove the existing attribute with 'rm'.");
            }
            
            container.addAttr(
                lastPart,
                new AtOnce(new AtComposite(container, r -> result))
            );
            
            return new RsText("Dispatched '" + attr + "' and attached result to @ φ." + pathAttr);
            
        } catch (Exception ex) {
            return new RsText("Failed to dispatch: " + ex.getMessage());
        }
    }
}
