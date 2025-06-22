package org.eolang;

import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsText;

public final class TkForm implements Take {
    @Override
    public Response act(Request req) {
        try {
            Phi current = Current.get();
            if (!(current instanceof Mocked)) {
                return new RsText("Current object is not Mock.");
            }
            Mocked mock = (Mocked) current;
            
            // Добавляем или заменяем атрибут φ на формацию (ссылку на самого себя)
            mock.addAttr("φ", new AtOnce(new AtComposite(mock, r -> mock)));
            
            return new RsText(Current.formatPathWithChildren());
        } catch (Exception e) {
            return new RsText("Failed to form: " + e.getMessage());
        }
    }
}
