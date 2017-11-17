package com.zjutkz.injector

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
/**
 * Created by kangzhe on 17/7/13.
 */

public class InjectPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("injectorConfig",InjectorExtension)
        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(new InjectTransform(project))
    }
}


