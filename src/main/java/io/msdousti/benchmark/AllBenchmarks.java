package io.msdousti.benchmark;

import io.msdousti.triop.Block;
import io.msdousti.triop.Operation;
import io.msdousti.triop.TriOpBlockchain;
import org.openjdk.jmh.annotations.*;

import java.security.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(value = 2, warmups = 1)
@Warmup(iterations = 5, time = 4000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 4000, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
public class AllBenchmarks {

    private static final int BLOCK_SIZE = 10_000;
    private static final int LEDGER_LEN = 100_000;
    private static final int TARGET_BLOCK = 50;
    private static final String ED25519 = "Ed25519";

    private Signature signer;
    private Signature verifier;
    private final byte[] b = new byte[BLOCK_SIZE];
    private final byte[] newContent = new byte[BLOCK_SIZE];
    private byte[] sig;
    private TriOpBlockchain chain;

    @Setup
    public void setUp() throws GeneralSecurityException {
        setUpBlocks();
        setUpSignature();
        setUpChain();
    }

    private void setUpBlocks() {
        Random r = new SecureRandom();
        r.nextBytes(b);
        r.nextBytes(newContent);
    }

    private void setUpSignature() throws GeneralSecurityException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ED25519);
        KeyPair kp = kpg.generateKeyPair();
        signer = Signature.getInstance(ED25519);
        verifier = Signature.getInstance(ED25519);

        signer.initSign(kp.getPrivate());
        verifier.initVerify(kp.getPublic());

        signer.update(b);
        sig = signer.sign();
    }

    private void setUpChain() throws GeneralSecurityException {
        chain = new TriOpBlockchain();
        for (int i = 0; i < LEDGER_LEN; i++) {
            Block blk = chain.create(b);
            chain.install(Operation.apd, i, blk);
        }
    }

    // The following methods benchmark basic signature operations

    @Benchmark
    public byte[] sigSign() throws SignatureException {
        signer.update(b);
        return signer.sign();
    }


    @Benchmark
    public boolean sigVerify() throws GeneralSecurityException {
        verifier.update(b);
        return verifier.verify(sig);
    }

    // The following methods benchmark redaction WITHOUT installation

    @Benchmark
    public Block redactChange() throws SignatureException {
        return chain.redact(Operation.chg, TARGET_BLOCK, newContent);
    }

    @Benchmark
    public Block redactInsert() throws SignatureException {
        return chain.redact(Operation.ins, TARGET_BLOCK, newContent);
    }

    @Benchmark
    public Block redactRemove() throws SignatureException {
        return chain.redact(Operation.rem, TARGET_BLOCK, newContent);
    }

    // The following methods benchmark redaction AND installation

    @Benchmark
    public boolean installChange() throws SignatureException {
        Block blk = chain.redact(Operation.chg, TARGET_BLOCK, newContent);
        return chain.install(Operation.chg, TARGET_BLOCK, blk);
    }

    @Benchmark
    public boolean installInsert() throws SignatureException {
        Block blk = chain.redact(Operation.ins, TARGET_BLOCK, newContent);
        return chain.install(Operation.ins, TARGET_BLOCK, blk);
    }

    @Benchmark
    public boolean installRemove() throws SignatureException {
        Block blk = chain.redact(Operation.rem, TARGET_BLOCK, newContent);
        return chain.install(Operation.rem, TARGET_BLOCK, blk);
    }
}
