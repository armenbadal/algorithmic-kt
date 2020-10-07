package algorithmic.compiler

import algorithmic.ast.*
import algorithmic.ast.Type
import org.apache.bcel.Const
import org.apache.bcel.generic.*
import java.nio.file.Path
import java.nio.file.Paths

class ByteCode(private val program: Program) {
    // ստեղծվելիք դասի անունը
    private val className = program.name

    private lateinit var classGenerator: ClassGen
    private lateinit var constantPool: ConstantPoolGen
    private lateinit var factory: InstructionFactory
    private lateinit var instructions: InstructionList

    private lateinit var methodGenerator: MethodGen
    private val nameIndices = hashMapOf<String, Int>()

    // Ալգորիթմական լեզվի տիպերի արտապատկերումը BCEL տիպերին։
    private val bcelType = mapOf(
        Type.VOID to org.apache.bcel.generic.Type.VOID,
        Type.TEXT to org.apache.bcel.generic.Type.STRING,
        Type.REAL to org.apache.bcel.generic.Type.DOUBLE,
        Type.BOOL to org.apache.bcel.generic.Type.BOOLEAN
    )


    fun compile(place: Path)
    {
        // դասը գեներացնող օբյեկտը
        classGenerator = ClassGen(
                className, // անունը
                "java.lang.Object", // բազային դասը
                "<generated>", // սկզբնական կոդի ֆայլի անունը
                Const.ACC_PUBLIC.toInt() or Const.ACC_SUPER.toInt(), // հասանելիությունը
                arrayOf()) // իրականացվոած ինտերֆեյսների ցուցակը
        constantPool = classGenerator.constantPool
        factory = InstructionFactory(classGenerator, constantPool)
        instructions = InstructionList()

        // գեներացնել ալգորիթմների կոդը
        program.algorithms.forEach { code(it) }

        // main մեթոդը
        entryPoint()

        val path = Paths.get(place.toString(), program.name + ".class")
        classGenerator.javaClass.dump(path.toString())
    }

    private fun entryPoint()
    {
        methodGenerator = MethodGen(
            Const.ACC_STATIC.toInt() or Const.ACC_PUBLIC.toInt(),
            bcelType[Type.VOID],
            arrayOf(ArrayType(bcelType[Type.TEXT], 1)),
            arrayOf("args"),
            "main",
            className,
            instructions,
            constantPool)

        code(program.body)
        instructions.append(InstructionFactory.createReturn(bcelType[Type.VOID]))

        methodGenerator.setMaxStack()
        methodGenerator.setMaxLocals()

        classGenerator.addMethod(methodGenerator.method)
        instructions.dispose()
    }

    /**
     * Ալգորիթմի համար բայթ-կոդի գեներացիան
     *
     * Ամեն մի ալգորիթմի համար ստեղծվում է նույն անունով `public`,
     * `static` մեթոդ։ Պարամետրերի, լոկալ անունների, ինչպես նաև
     * ալգորիթմի արդյունքի տիպը որոշվում է [bcelType] արտապատկերման
     * կանոններով։
     */
    private fun code(alg: Algorithm)
    {
        // հասանելիության որոշումը՝ public static
        val accessFlags = Const.ACC_STATIC.toInt() or Const.ACC_PUBLIC.toInt()
        // պարամետրերի տիպերի ցուցակը
        val parTypes = alg.parameters.map { bcelType[it.type] }
        // պարամետրերի անունների ցուցակը
        val parNames = alg.parameters.map { it.id }
        // մեթոդը կառուցող օբյեկտ
        methodGenerator = MethodGen(
            accessFlags,
            bcelType[alg.returnType],
            parTypes.toTypedArray(),
            parNames.toTypedArray(),
            alg.name,
            className,
            instructions,
            constantPool)

        // վերցնել մեթոդի պարամետրերի ինդեքսները
        nameIndices.clear()
        methodGenerator.localVariables.forEach { nameIndices[it.name] = it.index }

        // հայտարարել լոկալ փոփոխականները և վերագրել լռելության արժեքները
        for( vr in alg.locals ) {
            val ty = bcelType[vr.type]
            val lv = methodGenerator.addLocalVariable(vr.id, ty, null, null)
            nameIndices[vr.id] = lv.index
            when( vr.type ) {
                Type.REAL -> instructions.append(factory.createConstant(0.0))
                Type.TEXT -> instructions.append(factory.createConstant(""))
                Type.BOOL -> instructions.append(factory.createConstant(false))
                Type.VOID -> {}
            }
            instructions.append(InstructionFactory.createStore(ty, lv.index))
        }

        // մեթոդի մարմինը
        code(alg.body)
        if( alg.returnType == Type.VOID )
            instructions.append(InstructionFactory.createReturn(bcelType[Type.VOID]))

        methodGenerator.setMaxStack()
        methodGenerator.setMaxLocals()

        methodGenerator.removeNOPs()
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
        instructions.append(InstructionFactory.createStore(bcelType[asg.sym.type], ix))
    }

