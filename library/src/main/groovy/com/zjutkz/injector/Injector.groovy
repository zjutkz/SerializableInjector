package com.zjutkz.injector

import javassist.ClassPool
import javassist.CtClass;

/**
 * Created by kangzhe on 17/7/13.
 */

public class Injector {

    private static final String SERIALIZABLE = "java.io.Serializable";
    private static ClassPool pool = ClassPool.getDefault()

    public static void injectPath(String path){
        pool.appendClassPath(path)
    }

    public static void injectDir(String path, String packageName,String packagePattern) {
        injectPath(path)
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                if (filePath.endsWith(".class")
                        && !filePath.contains('R$')
                        && !filePath.contains('R.class')
                        && !filePath.contains("BuildConfig.class")) {

                    System.out.println("filePath: " + filePath + " packageName: " + packageName + " packagePattern: " + packagePattern)
                    int index = filePath.indexOf(packageName);
                    boolean isMyPackage = index != -1;
                    if (isMyPackage && filePath.contains(packagePattern)) {
                        int end = filePath.length() - 6 // .class = 6
                        String className = filePath.substring(index, end).replace('\\', '.').replace('/', '.')
                        System.out.println("className: " + className)
                        CtClass c = pool.getCtClass(className)
                        if (c.isFrozen()) {
                            c.defrost()
                        }

                        CtClass seri = pool.getCtClass(SERIALIZABLE)
                        boolean hit = false
                        CtClass[] interfaces = c.getInterfaces()
                        for(CtClass interfaceClz :interfaces){
                            if(seri == interfaceClz){
                                hit = true
                                break
                            }
                        }
                        if(!hit){
                            System.out.println("====== not hit ======")
                            c.addInterface(seri)
                            c.writeFile(path)
                        }
                    }
                }
            }
        }
    }
}