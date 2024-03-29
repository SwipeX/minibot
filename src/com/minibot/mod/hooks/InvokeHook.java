package com.minibot.mod.hooks;

import com.minibot.mod.Crypto;
import com.minibot.mod.ModScript;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author Tyler Sedlar
 */
public class InvokeHook extends Hook {

    private int predicate;

    private String clazz;
    private String method;
    private String desc;
    private String predicateType;
    private Class<?> predicateTypeClass;

    @Override
    protected void readData(DataInputStream in) throws IOException {
        setName(Crypto.decrypt(in.readUTF()));
        clazz = Crypto.decrypt(in.readUTF());
        method = Crypto.decrypt(in.readUTF());
        desc = Crypto.decrypt(in.readUTF());
        predicate = in.readInt();
        predicateType = Crypto.decrypt(in.readUTF());
    }

    private Class<?> resolvePredicateTypeClass() {
        return predicateType.equals("I") ? int.class : (predicateType.equals("B") ? byte.class : short.class);
    }

    private Method findMethod(Class<?>... parameterTypes) {
        try {
            Method[] methods = ModScript.classes().loadClass(clazz).getDeclaredMethods();
            main:
            for (Method m : methods) {
                if (m.getName().equals(method)) {
                    Class<?>[] types = m.getParameterTypes();
                    if (types.length == parameterTypes.length) {
                        for (int i = 0; i < types.length; i++) {
                            if (types[i] != parameterTypes[i]) {
                                continue main;
                            }
                        }
                        String returnType = m.getGenericReturnType().getTypeName();
                        switch (returnType) {
                            case "void": {
                                returnType = "V";
                                break;
                            }
                            case "boolean": {
                                returnType = "Z";
                                break;
                            }
                            case "int": {
                                returnType = "I";
                                break;
                            }
                            case "byte": {
                                returnType = "B";
                                break;
                            }
                            case "short": {
                                returnType = "S";
                                break;
                            }
                            case "char": {
                                returnType = "C";
                                break;
                            }
                            case "long": {
                                returnType = "J";
                                break;
                            }
                        }
                        if (desc.contains(")L")) {
                            if (!desc.endsWith(")L" + returnType + ";")) {
                                continue;
                            }
                        } else {
                            if (!desc.endsWith(")" + returnType)) {
                                continue;
                            }
                        }
                        return m;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object invoke(Object instance, Class<?>[] parameterTypes, Object... values) {
        try {
            if (predicate != Integer.MAX_VALUE) {
                Object predicateValue;
                if (predicateTypeClass == int.class) {
                    predicateValue = predicate;
                } else {
                    predicateValue = predicateTypeClass == byte.class ? (byte) predicate : (short) predicate;
                }
                Class<?>[] newParameterTypes = new Class<?>[parameterTypes.length + 1];
                if (parameterTypes.length > 0) {
                    System.arraycopy(parameterTypes, 0, newParameterTypes, 0, parameterTypes.length);
                }
                newParameterTypes[newParameterTypes.length - 1] = predicateTypeClass;
                parameterTypes = newParameterTypes;
                Object[] newValues = new Object[values.length + 1];
                if (values.length > 0) {
                    System.arraycopy(values, 0, newValues, 0, values.length);
                }
                newValues[newValues.length - 1] = predicateValue;
                values = newValues;
            }
            Method m = findMethod(parameterTypes);
            if (m != null) {
                m.setAccessible(true);
                return m.invoke(instance, values);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Object invoke(Object instance) {
        return invoke(instance, new Class<?>[0]);
    }

    public Object invoke() {
        return invoke(null);
    }

    public Object invokeStatic(Class<?>[] classes, Object... values) {
        return invoke(null, classes, values);
    }

    public Object invokeStatic() {
        return invokeStatic(new Class<?>[0]);
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getPredicate() {
        return predicate;
    }

    public void setPredicate(int predicate) {
        this.predicate = predicate;
    }

    public String getPredicateType() {
        return predicateType;
    }

    public void setPredicateType(String predicateType) {
        this.predicateType = predicateType;
    }

    public Class<?> getPredicateTypeClass() {
        return predicateTypeClass;
    }

    public void setPredicateTypeClass(Class<?> predicateTypeClass) {
        this.predicateTypeClass = predicateTypeClass;
    }
}