/**
 * Created by sandulmv on 27.11.16.
 */
package ru.compscicenter.java2016.implementor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SuccessorConstructor {
    private static String singleShift = "    ";

    private SuccessorConstructor() {
    }

    public static String constructSuccessor(Class ancestor) throws ImplementorException {
        if (Modifier.isFinal(ancestor.getModifiers())) {
            throw new ImplementorException("Cannot extend final class");
        }

        Collection<MethodSignature> undefinedMethods = getUndefinedMethods(ancestor);
        String signature = constructClassSignature(ancestor);
        String body = constructClassBody(undefinedMethods);

        return signature + "{" + "\n" + body + "}";
    }

    private static Collection<MethodSignature> getUndefinedMethods(Class ancestor) {
        if (ancestor.isInterface()) {
            return getUndefinedInterface(ancestor);
        } else {
            return getUndefinedAbstractClass(ancestor);
        }
    }

    private static Collection<MethodSignature> getUndefinedAbstractClass(Class ancestor) {
        if (!Modifier.isAbstract(ancestor.getModifiers())) {
            return new HashSet<>();
        }

        Set<MethodSignature> defined = new HashSet<>();
        Set<MethodSignature> undefined = new HashSet<>();

        for (Method m : ancestor.getDeclaredMethods()) {
            if (!Modifier.isAbstract(m.getModifiers())) {
                defined.add(new MethodSignature(m));
            }
        }
        for (Class iface : ancestor.getInterfaces()) {
            undefined.addAll(getUndefinedInterface(iface));
        }
        for (Method m : ancestor.getDeclaredMethods()) {
            if (Modifier.isAbstract(m.getModifiers())) {
                undefined.add(new MethodSignature(m));
            }
        }
        undefined.addAll(getUndefinedAbstractClass(ancestor.getSuperclass()));
        undefined.removeAll(defined);
        return undefined;
    }

    private static Collection<MethodSignature> getUndefinedInterface(Class ancestor) {
        Set<MethodSignature> undefined = new HashSet<>();
        for (Method m : ancestor.getMethods()) {
            undefined.add(new MethodSignature(m));
        }
        return undefined;
    }

    private static String constructClassSignature(Class ancestor) {
        StringBuilder fullSignature = new StringBuilder();
        String className = ancestor.getSimpleName() + SimpleImplementor.SUCCESSOR_SUFFIX + " ";
        String ancestorName = ancestor.getCanonicalName();

        fullSignature.append("public class").append(" ");
        fullSignature.append(className);
        if (ancestor.isInterface()) {
            fullSignature.append("implements").append(" ");
        } else {
            fullSignature.append("extends").append(" ");
        }
        fullSignature.append(ancestorName);
        fullSignature.append(" ");

        return fullSignature.toString();
    }

    private static String constructClassBody(Collection<MethodSignature> methods) {
        StringBuilder body = new StringBuilder();

        for (MethodSignature m : methods) {
            body.append(constructMethod(m)).append("\n");
        }

        return body.toString();
    }

    private static String constructMethod(MethodSignature method) {
        String signature = constructMethodSignature(method);
        String body = constructMethodBody(method);

        return singleShift + signature + "{" + "\n" + singleShift + body + "\n" + singleShift + "}";
    }

    private static String constructMethodSignature(MethodSignature method) {
        StringBuilder signature = new StringBuilder();
        String returnType = method.getReturnType().getCanonicalName();
        String methodName = method.getMethodName();
        Class[] parameterTypes = method.getParameterTypes();
        Class[] exceptionTypes = method.getExceptionTypes();
        String parameters = constructMethodParameters(parameterTypes);
        String exceptions = constructMethodExceptions(exceptionTypes);

        signature.append("public").append(" ");
        signature.append(returnType).append(" ");
        signature.append(methodName);
        signature.append(parameters);
        signature.append(" ");
        signature.append(exceptions);

        return signature.toString();
    }

    private static String constructMethodParameters(Class[] parameterTypes) {
        int nParameters = parameterTypes.length;
        StringBuilder parameters = new StringBuilder();

        parameters.append("(");
        for (int i = 0; i < nParameters; i++) {
            parameters.append(parameterTypes[i].getCanonicalName()).append(" ");
            parameters.append("arg").append(i);
            if (i < nParameters - 1) {
                parameters.append(",").append(" ");
            }
        }
        parameters.append(")");

        return parameters.toString();
    }

    private static String constructMethodExceptions(Class[] exceptionTypes) {
        int nExceptions = exceptionTypes.length;
        if (nExceptions > 0) {
            StringBuilder exceptions = new StringBuilder();

            exceptions.append("throws").append(" ");
            for (int i = 0; i < nExceptions; i++) {
                exceptions.append(exceptionTypes[i].getCanonicalName());
                if (i < nExceptions - 1) {
                    exceptions.append(",").append(" ");
                }
            }
            exceptions.append(" ");

            return exceptions.toString();
        } else {
            return "";
        }
    }

    private static String constructMethodBody(MethodSignature method) {
        Class methodReturnType = method.getReturnType();
        if (!methodReturnType.equals(void.class)) {
            String defaultValue = (getDefaultValueForReturnType(methodReturnType));
            StringBuilder returnStatement = new StringBuilder();

            returnStatement.append(singleShift);
            returnStatement.append("return").append(" ");
            returnStatement.append(defaultValue);
            returnStatement.append(";");

            return returnStatement.toString();
        } else {
            return "";
        }
    }

    private static String getDefaultValueForReturnType(Class returnType) {
        if (returnType.isPrimitive()) {
            if (returnType.equals(boolean.class)) {
                return "false";
            } else {
                return "0";
            }
        }
        return "null";
    }

    private static class MethodSignature {
        private final String methodName;
        private final Class returnType;
        private final Class[] parameterTypes;
        private final Class[] exceptionTypes;

        public MethodSignature(Method method) {
            this.methodName = method.getName();
            this.returnType = method.getReturnType();
            this.parameterTypes = method.getParameterTypes();
            this.exceptionTypes = method.getExceptionTypes();
        }

        public String getMethodName() {
            return methodName;
        }

        public Class getReturnType() {
            return returnType;
        }

        public Class[] getExceptionTypes() {
            return exceptionTypes;
        }

        public Class[] getParameterTypes() {
            return parameterTypes;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o != null && o instanceof MethodSignature) {
                MethodSignature e = (MethodSignature) o;
                return Arrays.equals(this.parameterTypes, e.parameterTypes)
                        && this.methodName.equals(e.methodName);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return methodName.hashCode();
        }
    }
}
