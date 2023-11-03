package com.lin.okhttpplugin.visitor

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MyMethodVisitor(methodVisitor: MethodVisitor?) : MethodVisitor(Opcodes.ASM5, methodVisitor) {

    override fun visitCode() {
        super.visitCode()
    }

    override fun visitEnd() {
        super.visitEnd()
    }
}