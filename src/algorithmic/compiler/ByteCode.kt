package algorithmic.compiler

import algorithmic.engine.*
import algorithmic.engine.Type
import org.apache.bcel.Const
import org.apache.bcel.generic.*

typealias BCELType = org.apache.bcel.generic.Type;

class ByteCode(private val program: Program) {
//    private data class ProgramCodeContext(
//        val classGenerator: ClassGen,
//        val constantPoolGen: ConstantPoolGen,
//        val instructionList: InstructionList
//    )
//    private data class MethodCodeContext(
//        val methodGenerator: MethodGen,
//        val instructionFactory: InstructionFactory
//    )

    // սկզբնական ծրագրի ֆայլի անունը
    private val fileName: String = program.name

    // ստեղծվելիք դասի անունը
    private val className = fileName.substring(0, fileName.lastIndexOf('.'))

    private lateinit var classGenerator: ClassGen
    private lateinit var constantPool: ConstantPoolGen
    private lateinit var factory: InstructionFactory
    private lateinit var instructionList: InstructionList

    private lateinit var methodGenerator: MethodGen
    private val nameIndices = hashMapOf<String, Int>()

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
        nameIndices.clear()
        for( vr in methodGenerator.localVariables )
            nameIndices[vr.name] = vr.index

        // հայտարարել լոկալ փոփոխականները
        for( vr in alg.locals ) {
            val ty = bcelType(vr.type)
            val lv = methodGenerator.addLocalVariable(vr.id, ty, null, null)
            nameIndices[vr.id] = lv.index
            if( Type.REAL == vr.type )
                instructionList.append(PUSH(constantPool, 0.0))
            else if( Type.TEXT == vr.type )
                instructionList.append(InstructionConst.ACONST_NULL)
            instructionList.append(InstructionFactory.createStore(ty, lv.index))
        }

        // մեթոդի մարմինը
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
            is Branching -> code(stat)
            is Repetition -> code(stat)
            is Result -> code(stat)
            is Call -> code(stat)
        }
    }

    private fun code(seq: Sequence)
    {
        seq.items.forEach { code(it) }
    }

    private fun code(asg: Assignment)
    {
        code(asg.value)
        val ix = nameIndices[asg.sym.id]!!
        if( Type.REAL == asg.sym.type )
            instructionList.append(DSTORE(ix))
        else if( Type.TEXT == asg.sym.type )
            instructionList.append(ASTORE(ix))
    }

    private fun code(bra: Branching)
    {}

    private fun code(rep: Repetition)
    {}

    private fun code(rs: Result)
    {
        code(rs.value)
        if( rs.value.type == Type.REAL )
            instructionList.append(InstructionConst.DRETURN)
        else if( rs.value.type == Type.TEXT )
            instructionList.append(InstructionConst.ARETURN)
    }

    private fun code(cl: Call)
    {
        cl.arguments.forEach { code(it) }
        val aty = cl.callee.parametersTypes.map { bcelType(it) }
        val inv = factory.createInvoke(className, cl.callee.name,
                bcelType(cl.callee.resultType), aty.toTypedArray(),
                Const.INVOKESTATIC)
        instructionList.append(inv)
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
    {
        code(bi.left)
        code(bi.right)
        val inst = when( bi.operation ) {
            Operation.ADD -> InstructionConst.DADD
            Operation.SUB -> InstructionConst.DSUB
            Operation.MUL -> InstructionConst.DMUL
            Operation.DIV -> InstructionConst.DDIV
            else -> InstructionConst.NOP
        }
        instructionList.append(inst)
    }

    private fun code(un: Unary)
    {
        code(un.right)
        if( un.operation == Operation.SUB )
            instructionList.append(InstructionConst.DNEG)
    }

    private fun code(ap: Apply)
    {
        ap.arguments.forEach { code(it) }
        val aty = ap.callee.parametersTypes.map { bcelType(it) }
        val inv = factory.createInvoke(className, ap.callee.name,
                bcelType(ap.callee.resultType), aty.toTypedArray(),
                Const.INVOKESTATIC)
        instructionList.append(inv)
    }

    private fun code(tx: Text)
    {
        instructionList.append(PUSH(constantPool, tx.value))
    }

    private fun code(nm: Numeric)
    {
        instructionList.append(PUSH(constantPool, nm.value))
    }

    private fun code(vr: Variable)
    {
        val ix = nameIndices[vr.sym.id]!!
        var ld = InstructionConst.NOP
        if( vr.sym.type == Type.REAL )
            ld = InstructionFactory.createLoad(BCELType.DOUBLE, ix)
        else if( vr.sym.type == Type.TEXT )
            ld = InstructionFactory.createLoad(BCELType.STRING, ix)
        instructionList.append(ld)
    }

    private fun bcelType(type: Type): BCELType =
        when( type ) {
            Type.VOID -> BCELType.VOID
            Type.TEXT -> BCELType.STRING
            Type.REAL -> BCELType.DOUBLE
        }
}
