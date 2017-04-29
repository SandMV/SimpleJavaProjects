package ru.compscicenter.java2016.implementor;

/**
 * Created by sandulmv on 27.11.16.
 */

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

public class SimpleImplementor implements Implementor {
    static final String SUCCESSOR_SUFFIX = "Impl";
    private final String workingDirectory;

    public SimpleImplementor(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public String implementFromDirectory(String path, String className) throws ImplementorException {
        Class ancestor = getClassFromDirectory(path, className);
        String packageName = getPackageName(ancestor);
        String successor = SuccessorConstructor.constructSuccessor(ancestor);
        successor = addPackage(successor, packageName);
        String shortSuccessorName = getShortSuccessorName(className);
        writeClass(successor, shortSuccessorName, packageName);
        return getFullSuccessorName(className);
    }

    @Override
    public String implementFromStandardLibrary(String className) throws ImplementorException {
        try {
            Class ancestor = Class.forName(className);
            String successor = SuccessorConstructor.constructSuccessor(ancestor);
            writeClass(successor, getShortSuccessorName(className), "");
            return getShortSuccessorName(className);
        } catch (ClassNotFoundException e) {
            throw new ImplementorException("Wrong class name:" + className, e);
        }
    }

    private String getPackageName(Class ancestor) {
        Package pkg = ancestor.getPackage();
        String packageName = "";
        if (pkg != null) {
            packageName = pkg.getName();
        }
        return packageName;
    }

    private String getFullSuccessorName(String ancestorName) {
        return ancestorName + SUCCESSOR_SUFFIX;
    }

    private String getShortSuccessorName(String ancestorName) {
        int lastPoint = ancestorName.lastIndexOf(".");
        return ancestorName.substring(lastPoint + 1) + SUCCESSOR_SUFFIX;
    }

    private String addPackage(String successor, String packageName) {
        if (!packageName.equals("")) {
            StringBuilder successorWithPackage = new StringBuilder();

            successorWithPackage.append("package").append(" ");
            successorWithPackage.append(packageName).append(";");
            successorWithPackage.append("\n");
            successorWithPackage.append(successor);

            successor = successorWithPackage.toString();
        }
        return successor;
    }

    private Class getClassFromDirectory(String path, String className) throws ImplementorException {
        try {
            URL classFileURL = Paths.get(path).toUri().toURL();
            ClassLoader classLoader = new URLClassLoader(new URL[] {classFileURL});
            Class loadedClass = classLoader.loadClass(className);
            return loadedClass;
        } catch (Exception e) {
            throw new ImplementorException("Cannot load the class: " + className, e);
        }
    }

    private void writeClass(String successor, String shortName, String successorPkg) throws ImplementorException {
        try {
            String path = workingDirectory + File.separator;
            if (successorPkg.length() > 0) {
                path += successorPkg.replaceAll("\\.", File.separator) + File.separator;
            }
            path += shortName + ".java";
            File outputDirectory = new File(path);
            boolean dirs = outputDirectory.getParentFile().mkdirs();
            boolean file = outputDirectory.createNewFile();
            if (!file && !dirs) {
                throw new ImplementorException("Unable to create file");
            }
            try (PrintWriter writeClass = new PrintWriter(outputDirectory)) {
                writeClass.print(successor);
            }
        } catch (Exception e) {
            throw new ImplementorException("Cannot write class", e);
        }
    }
}
