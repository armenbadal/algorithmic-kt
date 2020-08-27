package algorithmic.compiler

import algorithmic.engine.*
import algorithmic.engine.Type
import org.apache.bcel.Const
import org.apache.bcel.generic.*

typealias BCELType = org.apache.bcel.generic.Type;

class ByteCode(private val program: Program) {
    // սկզբնական ծրագրի ֆայլի անունը
    private val fileName = program.name
    // ստեղծվելիք դասի անունը
    private val className = fileName.substring(0, fileName.lastIndexOf('.'))

    private lateinit var classGenerator: ClassGen
    private lateinit var constantPool: ConstantPoolGen
    private lateinit var factory: InstructionFactory
    private lateinit var instructionList: InstructionList

    private lateinit var methodGenerator: MethodGen
    private val nameIndieces = hashMapOf<String,Int>()

    fun compile(/* TODO: add parameter for output file */)
    {
        val flags = Const.ACC_PUBLIC.toInt() or Const.ACC_SUPER.toInt()
        classGenerator = ClassGen(className, "java.lang.Object", fileName, flags, arrayOf())
        constantPool = classGenerator.constantPool
        instructionList = InstructionList()

        program.algorithms.forEach { code(it) }

        classGenerator.getJavaClass().dump("$fileName.class")
    }

    private fun code(alg: Algorithm)
    {
        val access = Const.ACC_STATIC.toInt() or Const.ACC_PUBLIC.toInt()
        val parTypes = alg.parameters.map { bcelType(it.type) }
        val parNames = alg.parameters.map { it.id }
        methodGenerator = MethodGen(access, bcelType(alg.returnType),
                parTypes.toTypedArray(), parNames.toTypedArray(),
                alg.name, "HelloWorld", instructionList, constantPool)
        factory = InstructionFactory(classGenerator, constantPool)

        // վերցնել մեթոդի պարամետրերի ինդեքսները
        nameIndieces.clear()
        for( vr in methodGenerator.localVariables )
            nameIndieces[vr.name] = vr.index

        // հայտարարել լոկալ փոփոխականները
        for( vr in alg.locals ) {
            val ty = bcelType(vr.type)
            val lv = methodGenerator.addLocalVariable(vr.id, ty, null, null)
            nameIndieces[vr.id] = lv.index
            if( Type.REAL == vr.type ) {
                instructionList.append(PUSH(constantPool, 0.0))
                instructionList.append(DSTORE(lv.index))
            }
            else if( Type.TEXT == vr.type ) {
                instructionList.append(InstructionConst.ACONST_NULL)
                instructionList.append(ASTORE(lv.index))
            }
        }

        code(alg.body)

        methodGenerator.setMaxStack()
        methodGenerator.setMaxLocals()

        classGenerator.addMethod(methodGenerator.method)
        instructionList.dispose()
    }

    private fun code(stat: Statement)
    {
        when( stat ) {
            is Sequence -> code(stat)
            is Assignment -> code(stat)
            is Result -> code(stat)
            else -> throw CompileError("Չիրականացված թարգմանություն։")
        }
    }

    private fun code(seq: Sequence)
    {
        seq.items.forEach { code(it) }
    }

    private fun code(asg: Assignment)
    {
        code(asg.value)
        val ix = nameIndieces[asg.sym.id]!!
        if( Type.REAL == asg.sym.type )
            instructionList.append(DSTORE(ix))
        else if( Type.TEXT == asg.sym.type )
            instructionList.append(ASTORE(ix))
    }

    private fun code(rs: Result)
    {
        code(rs.value)
        //instructionList.append(factory.createReturn)
    }

    private fun code(expr: Expression)
    {
        when( expr ) {
            is Binary -> code(expr)
            is Unary -> code(expr)
            is Apply -> code(expr)
            is Text -> code(expr)
            is Numeric -> code(expr)
            is Variable -> code(expr)
        }
    }

    private fun code(bi: Binary)
    {}

    private fun code(un: Unary)
    {}

    private fun code(ap: Apply)
    {}

    private fun code(tx: Text)
    {}

    private fun code(nm: Numeric)
    {
        instructionList.append(PUSH(constantPool, nm.value))
    }

    private fun code(vr: Variable)
    {
        if( vr.sym.type == Type.REAL )
            instructionList.append(DLOAD(nameIndieces[vr.sym.id]!!))
        else if( vr.sym.type == Type.TEXT )
            instructionList.append(ALOAD(nameIndieces[vr.sym.id]!!))
    }

    private fun bcelType(type: Type): BCELType =
        when( type ) {
            Type.VOID -> BCELType.VOID
            Type.TEXT -> BCELType.STRING
            Type.REAL -> BCELType.DOUBLE
        }
}