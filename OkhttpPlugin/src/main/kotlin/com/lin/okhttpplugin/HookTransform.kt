package com.sf.plugin.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.ide.common.internal.WaitableExecutor
import com.google.common.io.Files
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import java.io.File
import java.io.IOException
import java.util.*

/**
 * @Copyright (C), 2019-2020, 顺丰科技有限公司
 * @ClassName: HookTransform
 * @Author: chenlong 01401581
 * @CreateDate: 2022/11/25
 * @Description: Transform操作基类
 */
open class HookTransform constructor(private val project: Project) : Transform() {

    private val SCOPES: MutableSet<QualifiedContent.Scope> = mutableSetOf()

    private val TRANSFORM_NAME = "sfNetworkTrackTransform"

    init {
        SCOPES.add(QualifiedContent.Scope.PROJECT)
        SCOPES.add(QualifiedContent.Scope.SUB_PROJECTS)
        SCOPES.add(QualifiedContent.Scope.EXTERNAL_LIBRARIES)
    }


    /**
     * 支持并发编译操作
     */
    private var waitableExecutor = WaitableExecutor.useGlobalSharedThreadPool()


    /**
     * 是否空运行（没有修改任何文件）
     */
    private var emptyRun = false

    /**
     * transform 名字
     */
    override fun getName(): String {
        return TRANSFORM_NAME
    }

    /**
     * 输入文件类型为class文件
     */
    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 指定transform作用范围为（这里为工程，子工程和依赖的模块）
     */
    override fun getScopes(): MutableSet<in QualifiedContent.Scope>? {
        return SCOPES
    }

    /**
     * 是否支持增量（这里默认为true）
     */
    override fun isIncremental(): Boolean {
        return true
    }

    /**
     * transform执行
     */
    @Throws(IOException::class, TransformException::class, InterruptedException::class)
    override fun transform(transformInvocation: TransformInvocation) {
        println("transform")


        project.logger.warn(
            name + " isIncremental = " + transformInvocation.isIncremental + ", runVariant = "
                    + "runVariant" + ", emptyRun = " + emptyRun + ", inDuplcatedClassSafeMode = " + inDuplcatedClassSafeMode()
        )

        val startTime = System.currentTimeMillis()

        //非增量编译删除掉旧的输出内容
        if (!transformInvocation.isIncremental) {
            transformInvocation.outputProvider.deleteAll()
        }


        var flagForCleanDexBuilderFolder = false

        //遍历输入文件
        for (input: TransformInput in transformInvocation.inputs) {
            //遍历项目中引入的第三方JAR包代码
            for (jarInput: JarInput in input.jarInputs) {

                val status = jarInput.status

                //目标文件位置
                val dest = transformInvocation.outputProvider.getContentLocation(
                    jarInput.file.absolutePath,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )

                if (transformInvocation.isIncremental && !emptyRun) {

                    when (status) {

                        //当前文件不需要处理
                        Status.NOTCHANGED -> {

                        }
                        //正常处理，输出给下一个任务（增加或者改变的文件）
                        Status.ADDED, Status.CHANGED -> transformJar(jarInput.file, dest, status)

                        //删除的文件
                        Status.REMOVED -> if (dest.exists()) {
                            FileUtils.forceDelete(dest)
                        }
                    }
                } else {
                    if (inDuplcatedClassSafeMode() and !transformInvocation.isIncremental && !flagForCleanDexBuilderFolder) {
                        cleanDexBuilderFolder(dest)
                        flagForCleanDexBuilderFolder = true
                    }
                    transformJar(jarInput.file, dest, status)
                }
            }

            //项目中编写的代码
            for (directoryInput: DirectoryInput in input.directoryInputs) {

                //目标文件夹
                val dest = transformInvocation.outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes, directoryInput.scopes,
                    Format.DIRECTORY
                )

                //创建目标文件夹
                FileUtils.forceMkdir(dest)

                if (transformInvocation.isIncremental && !emptyRun) {
                    val srcDirPath = directoryInput.file.absolutePath
                    val destDirPath = dest.absolutePath
                    val fileStatusMap = directoryInput.changedFiles
                    for (changedFile: Map.Entry<File, Status> in fileStatusMap.entries) {
                        val status = changedFile.value
                        val inputFile = changedFile.key
                        val destFilePath = inputFile.absolutePath.replace(srcDirPath, destDirPath)
                        val destFile = File(destFilePath)
                        when (status) {
                            Status.NOTCHANGED -> {
                            }
                            Status.REMOVED -> if (destFile.exists()) {
                                destFile.delete()
                            }
                            Status.ADDED, Status.CHANGED -> {
                                try {
                                    FileUtils.touch(destFile)
                                } catch (e: IOException) {
                                    Files.createParentDirs(destFile)
                                }
                                transformSingleFile(inputFile, destFile, srcDirPath)
                            }
                        }
                    }
                } else {
                    transformDir(directoryInput.file, dest)
                }
            }
        }

