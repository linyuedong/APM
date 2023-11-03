package com.lin.okhttpplugin;

import org.gradle.api.Plugin
import org.gradle.api.Project

class TestPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {

        println("======自定义MainPlugin加载===========")
        //执行自定义的  task
        project.task("TestPluginTask"){
            doLast {
                println("自定义插件task 任务执行")
            }
        }
    }
}
