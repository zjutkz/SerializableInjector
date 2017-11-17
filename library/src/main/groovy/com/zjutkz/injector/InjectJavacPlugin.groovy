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
        variants.all { variant ->
            JavaCompile javaCompile = (JavaCompile) (variant.hasProperty('javaCompiler') ? variant.javaCompiler : variant.javaCompile)
            javaCompile.doLast {
                InjectorExtension injectorExtension = project.getExtensions().findByType(InjectorExtension.class)
                Injector.injectDir(javaCompile.getDestinationDir(),injectorExtension.packageName,injectorExtension.packagePattern)
            }
        }
    }
}
