package org.eolang;

import org.eolang.*;

@XmirObject(
    name = "mock",
    oname = "mock",
    source = "mock.eo"
)
public final class MockProgram extends Mocked {
    private final Mocked root;
    
    public MockProgram() {
        this.addAttr("args", new AtVoid("args"));
        
        this.root = new Mocked();
        
        // val1
        Mocked val1 = new Mocked();
        val1.addAttr("attr1", new AtOnce(new AtComposite(val1, r -> new PhiString("some_value"))));
        val1.addAttr("attr2", new AtOnce(new AtComposite(val1, r -> new PhiString("another_value"))));
        
        // val2
        Mocked val2 = new Mocked();
        val2.addAttr("attrA", new AtOnce(new AtComposite(val2, r -> new PhiString("valueA"))));
        
        // empty
        Mocked empty = new Mocked();
        
        // Add val1, val2 и empty
        root.addAttr("val1", new AtOnce(new AtComposite(root, r -> val1)));
        root.addAttr("val2", new AtOnce(new AtComposite(root, r -> val2)));
        root.addAttr("empty", new AtOnce(new AtComposite(root, r -> empty)));
        
        // Сохраняем в φ
        this.addAttr("φ", new AtOnce(new AtComposite(this, rho -> root)));
    }
}
