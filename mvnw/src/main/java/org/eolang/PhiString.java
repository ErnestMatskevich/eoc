package org.eolang;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class PhiString extends PhDefault {
    private final String value;
    
    public PhiString(String value) {
        super(value.getBytes(StandardCharsets.UTF_8));
        this.value = value;
        this.add("φ", new AtOnce(new AtComposite(this, rho -> this)));
        //System.out.println("Data bytes: " + Arrays.toString(this.delta()));
    }
    
    @Override
    public String forma() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
    @Override
    public byte[] delta() {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
