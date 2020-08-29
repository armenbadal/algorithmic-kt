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
    private lateinit var instructions: InstructionList

    private lateinit var methodGenerator: MethodGen
    private val nameIndices = hashMapOf<String, Int>()

    fun compile(/* TODO: add parameter for output file */)
    {
        val flags = Const.ACC_PUBLIC.toInt() or Const.ACC_SUPER.toInt()
        classGenerator = ClassGen(className, "java.lang.Object", fileName, flags, arrayOf())
        constantPool = classGenerator.constantPool
        instructions = InstructionList()

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
                alg.name, "HelloWorld", instructions, constantPool)
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
                instructions.append(factory.createConstant(0.0))
            else if( Type.TEXT == vr.type )
                instructions.append(InstructionConst.ACONST_NULL)
            instructions.append(InstructionFactory.createStore(ty, lv.index))
        }

        // մեթոդի մարմինը
        code(alg.body)

        methodGenerator.setMaxStack()
        methodGenerator.setMaxLocals()

        classGenerator.addMethod(methodGenerator.method)
        instructions.dispose()
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
        instructions.append(InstructionFactory.createStore(bcelType(asg.sym.type), ix))
    }

    private fun code(bra: Branching)
    {
        // ԵԹԵ և ԻՍԿ ԵԹԵ ճյուղերից ցիկլի վերջին անցման հղումներ
        val gofis = arrayListOf<BranchInstruction>()
        var p: Statement = bra
        while( p is Branching ) {
            // ճյուղավորման պայմանը
            code(p.condition)
            val bri = createIfJump(Const.IFEQ, null) // ?
            instructions.append(bri)
            // then ճյուղը
            code(p.decision)
            val fi = createGoto(null)
            instructions.append(fi)
            gofis.add(fi)
            bri.target = instructions.append(createNop())
            // անցում else ճյուղին
            p = p.alternative
        }

        // վերջին else բլոկը
        if( p is Sequence )
            if( !p.items.isEmpty() )
                code(p)

        // ճյուղավորման վերջը
        val endif = instructions.append(createNop())
        gofis.forEach { e -> e.target = endif }
    }

    private fun code(rep: Repetition)
    {
        // կրկնման սկիզբը
        val bg = instructions.append(createNop())
        // պայման, որը ստուգվում է ամեն կրկնությունից առաջ
        code(rep.condition)
        val bri = createIfJump(Const.IFEQ, null)
        instructions.append(bri)

        // կրկնության մարմինը
        code(rep.body)

        // անպայման անցում կրկնման սկզբին
        instructions.append(createGoto(bg))
        // կրկնման մարմնից դուրս
        bri.target = instructions.append(createNop())
    }

    private fun code(rs: Result)
    {
        code(rs.value)
        instructions.append(InstructionFactory.createReturn(bcelType(rs.value.type)))
    }

    private fun code(cl: Call)
    {
        cl.apply.arguments.forEach { code(it) }
        val aty = cl.apply.callee.parametersTypes.map { bcelType(it) }
        val inv = factory.createInvoke(className, cl.apply.callee.name,
                bcelType(cl.apply.callee.resultType), aty.toTypedArray(),
                Const.INVOKESTATIC)
        instructions.append(inv)
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

        when( bi.operation ) {
            in Operation.ADD..Operation.DIV -> arithmetic(bi.operation)
            in Operation.EQ..Operation.LE -> comparison(bi.operation)
            in Operation.AND..Operation.OR -> logical(bi.operation)
            else -> {}
        }
    }

    private fun arithmetic(oper: Operation)
    {
        val inst = when( oper ) {
            Operation.ADD -> InstructionConst.DADD
            Operation.SUB -> InstructionConst.DSUB
            Operation.MUL -> InstructionConst.DMUL
            Operation.DIV -> InstructionConst.DDIV
            else -> InstructionConst.NOP
        }
        instructions.append(inst)
    }

    private fun comparison(oper: Operation)
    {
        instructions.append(InstructionConst.DCMPL)

        val opcode = when( oper ) {
            Operation.EQ -> Const.IFNE
            Operation.NE -> Const.IFEQ
            Operation.GT -> Const.IFLE
            Operation.GE -> Const.IFLT
            Operation.LT -> Const.IFGE
            Operation.LE -> Const.IFGT
            else -> 0
        }

        val bri = createIfJump(opcode, null)
        instructions.append(bri)
        instructions.append(factory.createConstant(1))
        val go = createGoto(null)
        instructions.append(go)
        bri.target = instructions.append(factory.createConstant(0))
        go.target = instructions.append(createNop())
    }

    private fun logical(oper: Operation)
    {}

    private fun code(un: Unary)
    {
        code(un.right)
        if( un.operation == Operation.SUB )
            instructions.append(InstructionConst.DNEG)
    }

    private fun code(ap: Apply)
    {
        ap.arguments.forEach { code(it) }
        val aty = ap.callee.parametersTypes.map { bcelType(it) }
        val inv = factory.createInvoke(className, ap.callee.name,
                bcelType(ap.callee.resultType), aty.toTypedArray(),
                Const.INVOKESTATIC)
        instructions.append(inv)
    }

    private fun code(tx: Text)
    {
        instructions.append(factory.createConstant(tx.value))
    }

    private fun code(nm: Numeric)
    {
        instructions.append(factory.createConstant(nm.value))
    }

    private fun code(vr: Variable)
    {
        val ix = nameIndices[vr.sym.id]!!
        instructions.append(InstructionFactory.createLoad(bcelType(vr.sym.type), ix))
    }

    private fun createGoto(target: InstructionHandle?) =
        InstructionFactory.createBranchInstruction(Const.GOTO, target)

    private fun createIfJump(opcode: Short, target: InstructionHandle?) =
        InstructionFactory.createBranchInstruction(opcode, target)

    private fun createNop() =
        InstructionFactory.createNull(BCELType.VOID)

    private fun bcelType(type: Type): BCELType =
        when( type ) {
            Type.VOID -> BCELType.VOID
            Type.TEXT -> BCELType.STRING
            Type.REAL -> BCELType.DOUBLE
        }
}
