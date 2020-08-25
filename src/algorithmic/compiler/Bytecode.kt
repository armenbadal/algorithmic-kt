package algorithmic.compiler

import algorithmic.engine.Program
import org.apache.bcel.Const
import org.apache.bcel.generic.ClassGen
import org.apache.bcel.generic.InstructionFactory

class Bytecode constructor(private val program: Program) {

    private val classGenerator: ClassGen = createClassGenerator()
    private val constantPool = classGenerator.constantPool
    private val factory = InstructionFactory(classGenerator, constantPool)

    private fun createClassGenerator(): ClassGen
    {
        val fileName = program.name
        val className = fileName.substring(0, fileName.lastIndexOf('.'))

        val flags = Const.ACC_PUBLIC.toInt() or Const.ACC_SUPER.toInt()
        return ClassGen(className, "java.lang.Object", fileName, flags, arrayOf())
    }
}