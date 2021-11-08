package io.msdousti.triop;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockTest {
    static private Random r;

    @BeforeAll
    static void setup() {
        r = new SecureRandom();
    }

    @Test
    void getCV() {
        byte[] c = new byte[100];
        Version v = new Version(14343, Operation.chg, 203902);

        r.nextBytes(c);
        Block b = new Block(c, v, null);
        byte[] vArr = v.toByteBuffer().array();
        byte[] cv = b.getCV().array();

        for (int i = 0; i < c.length; i++)
            assertEquals(c[i], cv[i]);
        for (int i = 0; i < vArr.length; i++)
            assertEquals(vArr[i], cv[i + c.length]);
    }
}