    private fun code(bra: Branching)
    {
        // ԵԹԵ և ԻՍԿ ԵԹԵ ճյուղերից ցիկլի վերջին անցման հղումներ
        val gofis = arrayListOf<BranchInstruction>()
        var p: Statement = bra
        while( p is Branching ) {
            // ճյուղավորման պայմանը
            code(p.condition)
            val bri = createIfJump(Const.IFEQ)
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
            if( p.items.isNotEmpty() )
                code(p)

        // ճյուղավորման վերջը
        val endif = instructions.append(createNop())
        gofis.forEach { e -> e.target = endif }
    }

    /**
     * Կրկնության կառուցվածքի թարգմանությունը
     *
     * Repetition(condition, body) =>
     * begin: nop
     *        code(condition)
     *        ifeq end
     *        code(body)
     *        goto begin
     *  end:  nop
     */
    private fun code(rep: Repetition)
    {
        // կրկնման սկիզբը
        val bg = instructions.append(createNop())
        // պայման, որը ստուգվում է ամեն կրկնությունից առաջ
        code(rep.condition)
        val bri = createIfJump(Const.IFEQ)
        instructions.append(bri)

        // կրկնության մարմինը
        code(rep.body)

        // անպայման անցում կրկնման սկզբին
        instructions.append(createGoto(bg))
        // կրկնման մարմնից դուրս
        bri.target = instructions.append(createNop())
    }

    /**
     * Ալգորիթմի արդյունքի որոշում։
     *
     * Վերադարձվելիք արժեքի տիպից կախված.
     *   dreturn
     *   ireturn
     *   areturn
     *   return
     */
    private fun code(rs: Result)
    {
        code(rs.value)
        instructions.append(InstructionFactory.createReturn(bcelType[rs.value.type]))
    }

    /**
     * Ալգորիթմի կիրառում։
     */
    private fun code(cl: Call)
    {
        code(cl.apply)
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
            is Logical -> code(expr)
        }
    }

    private fun code(bi: Binary)
    {
        when( bi.operation ) {
            in Operation.ADD..Operation.MOD -> arithmetic(bi)
            in Operation.EQ..Operation.LE -> comparison(bi)
            in Operation.AND..Operation.OR -> logical(bi)
            else -> {}
        }
    }

    private fun arithmetic(bi: Binary)
    {
        if( bi.operation == Operation.MOD ) {
            code(bi.left)
            instructions.append(InstructionConst.D2I)
            code(bi.right)
            instructions.append(InstructionConst.D2I)
            instructions.append(InstructionConst.IREM)
            instructions.append(InstructionConst.I2D)
        }
        else {
            code(bi.left)
            code(bi.right)
            instructions.append(InstructionFactory.createBinaryOperation(bi.operation.text, bcelType[Type.REAL]))
        }
    }

    private fun comparison(bi: Binary)
    {
        code(bi.left)
        code(bi.right)

        if (bi.left.type == Type.TEXT && bi.right.type == Type.TEXT) {
            val inv = factory.createInvoke(
                    "Algorithmic",
                    if( bi.operation == Operation.EQ ) "eq" else "ne",
                    bcelType[Type.BOOL],
                    arrayOf(bcelType[Type.TEXT], bcelType[Type.TEXT]),
                    Const.INVOKESTATIC)
            instructions.append(inv)
        }
        else {
            instructions.append(InstructionConst.DCMPL)
            val opcode = when (bi.operation) {
                Operation.EQ -> Const.IFNE
                Operation.NE -> Const.IFEQ
                Operation.GT -> Const.IFLE
                Operation.GE -> Const.IFLT
                Operation.LT -> Const.IFGE
                Operation.LE -> Const.IFGT
                else -> 0
            }

            val bri = instructions.append(createIfJump(opcode))
            instructions.append(factory.createConstant(1))
            val go = instructions.append(createGoto(null))
            bri.target = instructions.append(factory.createConstant(0))
            go.target = instructions.append(createNop())
        }
    }

    private fun logical(bi: Binary)
    {
        if( bi.operation == Operation.AND ) {
            code(bi.left)
            val zr0 = instructions.append(createIfJump(Const.IFEQ))
            code(bi.right)
            val zr1 = instructions.append(createIfJump(Const.IFEQ))
            instructions.append(factory.createConstant(1))
            val en = instructions.append(createGoto(null))
            val zc = instructions.append(factory.createConstant(1))
            zr0.target = zc
            zr1.target = zc
            en.target = instructions.append(createNop())
        }
        else if( bi.operation == Operation.OR ) {
            code(bi.left)
            val ne = instructions.append(createIfJump(Const.IFNE))
            code(bi.right)
            val eq = instructions.append(createIfJump(Const.IFEQ))
            ne.target = instructions.append(factory.createConstant(1))
            val en = instructions.append(createGoto(null))
            eq.target = instructions.append(factory.createConstant(0))
            en.target = instructions.append(createNop())
        }
    }

    private fun code(un: Unary)
    {
        code(un.right)
        if( un.operation == Operation.SUB )
            instructions.append(InstructionConst.DNEG)
        else if( un.operation == Operation.NOT ) {
            val ne = instructions.append(createIfJump(Const.IFNE))
            instructions.append(factory.createConstant(1))
            val en = instructions.append(createGoto(null))
            ne.target = instructions.append(factory.createConstant(0))
            en.target = instructions.append(createNop())
        }
    }

    private fun code(ap: Apply)
    {
        val els = ap.callee.name.split('.')
        val base = if (els.size == 2) els[0] else className
        val name = if (els.size == 2) els[1] else els[0]

        ap.arguments.forEach { code(it) }
        val aty = ap.callee.parametersTypes.map { bcelType[it] }
        val inv = factory.createInvoke(
                base,
                name,
                bcelType[ap.callee.resultType],
                aty.toTypedArray(),
                Const.INVOKESTATIC)
        instructions.append(inv)
    }

    private fun code(lg: Logical)
    {
        instructions.append(factory.createConstant(lg.value == "ՃԻՇՏ"))
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
        instructions.append(InstructionFactory.createLoad(bcelType[vr.sym.type], ix))
    }

    private fun createGoto(target: InstructionHandle?) =
        InstructionFactory.createBranchInstruction(Const.GOTO, target)

    private fun createIfJump(opcode: Short) =
        InstructionFactory.createBranchInstruction(opcode, null)

    private fun createNop() =
        InstructionFactory.createNull(bcelType[Type.VOID])
}
