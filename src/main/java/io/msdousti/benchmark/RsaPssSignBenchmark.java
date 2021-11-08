package io.msdousti.benchmark;

import org.openjdk.jmh.annotations.*;

import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class RsaPssSignBenchmark {

    private Signature sig;
    private final byte[] b = new byte[10000];

    @Setup
    public void setUp() throws NoSuchAlgorithmException,
            InvalidKeyException, InvalidAlgorithmParameterException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSASSA-PSS");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        sig = Signature.getInstance("RSASSA-PSS");
        sig.setParameter(new PSSParameterSpec(
                "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
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
