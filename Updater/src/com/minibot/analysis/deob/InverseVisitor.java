package com.minibot.analysis.deob;

import com.minibot.util.Populous;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.util.Assembly;
import org.objectweb.asm.commons.wrapper.ClassFactory;
import org.objectweb.asm.commons.wrapper.ClassField;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.math.BigInteger;
import java.util.*;

/**
 * @author Tyler Sedlar
 * @since 3/8/15.
 */
public class InverseVisitor extends MethodVisitor implements Opcodes {

    private final Map<String, List<BigInteger>> decoders = new HashMap<>();
    private final Map<String, List<BigInteger>> encoders = new HashMap<>();

    private final Map<String, ClassFactory> classes;

    public InverseVisitor(Map<String, ClassFactory> classes) {
        this.classes = classes;
    }

    @Override
    public void visitFieldInsn(FieldInsnNode fin) {
        if (fin.desc.equals("I")) {
            AbstractInsnNode first = fin.next();
            if (first == null || first.opcode() == PUTSTATIC || first.opcode() == PUTFIELD) {
                return;
            }
            AbstractInsnNode second = first.next();
            if (second == null || second.opcode() == PUTSTATIC || second.opcode() == PUTFIELD) {
                return;
            }
            AbstractInsnNode third = second.next();
            if (third == null || third.opcode() == PUTSTATIC || third.opcode() == PUTFIELD || third.opcode() == ILOAD) {
                return;
            }
            AbstractInsnNode fourth = third.next();
            if (fourth != null) {
                fourth = fourth.next();
                if (fourth != null) {
                    fourth = fourth.next();
                    if (fourth != null && fourth.opcode() == ALOAD) {
                        if (((VarInsnNode) fourth).var != 0) {
                            return;
                        }
                    }
                }
            }
            List<LdcInsnNode> ldcs = new ArrayList<>();
            LdcInsnNode ldc = Assembly.next(fin, LDC, 2);
            if (ldc != null && ldc.cst instanceof Integer && ((int) ldc.cst) % 2 != 0) {
                ldcs.add(ldc);
            }
            ldc = Assembly.previous(fin, LDC, 2);
            if (ldc != null && ldc.cst instanceof Integer && ((int) ldc.cst) % 2 != 0) {
                ldcs.add(ldc);
            }
            for (LdcInsnNode ldcInsn : ldcs) {
                Modulus mod = new Modulus(BigInteger.valueOf((int) ldcInsn.cst), 32);
                if (mod.validate()) {
                    String key = fin.owner + "." + fin.name;
                    boolean getting = fin.opcode() == GETFIELD || fin.opcode() == GETSTATIC;
                    Map<String, List<BigInteger>> map = getting ? decoders : encoders;
                    if (!map.containsKey(key)) {
                        map.put(key, new LinkedList<>());
                    }
                    map.get(key).add(mod.quotient);
                }
            }
        }
    }

    public Map<String, List<BigInteger>> getDecoders() {
        return decoders;
    }

    public Map<String, List<BigInteger>> getEncoders() {
        return encoders;
    }

    private class Modulus {

        public final BigInteger quotient;
        public final int bits;

        public Modulus(BigInteger quotient, int bits) {
            this.quotient = quotient;
            this.bits = bits;
        }

        public BigInteger compute() {
            try {
                BigInteger shift = BigInteger.ONE.shiftLeft(bits);
                return quotient.modInverse(shift);
            } catch (ArithmeticException e) {
                return null;
            }
        }

        public boolean validate() {
            return compute() != null;
        }
    }

    public BigInteger inverseFor(String clazz, String field) {
        String key = clazz + "." + field;
        Populous<BigInteger> populous = new Populous<>();
        if (decoders.containsKey(key) && encoders.containsKey(key)) {
            for (BigInteger bigIntD : decoders.get(key)) {
                for (BigInteger bigIntE : encoders.get(key)) {
                    if (bigIntD.intValue() * bigIntE.intValue() == 1) {
                        populous.add(bigIntD);
                        break;
                    }
                }
            }
        }
        if (!populous.isEmpty() && populous.uniqueCount() == 1) {
            return populous.top();
        }
        if (decoders.containsKey(key)) {
            populous.addAll(decoders.get(key));
            return populous.top();
        }
        if (encoders.containsKey(key)) {
            populous.addAll(encoders.get(key));
            return new Modulus(populous.top(), 32).compute();
        }
        ClassFactory factory = classes.get(clazz);
        if (factory != null) {
            ClassFactory superFactory = classes.get(factory.superName());
            if (superFactory != null) {
                ClassField superField = superFactory.findField(cf -> cf.name().equals(field));
                if (superField != null) {
                    return inverseFor(superField.owner.name(), superField.name());
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return Integer.toString(decoders.size());
    }
}