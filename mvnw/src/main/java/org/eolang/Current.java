package org.eolang;

import org.eolang.Phi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Current {
    private static final MockProgram root = new MockProgram();
    private static final Phi rootPhi;
    
    private static Phi current;
    private static final List<String> path = new ArrayList<>();
    
    static {
        try {
            rootPhi = root.take("φ");
            current = rootPhi;
            path.add("φ");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to take φ from root: " + e.getMessage(), e);
        }
    }
    
    private Current() { }
    
    public static Phi get() {
        return current;
    }
    
    public static void set(Phi phi) {
        current = phi;
    }
    
    public static void addToPath(String name) {
        path.add(name);
    }
    
    public static List<String> getPath() {
        return Collections.unmodifiableList(path);
    }
    
    public static void resetPath(List<String> newPath) {
        path.clear();
        path.addAll(newPath);
    }
    
    public static Phi getRoot() {
        return root;
    }
    
    public static Phi getRootPhi() {
        return rootPhi;
    }
    
    public static String formatPathWithChildren() {
        StringBuilder sb = new StringBuilder();
        sb.append("@ ");
        sb.append(String.join(" ", path));
        Phi current = get();
        if (current instanceof Mocked) {
            Mocked mock = (Mocked) current;
            List<String> names = mock.attrNames();
            
            if (names.isEmpty()) {
                // Нет атрибутов вообще — пустой объект
                sb.append(" ∅");
            } else if (names.size() == 1 && "φ".equals(names.get(0))) {
                // Только φ — сформированная формация
                sb.append(" ⟦⟧");
            } else {
                // Остальные случаи — показываем список атрибутов без φ
                List<String> filtered = new ArrayList<>();
                for (String name : names) {
                    if (!"φ".equals(name)) {
                        filtered.add(name);
                    }
                }
                sb.append(" ⟦");
                sb.append(String.join(", ", filtered));
                sb.append("⟧");
            }
        }
        return sb.toString();
    }
}
