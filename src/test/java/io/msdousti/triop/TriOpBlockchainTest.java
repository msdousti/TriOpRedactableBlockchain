package io.msdousti.triop;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TriOpBlockchainTest {

    @Test
    void testChainCreation() throws Exception {
        TriOpBlockchain chain = new TriOpBlockchain();
        List<Block> ledger = chain.getLedger();

        assertEquals(1, ledger.size());
        assertEquals(1, chain.getVMax());

        Block b = ledger.get(0);
        assertNull(b.getC());
        assertNull(b.getW());
        assertEquals(new Version(1, Operation.apd, 0), b.getV());
    }

    @Test
    void testAppend() throws Exception {
        TriOpBlockchain chain = new TriOpBlockchain();
        final byte[] c = "New block".getBytes(StandardCharsets.UTF_8);
        Block b = chain.create(c);

        assertArrayEquals(c, b.getC());
        assertEquals(new Version(1, Operation.apd, 1), b.getV());
        assertNull(b.getW());

        assertTrue(chain.verify(Operation.apd, 1, b));

        assertTrue(chain.install(Operation.apd, 1, b));

        assertEquals(2, chain.getVMax());
        assertEquals(2, chain.getLedger().size());

        // The block cannot be appended again; neither at position 1 nor 2
        assertFalse(chain.verify(Operation.apd, 1, b));
        assertFalse(chain.verify(Operation.apd, 2, b));
    }

    private TriOpBlockchain initChain(int n) throws Exception {
        TriOpBlockchain chain = new TriOpBlockchain();
        final byte[] c = "New block".getBytes(StandardCharsets.UTF_8);

        for (int i = 1; i <= n; i++) {
            Block b = chain.create(c);
            assertTrue(chain.install(Operation.apd, i, b));
        }
        return chain;
    }

    @Test
    void testNAppends() throws Exception {
        int n = 100;
        TriOpBlockchain chain = initChain(n);

        assertEquals(n + 1, chain.getLedger().size());
        assertEquals(n + 1, chain.getVMax());
    }

    @Test
    void testChange() throws Exception {
        int n = 100;
        TriOpBlockchain chain = initChain(n);
        final byte[] c = "Changed block".getBytes(StandardCharsets.UTF_8);
        final int i = 3;

        Block b = chain.redact(Operation.chg, i, c);

        assertNotNull(b);
        assertArrayEquals(c, b.getC());
        assertEquals(new Version(n + 1, Operation.chg, i), b.getV());

        assertTrue(chain.install(Operation.chg, i, b));
        final List<Block> ledger = chain.getLedger();
        assertEquals(n + 1, ledger.size());
        assertEquals(n + 2, chain.getVMax());
        assertArrayEquals(c, ledger.get(i).getC());
    }

    @Test
    void testInsert() throws Exception {
        int n = 100;
        TriOpBlockchain chain = initChain(n);
        final byte[] c = "Inserted block".getBytes(StandardCharsets.UTF_8);
        final int i = 3;

        Block b = chain.redact(Operation.ins, i, c);

        assertNotNull(b);
        assertArrayEquals(c, b.getC());
        assertEquals(new Version(n + 1, Operation.ins, i), b.getV());

        assertTrue(chain.install(Operation.ins, i, b));
        final List<Block> ledger = chain.getLedger();
        assertEquals(n + 2, ledger.size());
        assertEquals(n + 2, chain.getVMax());
        assertArrayEquals(c, ledger.get(i).getC());
    }

    @Test
    void testRemove() throws Exception {
        int n = 100;
        TriOpBlockchain chain = initChain(n);
        final byte[] c = "Before removed block".getBytes(StandardCharsets.UTF_8);
        final int i = 3;

        Block b = chain.redact(Operation.rem, i, c);

        assertNotNull(b);
        assertArrayEquals(c, b.getC());
        assertEquals(new Version(n + 1, Operation.rem, i), b.getV());

        assertTrue(chain.install(Operation.rem, i, b));
        final List<Block> ledger = chain.getLedger();
        assertEquals(n, ledger.size());
        assertEquals(n + 2, chain.getVMax());
        assertArrayEquals(c, ledger.get(i - 1).getC());
    }

    @Test
    void staleBlockCannotBeInstalled() throws Exception {
        int n = 100;
        TriOpBlockchain chain = initChain(n);

        final byte[] c = "Before removed block".getBytes(StandardCharsets.UTF_8);
        final int i = 3;

        Block b = chain.redact(Operation.rem, i, c);

        assertTrue(chain.install(Operation.apd, n+1,
                chain.create(new byte[10])));

        assertFalse(chain.install(Operation.rem, i, b));

    }
}