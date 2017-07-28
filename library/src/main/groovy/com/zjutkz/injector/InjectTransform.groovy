package com.zjutkz.injector

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 * Created by kangzhe on 17/7/13.
 */


public class InjectTransform extends Transform{

    private static final String BASE_PACKAGE = "com/vdian/tuwen"

    Project project

    public InjectTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "SerializableInjector"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

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

        def sdkDir
        Properties properties = new Properties()
        properties.load(project.rootProject.file('local.properties').newDataInputStream())
        if (System.getenv("ANDROID_HOME") != null) {
            sdkDir = System.getenv("ANDROID_HOME")
        } else {
            sdkDir = properties.getProperty('sdk.dir')
        }
        def androidJar = "${sdkDir}/platforms/${project.android.compileSdkVersion}/android.jar"
        Injector.injectPath(androidJar)

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
                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }

}
