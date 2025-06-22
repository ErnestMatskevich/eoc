package org.eolang;

import java.util.ArrayList;
import java.util.List;

/**
 * Расширение PhDefault с отслеживанием имён добавленных атрибутов.
 */
public class Mocked extends PhDefault {
    
    private final List<String> attrNames = new ArrayList<>();
    
    public Mocked() {
        super();
    }
    
    public void addAttr(String name, Attr attr) {
        super.add(name, attr);
        attrNames.add(name);
    }
    
    public void removeAttr(String name) {
        if (attrNames.contains(name)) {
            // Since there is no way to remove an attribute from PhDefault,
            // we will just overwrite it with AtVoid.
            super.add(name, new AtVoid(name));
            attrNames.remove(name); // Remove from the tracking list
        }
    }
    
    public List<String> attrNames() {
        return attrNames;
    }
}
