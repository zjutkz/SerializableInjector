package com.zjutkz.injector;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.LibraryExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile;

/**
 * Created by kangzhe on 17/11/17.
 */

public class InjectJavacPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.extensions.create("injectorConfig",InjectorExtension.class)

        def variants
        if (project.plugins.hasPlugin(AppPlugin)) {
            AppExtension android = project.extensions.getByType(AppExtension.class)
            variants = android.getApplicationVariants()
        } else {
            LibraryExtension android = project.extensions.getByType(LibraryExtension.class)
            variants = android.getLibraryVariants()
        }

        def sdkDir
        Properties properties = new Properties()
        File local = project.rootProject.file('local.properties')
        if(local.exists()){
            properties.load(local.newDataInputStream())
        }
        if (System.getenv("ANDROID_HOME") != null) {
            sdkDir = System.getenv("ANDROID_HOME")
        } else {
            sdkDir = properties.getProperty('sdk.dir')
        }
        def androidJar = "${sdkDir}/platforms/${project.android.compileSdkVersion}/android.jar"
        println("android sdk version: " + project.android.compileSdkVersion)
        Injector.injectPath(androidJar)

        variants.all { variant ->
            JavaCompile javaCompile = (JavaCompile) (variant.hasProperty('javaCompiler') ? variant.javaCompiler : variant.javaCompile)
            javaCompile.doLast {
                InjectorExtension injectorExtension = project.getExtensions().findByType(InjectorExtension.class)
                println("packageName: " + injectorExtension.packageName)
                println("packagePattern: " + injectorExtension.packagePattern)
                Injector.injectDir(javaCompile.getDestinationDir().toString(),injectorExtension.packageName,injectorExtension.packagePattern)
            }
        }
    }
}
