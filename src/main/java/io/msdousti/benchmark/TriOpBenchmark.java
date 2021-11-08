package io.msdousti.benchmark;

import io.msdousti.triop.Block;
import io.msdousti.triop.Operation;
import io.msdousti.triop.TriOpBlockchain;
import org.openjdk.jmh.annotations.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class TriOpBenchmark {

    private TriOpBlockchain chain;

    @Setup
    public void setUp()
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        chain = new TriOpBlockchain();
        for (int i = 0; i < 1000; i++) {
            Block b = chain.create(new byte[10000]);
            chain.install(Operation.apd, i, b);
        }
    }

    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode(Mode.Throughput)
    @Benchmark
    public void redactChange() throws SignatureException {
        chain.redact(Operation.chg, 100, new byte[10000]);
    }

    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode(Mode.Throughput)
    @Benchmark
    public void redactInsert() throws SignatureException {
        chain.redact(Operation.ins, 100, new byte[10000]);
    }

    @Fork(value = 1, warmups = 1)
    @Warmup(iterations = 1)
    @Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
    @BenchmarkMode(Mode.Throughput)
    @Benchmark
    public void redactRemove() throws SignatureException {
        chain.redact(Operation.rem, 100, new byte[10000]);
    }
}
