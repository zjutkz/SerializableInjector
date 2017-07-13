package com.zjutkz.injector

import javassist.ClassPool
import javassist.CtClass;

/**
 * Created by kangzhe on 17/7/13.
 */

public class Injector {

    private static final String SERIALIZABLE = "java.io.Serializable";
    private static ClassPool pool = ClassPool.getDefault()

    public static void injectDir(String path, String packageName) {
        pool.appendClassPath(path)
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                if (filePath.endsWith(".class")
                        && !filePath.contains('R$')
                        && !filePath.contains('R.class')
                        && !filePath.contains("BuildConfig.class")) {

                    int index = filePath.indexOf(packageName);
                    boolean isMyPackage = index != -1;
                    if (isMyPackage) {
                        int end = filePath.length() - 6 // .class = 6
                        String className = filePath.substring(index, end).replace('\\', '.').replace('/', '.')
                        CtClass c = pool.getCtClass(className)
                        if (c.isFrozen()) {
                            c.defrost()
                        }

                        boolean hit = false
                        CtClass[] interfaces = c.getInterfaces()
                        for(CtClass interfaceClz :interfaces){
                            if(SERIALIZABLE == interfaceClz){
                                hit = true
                                break
                            }
                        }
                        if(!hit){
                            c.addInterface(pool.getCtClass(SERIALIZABLE))
                        }
                    }
                }
            }
        }
    }

}
