package com.lin.okhttpplugin.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MyClassVisitor(classVisitor: ClassVisitor?) : ClassVisitor(Opcodes.ASM5, classVisitor) {

    private var className:String? = null
    private var superName:String? = null

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        this.className = name
        this.superName = superName
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {

        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }

}