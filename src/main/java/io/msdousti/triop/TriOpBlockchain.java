package io.msdousti.triop;

import java.security.*;
import java.util.ArrayList;
import java.util.List;

public class TriOpBlockchain {
    private static final String ED25519 = "Ed25519";

    private final Signature signer;
    private final Signature verifier;
    private final List<Block> ledger = new ArrayList<>();
    private int vMax = 1;

    public TriOpBlockchain() throws NoSuchAlgorithmException, InvalidKeyException {
        signer = Signature.getInstance(ED25519);
        verifier = Signature.getInstance(ED25519);
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ED25519);
        KeyPair kp = kpg.generateKeyPair();
        signer.initSign(kp.getPrivate());
        verifier.initVerify(kp.getPublic());

        ledger.add(new Block(null, new Version(1, Operation.apd, 0), null));
    }

    public Block create(byte[] c) {
        return new Block(c, new Version(vMax, Operation.apd, ledger.size()), null);
    }

    public boolean verify(Operation op, int i, Block b) throws SignatureException {
        return (b != null) && phi(op, i, b.getV()) && psi(b);
    }

    public boolean install(Operation op, int i, Block b) throws SignatureException {
        if (!verify(op, i, b)) return false;
        transformLedger(op, i, b);
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
        if (!ind(i, op)) return null;
        Version v = new Version(vMax, op, i);
        Block b = new Block(c, v, null);

        signer.update(b.getCV());
        b.setW(signer.sign());

        return b;
    }

    /*
     *
     *
     * Private methods
     *
     *
     */

    private boolean phi(Operation op, int i, Version v) {
        return ind(i, op) &&
               v.getOp() == op &&
               v.getIdx() == i &&
               v.getMe() == vMax;
    }

    private boolean psi(Block b) throws SignatureException {
        if (b.getV().getOp() != Operation.apd) {
            verifier.update(b.getCV());
            return verifier.verify(b.getW());
        }
        return true;
    }

    private boolean ind(int i, Operation op) {
        if (op == Operation.apd)
            return i == ledger.size();
        else
            return (i >= 1) && (i < ledger.size());
    }

    void transformLedger(Operation op, int i, Block b) {
        switch (op) {
            case apd -> ledger.add(b);
            case chg -> ledger.set(i, b);
            case ins -> ledger.add(i, b);
            case rem -> {
                ledger.remove(i);
                ledger.set(i - 1, b);
            }
        }
    }
}
