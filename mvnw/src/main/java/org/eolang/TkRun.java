package org.eolang;

import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsText;

/**
 * Take для команды run: датаизирует текущий объект.
 */
public final class TkRun implements Take {
//    @Override
//    public Response act(Request req) {
//        try {
//            Phi current = Current.get();
//            Phi target = current;
//            try {
//                target = current.take("Δ");
//            } catch (Exception ignored) {
//                // если Δ нет — датаизируем сам current
//            }
//            // Датаизируем текущий объект как строку
//            String result = new Dataized(target).asString();
//
//            return new RsText(result);
//        } catch (Exception e) {
//            return new RsText("Failed to dataize: " + e.getMessage());
//        }
//    }
public Response act(Request req) {
    try {
        Phi current = Current.get();
        Phi target = current;
        
        if (!(current instanceof PhiString)) {
            try {
                target = current.take("Δ");
            } catch (Exception ignored) {
                // если Δ нет — датаизируем сам current
            }
        }
        
        String result = new Dataized(target).asString();
        return new RsText(result);
    } catch (Exception e) {
        return new RsText("Failed to dataize: " + e.getMessage());
    }
}

}
