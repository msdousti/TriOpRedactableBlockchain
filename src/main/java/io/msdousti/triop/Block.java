package io.msdousti.triop;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

@Getter
@Setter
public class Block {
    private byte[] c;
    private Version v;
    private byte[] w;

    public Block(byte[] c, Version v, byte[] w) {
        this.c = (c == null) ? null : c.clone();
        this.v = v;
        this.w = (w == null) ? null : w.clone();
    }

    public byte[] getCV() {
        byte[] vBytes = v.toBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(c.length + vBytes.length);
        byteBuffer.put(c);
        byteBuffer.put(vBytes);
        return byteBuffer.rewind().array();
    }
}
