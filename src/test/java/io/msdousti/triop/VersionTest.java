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
    void toBytes() {
        int me = r.nextInt();
        int idx = r.nextInt();
        Version v = new Version(me, Operation.chg, idx);
        ByteBuffer vBuff = ByteBuffer.wrap(v.toBytes()).rewind();

        assertEquals(me, vBuff.getInt(0));
        assertEquals(idx, vBuff.getInt(Integer.BYTES));
        assertEquals(1, vBuff.get(2 * Integer.BYTES));
    }
}