        // 等待所有异步并发任务结束
        waitableExecutor.waitForTasksWithQuickFail<Any>(true)

        project.logger.warn((name + "Transform 操作耗时:${System.currentTimeMillis() - startTime}  ms"))
    }

    /**
     * 对普通文件进行Transform操作
     */
    private fun transformSingleFile(inputFile: File, outputFile: File, srcBaseDir: String) {
        // 异步并发处理普通文件
        waitableExecutor.execute {
            project.logger.warn((name + inputFile.name))
//            bytecodeWeaver.weaveSingleClassToFile(inputFile, outputFile, srcBaseDir)
            null
        }
    }


    /**
     * 对目录进行Transform操作
     */
    @kotlin.jvm.Throws(IOException::class)
    private fun transformDir(inputDir: File, outputDir: File) {
        if (emptyRun) {
            FileUtils.copyDirectory(inputDir, outputDir)
            return
        }
        val inputDirPath: String = inputDir.absolutePath
        val outputDirPath: String = outputDir.absolutePath
        if (inputDir.isDirectory) {
            for (file: File in com.android.utils.FileUtils.getAllFiles(inputDir)) {
                //异步并发处理目录文件
                waitableExecutor.execute<Any> {
                    val filePath: String = file.absolutePath
                    val outputFile = File(filePath.replace(inputDirPath, outputDirPath))
                    project.logger.warn((name + filePath))
//                    bytecodeWeaver.weaveSingleClassToFile(file, outputFile, inputDirPath)
                    null
                }
            }
        }
    }

    /**
     * 对jar包进行transform操作
     */
    private fun transformJar(srcJar: File, destJar: File, status: Status) {
        //异步并发处理jar文件
        waitableExecutor.execute {
            if (emptyRun) {
                //将文件copy到目标目录下面
                FileUtils.copyFile(srcJar, destJar)
                return@execute null
            }
//            bytecodeWeaver.weaveJar(srcJar, destJar)
            null
        }
    }

    /**
     * 删除掉DexBuilder文件夹
     */
    private fun cleanDexBuilderFolder(dest: File) {
        //异步并发处理DexBuilder
        waitableExecutor.execute<Any> {
            try {
                val dexBuilderDir: String = replaceLastPart(dest.absolutePath, name, "dexBuilder")
                val file: File = File(dexBuilderDir).parentFile
                project.logger.warn("clean dexBuilder folder = " + file.absolutePath)
                if (file.exists() && file.isDirectory) {
                    com.android.utils.FileUtils.deleteDirectoryContents(file)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            null
        }
    }

    private fun replaceLastPart(originString: String, replacement: String, toReplace: String): String {
        val start: Int = originString.lastIndexOf(replacement)
        val builder: java.lang.StringBuilder = java.lang.StringBuilder()
        builder.append(originString.substring(0, start))
        builder.append(toReplace)
        builder.append(originString.substring(start + replacement.length))
        return builder.toString()
    }

    override fun isCacheable(): Boolean {
        return true
    }


    protected open fun inDuplcatedClassSafeMode(): Boolean {
        return false
    }
}