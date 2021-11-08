package io.msdousti.triop;

import lombok.Data;

import java.nio.ByteBuffer;

@Data
public class Version {
    private final int me;
    private final Operation op;
    private final int idx;

    public ByteBuffer toByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(2 * Long.BYTES + 1);
        buffer.putInt(me);
        buffer.putInt(idx);
        buffer.put((byte) op.ordinal());
        return buffer.rewind();
    }
}
