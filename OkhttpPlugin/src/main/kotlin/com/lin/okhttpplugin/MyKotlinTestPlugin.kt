package com.lin.okhttpplugin
import com.android.build.gradle.AppExtension
import com.sf.plugin.transform.HookTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*


class MyKotlinTestPlugin:Plugin<Project> {

    override fun apply(project: Project) {
        println("======自定义MyKotlinTestPlugin加载===========")

        val asmTransform = project.extensions.getByType(AppExtension::class.java)
        asmTransform.registerTransform(OkhttpTransform(project))
//        asmTransform.registerTransform(HookTransform(project))

    }

}
