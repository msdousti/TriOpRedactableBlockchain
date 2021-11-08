package io.msdousti.triop;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VersionTest {
    static private Random r;

    @BeforeAll
    static void setup() {
        r = new SecureRandom();
    }

    @RepeatedTest(value = 10)
    void toBuffer() {
        int me = r.nextInt();
        int idx = r.nextInt();
        Version v = new Version(me, Operation.chg, idx);
        final ByteBuffer buff = v.toByteBuffer();

        assertEquals(0, buff.position());
        assertEquals(2 * Integer.BYTES + 1, buff.limit());
        assertEquals(2 * Integer.BYTES + 1, buff.capacity());

        assertEquals(me, buff.getInt(0));
        assertEquals(idx, buff.getInt(Integer.BYTES));
        assertEquals(1, buff.get(2 * Integer.BYTES));
    }
}