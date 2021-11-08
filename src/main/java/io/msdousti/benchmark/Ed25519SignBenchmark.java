package io.msdousti.benchmark;

import org.openjdk.jmh.annotations.*;

import java.security.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class Ed25519SignBenchmark {

    private Signature sig;
    private final byte[] b = new byte[10000];

    @Setup
    public void setUp() throws NoSuchAlgorithmException, InvalidKeyException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("Ed25519");
        KeyPair kp = kpg.generateKeyPair();
        sig = Signature.getInstance("Ed25519");
        sig.initSign(kp.getPrivate());
    }

    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode(Mode.Throughput)
    @Benchmark
    public void sign() throws SignatureException {
        sig.update(b);
        sig.sign();
    }
}
