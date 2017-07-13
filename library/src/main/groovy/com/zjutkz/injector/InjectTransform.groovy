package com.zjutkz.injector

import org.gradle.api.Project

import javax.xml.crypto.dsig.TransformException

/**
 * Created by kangzhe on 17/7/13.
 */


public class InjectTransform extends Transform{

    private static final String BASE_PACKAGE = "com.vdian.tuwen"
    Project project

    // 构造函数，我们将Project保存下来备用
    public MyTransform(Project project) {
        this.project = project
    }

    // 设置我们自定义的Transform对应的Task名称
    @Override
    String getName() {
        return "SerializableInjector"
    }

    // 指定输入的类型，通过这里的设定，可以指定我们要处理的文件类型
    //这样确保其他类型的文件不会传入
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    // 指定Transform的作用范围
    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {

        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                Injector.injectDir(directoryInput.file.absolutePath,BASE_PACKAGE)
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)

                FileUtils.copyDirectory(directoryInput.file, dest)
            }
            input.jarInputs.each { JarInput jarInput ->
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                //生成输出路径
                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }

}
