package com.zjutkz.injector

import org.gradle.api.Plugin
import org.gradle.api.Project;

/**
 * Created by kangzhe on 17/7/13.
 */

public class InjectPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def isApp = project.plugins.hasPlugin(AppPlugin)
        if (isApp) {
            def android = project.extensions.findByType(AppExtension)
            android.registerTransform(new InjectTransform(project))
        }
    }
}


