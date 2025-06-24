package org.eolang;

import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsText;

import java.util.List;

public final class TkRun implements Take {
    @Override
    public Response act(Request req) {
        try {
            Phi current = Current.get();
            Phi target = current;
            
            // Вызываем take("Δ") только если это НЕ PhiString
            if (!(current instanceof PhiString)) {
                try {
                    target = current.take("Δ");
                } catch (Exception ignored) {
                    // если Δ нет — остаёмся на current
                    target = current;
                }
            }
            
            // Если target — PhiString, датаизируем напрямую
            if (target instanceof PhiString) {
                return new RsText(new Dataized(target).asString());
            }
            
            // Если target — Mocked, обходим атрибуты и собираем значения
            if (target instanceof Mocked) {
                Mocked mocked = (Mocked) target;
                List<String> names = mocked.attrNames();
                StringBuilder sb = new StringBuilder();
                for (String name : names) {
                    if ("φ".equals(name)) {
                        continue;
                    }
                    try {
                        Phi attr = target.take(name);
                        String value = new Dataized(attr).asString();
                        sb.append(name).append(": ").append(value).append("\n");
                    } catch (Exception ex) {
                        sb.append(name).append(": [ERROR: ").append(ex.getMessage()).append("]\n");
                    }
                }
                return new RsText(sb.toString().trim());
            }
            
            // Для всех остальных — просто датаизируем
            String result = new Dataized(target).asString();
            return new RsText(result);
            
        } catch (Exception e) {
            return new RsText("Failed to dataize: " + e.getMessage());
        }
    }
}
