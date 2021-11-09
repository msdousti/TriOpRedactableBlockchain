package io.msdousti.benchmark;

import io.msdousti.triop.Block;
import io.msdousti.triop.Operation;
import io.msdousti.triop.TriOpBlockchain;
import org.openjdk.jmh.annotations.*;

import java.security.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(value = 1, warmups = 1)
@Warmup(iterations = 1)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
public class Benchmark {

    private static final int BLOCK_SIZE = 10000;
    private static final int LEDGER_LEN = 1000;
    private static final int TARGET_BLOCK = 50;

    private Signature sig;
    private final byte[] b = new byte[BLOCK_SIZE];
    private byte[] s;
    private KeyPair kp;
    private TriOpBlockchain chain;

    @Setup
    public void setUp() throws GeneralSecurityException {
        setUpSignature();
        setUpChain();
    }

    public void setUpSignature() throws GeneralSecurityException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("Ed25519");
        kp = kpg.generateKeyPair();
        sig = Signature.getInstance("Ed25519");
        sig.initSign(kp.getPrivate());

        sig.update(b);
        s = sig.sign();
    }

    private void setUpChain() throws GeneralSecurityException {
        chain = new TriOpBlockchain();
        for (int i = 0; i < LEDGER_LEN; i++) {
            Block blk = chain.create(new byte[BLOCK_SIZE]);
            chain.install(Operation.apd, i, blk);
        }
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void sign() throws SignatureException {
        sig.update(b);
        sig.sign();
    }


    @org.openjdk.jmh.annotations.Benchmark
    public void verify() throws GeneralSecurityException {
        sig.initVerify(kp.getPublic());

        sig.update(b);
        sig.verify(s);
    }


    @org.openjdk.jmh.annotations.Benchmark
    public void redactChange() throws SignatureException {
        chain.redact(Operation.chg, TARGET_BLOCK, new byte[BLOCK_SIZE]);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void redactInsert() throws SignatureException {
        chain.redact(Operation.ins, TARGET_BLOCK, new byte[BLOCK_SIZE]);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void redactRemove() throws SignatureException {
        chain.redact(Operation.rem, TARGET_BLOCK, new byte[BLOCK_SIZE]);
    }
}
