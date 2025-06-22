package org.eolang;

import org.takes.Take;
import org.takes.Request;
import org.takes.Response;
import org.takes.rs.RsText;

import java.util.ArrayList;
import java.util.List;

public final class TkLs implements Take {
    
    @Override
    public Response act(final Request req) {
        Phi current = Current.get();
        
        if (!(current instanceof Mocked)) {
            return new RsText("Current object is not Mock.");
        }
        
        Mocked mock = (Mocked) current;
        StringBuilder out = new StringBuilder();
        out.append(Current.formatPathWithChildren()).append("\n\n");
        
        // Список атрибутов без φ
        List<String> attrs = new ArrayList<>();
        for (String name : mock.attrNames()) {
            if (!"φ".equals(name)) {
                attrs.add(name);
            }
        }
        
        if (attrs.isEmpty()) {
            out.append("∅\n"); // Показать пустоту, если кроме φ нет атрибутов
        } else {
            for (String name : attrs) {
                try {
                    Phi attr = mock.take(name);
                    out.append(name).append(" → ").append(attr.forma()).append("\n");
                } catch (Exception ex) {
                    out.append(name).append(" → ∅\n");
                }
            }
        }
        
        return new RsText(out.toString());
    }

}
