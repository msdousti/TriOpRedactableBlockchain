package io.msdousti.triop;

import java.security.*;
import java.util.ArrayList;
import java.util.List;

public class TriOpBlockchain {
    private final Signature signer;
    private final Signature verifier;
    private final List<Block> ledger = new ArrayList<>();
    private int vMax = 1;

    public TriOpBlockchain() throws NoSuchAlgorithmException, InvalidKeyException {
        final String ed25519 = "Ed25519";

        signer = Signature.getInstance(ed25519);
        verifier = Signature.getInstance(ed25519);
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ed25519);
        KeyPair kp = kpg.generateKeyPair();
        signer.initSign(kp.getPrivate());
        verifier.initVerify(kp.getPublic());

        ledger.add(new Block(null, new Version(1, Operation.apd, 0), null));
    }

    public Block create(byte[] c) {
        return new Block(c, new Version(vMax, Operation.apd, ledger.size()), null);
    }

    public boolean verify(Operation op, int i, Block b) throws SignatureException {
        if (b == null) return false;
        Version[] v = getVersions();

        if (!phi(v, op, i, b.getV())) return false;
        List<Block> transformedLedger = transform(op, i, b);
        return psi(transformedLedger);
    }

    public boolean install(Operation op, int i, Block b) throws SignatureException {
        if (!verify(op, i, b)) return false;
        transformLedger(ledger, op, i, b);
        vMax++;
        return true;
    }

    public List<Block> getLedger() {
        return new ArrayList<>(ledger);
    }

    public int getVMax() {
        return vMax;
    }

    public Block redact(Operation op, int i, byte[] c) throws SignatureException {
        if (!ind(ledger.size(), i, op)) return null;
        Version v = new Version(vMax, op, i);
        Block b = new Block(c, v, null);

        signer.update(b.getCV());
        b.setW(signer.sign());

        return b;
    }

    /*
     *
     *
     * Package private methods
     *
     *
     */

    List<Block> transform(Operation op, int i, Block b) {
        List<Block> transformedLedger = new ArrayList<>(ledger);
        transformLedger(transformedLedger, op, i, b);
        return transformedLedger;
    }

    void transformLedger(List<Block> givenLedger, Operation op, int i, Block b) {
        switch (op) {
            case apd -> givenLedger.add(b);
            case chg -> givenLedger.set(i, b);
            case ins -> givenLedger.add(i, b);
            case rem -> {
                givenLedger.remove(i);
                givenLedger.set(i - 1, b);
            }
        }
    }

    boolean psi(List<Block> transformedLedger) throws SignatureException {
        for (Block b : transformedLedger) {
            if (b.getV().getOp() != Operation.apd) {
                verifier.update(b.getCV());
                if (!verifier.verify(b.getW()))
                    return false;
            }
        }
        return true;
    }

    boolean phi(Version[] versions, Operation op, int i, Version v) {
        return ind(versions.length, i, op) &&
               v.getOp() == op &&
               v.getIdx() == i &&
               v.getMe() == vMax;
    }

    boolean ind(int length, int i, Operation op) {
        if (op == Operation.apd)
            return i == length;
        else
            return (i >= 1) && (i < length);
    }

    Version[] getVersions() {
        Version[] v = new Version[ledger.size()];
        for (int i = 0; i < ledger.size(); i++)
            v[i] = ledger.get(i).getV();
        return v;
    }
}
