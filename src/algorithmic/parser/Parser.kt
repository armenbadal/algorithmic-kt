package algorithmic.parser

import algorithmic.engine.*
import java.nio.file.Paths

class Parser constructor(private val scanner: Scanner) {
    // look-a-head սիմվոլը
    private var lookahead = scanner.next()

    // հրամանների FIRST բազմությունը
    private val firstStat = arrayOf(Token.ԱՆՈՒՆ, Token.ԵԹԵ, Token.ՔԱՆԻ, Token.ԱՐԴՅՈՒՆՔ)
    // արտահայտությունների FIRST բազմությունը
    private val firstExpr = arrayOf(Token.ԹՎԱՅԻՆ, Token.ՏԵՔՍՏԱՅԻՆ, Token.ԱՆՈՒՆ,
            Token.ՁԱԽ_ՓԱԿԱԳԻԾ, Token.ADD, Token.SUB)

    // վերլուծվող ծրագիրը
    private val program = Program(Paths.get(scanner.filename).fileName.toString())

    // սիմվոլների աղյուսակ
    private val symbolTable = arrayListOf<Symbol>()
    // սահմանված ալգորիթմներ
    private val signatures = arrayListOf<Signature>()
    private val unresolved = arrayListOf<Signature>()

    init {
        signatures.add(Signature("ֆ", Symbol.Type.VOID,
                arrayListOf(Symbol.Type.TEXT, Symbol.Type.NUMBER, Symbol.Type.TEXT)))
    }


    // վերլուծել ծրագիրը
    fun parse(): Program
    {
        while( see(Token.ԱԼԳՈՐԻԹՄ) )
            algorithm()

        return program
    }

    // վերլուծել մեկ ալգորիթմ
    private fun algorithm()
    {
        match(Token.ԱԼԳՈՐԻԹՄ)
        val resultType = type(true)
        val name = match(Token.ԱՆՈՒՆ)

        // պարամետրերի ցուցակ
        val params = arrayListOf<Symbol>()
        if( see(Token.ՁԱԽ_ՓԱԿԱԳԻԾ) ) {
            match(Token.ՁԱԽ_ՓԱԿԱԳԻԾ)
            if ( !see(Token.ԱՋ_ՓԱԿԱԳԻԾ) )
                params.addAll(declarationList(true, Token.ՍՏՈՐԱԿԵՏ))
            match(Token.ԱՋ_ՓԱԿԱԳԻԾ)
        }

        // մաքրել սիմվոլների աղյուսակը...
        symbolTable.clear()
        // ... ու ավելացնել ալգորիթմի պարամետրերը
        symbolTable.addAll(params)

        // ստեղծել նոր ալգորիթմի նկարագրությունը
        val sig = Signature(name, resultType, params.map { it.type })
        signatures.add(sig)

        // լոկալ անուններ
        if ( !see(Token.ՍԿԻԶԲ) ) {
            val locals = declarationList(false, Token.ԿԵՏ_ՍՏՈՐԱԿԵՏ)
            symbolTable.addAll(locals)
        }

        // մարմին
        match(Token.ՍԿԻԶԲ)
        val body = sequence()
        match(Token.ՎԵՐՋ)

        val alg = Algorithm(name, resultType, params, body)
        alg.locals.addAll(symbolTable.filter { !params.contains(it) })
        program.add(alg)
    }

    // տիպի վերլուծություն
    private fun type(opt: Boolean): Symbol.Type
    {
        if( see(Token.ԹԻՎ) )
            return asType(pass())

        if( see(Token.ՏԵՔՍՏ) )
            return asType(pass())

        if( opt )
            return Symbol.Type.VOID

        throw ParseError("Սպասվում է տիպի անուն, բայց հանդիպել է ${lookahead.value}։", scanner.line)
    }

    // վերլուծել մեկ հայտարարություն
    private fun declaration(single: Boolean): List<Symbol>
    {
        val ty = type(false)

        val symbols = arrayListOf<Symbol>()
        val nm0 = match(Token.ԱՆՈՒՆ)
        symbols.add(Symbol(nm0, ty))
        if( !single ) {
            while( see(Token.ՍՏՈՐԱԿԵՏ) ) {
                match(Token.ՍՏՈՐԱԿԵՏ)
                val nm1 = match(Token.ԱՆՈՒՆ)
                symbols.add(Symbol(nm1, ty))
            }
        }

        return symbols
    }

    // հայտարարությունների շարք
    private fun declarationList(single: Boolean, sep: Token): List<Symbol>
    {
        val symbols = arrayListOf<Symbol>()

        symbols.addAll(declaration(single))
        while( see(sep) ) {
            pass()
            symbols.addAll(declaration(single))
        }

        return symbols
    }

    // հրամանների հաջորդականություն
    private fun sequence(): Sequence
    {
        val seq = Sequence()

        if( see(*firstStat) ) {
            seq.items.add(statement())
            while( see(Token.ԿԵՏ_ՍՏՈՐԱԿԵՏ) ) {
                pass()
                seq.items.add(statement())
            }
        }

        return seq
    }

    // մեկ կառուցվածքի վերլուծություն
    private fun statement(): Statement
    {
        if( see(Token.ԱՆՈՒՆ) )
            return assignmentOrCall()

        if( see(Token.ԵԹԵ) )
            return branching(true)

        if( see(Token.ՔԱՆԻ) )
            return repetition()

        if( see(Token.ԱՐԴՅՈՒՆՔ) )
            return result()

        throw ParseError("Սպասվում էր ... բայց հանդիպել է ${lookahead.value}։", scanner.line)
    }

    // վերագրում կամ ալգորիթմի կիրառում
    private fun assignmentOrCall(): Statement
    {
        val name = match(Token.ԱՆՈՒՆ)

        if( see(Token.ՎԵՐԱԳՐԵԼ) )
            return assignment(name)

        if( see(Token.ՁԱԽ_ՓԱԿԱԳԻԾ) )
            return algorithmCall(name)

        throw ParseError("Սպասվում էր «:=» կամ «(», բայց հանդիպել է «${lookahead.value}»", scanner.line)
    }

    // վերագրում
    private fun assignment(name: String): Assignment
    {
        match(Token.ՎԵՐԱԳՐԵԼ)
        val sym = lookup(name)
        val value = expression()
        return Assignment(sym, value)
    }

    // ալգորիթմի կիրառում
    private fun algorithmCall(name: String): Call
    {
        match(Token.ՁԱԽ_ՓԱԿԱԳԻԾ)
        val arguments = expressionList()
        match(Token.ԱՋ_ՓԱԿԱԳԻԾ)
        val alg = signatures.find { it.name == name }
                ?: throw ParseError("Անծանոթ ֆունկցիայի՝ «$name», կիրառություն:", scanner.line)

        return Call(alg, arguments)
    }

    // արտահայտությունների ցուցակ
    private fun expressionList(): List<Expression>
    {
        val exprs = arrayListOf<Expression>()
        if( see(*firstExpr) ) {
            exprs.add(expression())
            while( see(Token.ՍՏՈՐԱԿԵՏ) ) {
                pass()
                exprs.add(expression())
            }
        }
        return exprs
    }

    // ճյուղավորում
    private fun branching(closing: Boolean): Branching
    {
        match(Token.ԵԹԵ)
        val cond = expression()
        match(Token.ԱՊԱ)
        val deci = sequence()
        val alte = when {
            see(Token.ԻՍԿ) -> {
                pass()
                Branching(cond, deci, branching(false))
            }
            see(Token.ԱՅԼԱՊԵՍ) -> {
                pass()
                Branching(cond, deci, sequence())
            }
            else -> {
                Sequence()
            }
        }
        if( closing )
            match(Token.ԱՎԱՐՏ)
        return Branching(cond, deci, alte)
    }

    // կրկնություն
    private fun repetition(): Repetition
    {
        match(Token.ՔԱՆԻ)
        match(Token.ԴԵՌ)
        val cond = expression()
        match(Token.ԱՊԱ)
        val body = sequence()
        match(Token.ԱՎԱՐՏ)
        return Repetition(cond, body)
    }

    // արդյունք
    private fun result(): Statement
    {
        match(Token.ԱՐԴՅՈՒՆՔ)
        val value = expression()
        return Result(value)
    }

    // արտահայտություն
    private fun expression(): Expression
    {
        return equality()
    }

    // հավասարություն
    private fun equality(): Expression
    {
        var res = comparison()
        if( see(Token.EQ, Token.NE) ) {
            val oper = asOperation(pass())
            res = Binary(oper, res, comparison())
        }
        return res
    }

    // համեմատություն
    private fun comparison(): Expression
    {
        var res = addition()
        if( see(Token.GT, Token.GE, Token.LT, Token.LE) ) {
            val oper = asOperation(pass())
            res = Binary(oper, res, addition())
        }
        return res
    }

    // գումար
    private fun addition(): Expression
    {
        var res = multiplication()
        while( see(Token.ADD, Token.SUB) ) {
            val oper = asOperation(pass())
            res = Binary(oper, res, multiplication())
        }
        return res
    }

    // արտադրյալ
    private fun multiplication(): Expression
    {
        var res = factor()
        while( see(Token.MUL, Token.DIV) ) {
            val oper = asOperation(pass())
            res = Binary(oper, res, factor())
        }
        return res
    }

    // ամենապարզ արտահայտությունը
    private fun factor(): Expression =
        when (lookahead.token) {
            Token.ԹՎԱՅԻՆ -> {
                val value = pass()
                Numeric(value.toDouble())
            }
            Token.ՏԵՔՍՏԱՅԻՆ -> {
                val value = pass()
                Text(value)
            }
            Token.ԱՆՈՒՆ -> {
                val name = pass()
                Variable(lookup(name))
            }
            Token.SUB, Token.ADD -> {
                val oper = asOperation(pass())
                Unary(oper, factor())
            }
            Token.ՁԱԽ_ՓԱԿԱԳԻԾ -> {
                pass()
                val expr = expression()
                match(Token.ԱՋ_ՓԱԿԱԳԻԾ)
                expr
            }
            else -> {
                throw ParseError("Ստպասվում է պարզ արտահայտություն", scanner.line)
            }
        }

    // ֆունկցիա ալգորիթմի կանչ
    private fun apply(name: String): Expression
    {
        match(Token.ՁԱԽ_ՓԱԿԱԳԻԾ)
        val args = expressionList()
        match(Token.ԱՋ_ՓԱԿԱԳԻԾ)
        val func = signatures.find { it.name == name }
                ?: throw ParseError("Անծանոթ ֆունկցիայի՝ «$name», կիրառություն:", scanner.line)

        return Apply(func, args)
    }

    private fun see(exp: Token): Boolean =
        lookahead.token == exp

    private fun see(vararg exps: Token): Boolean =
        exps.contains(lookahead.token)

    private fun pass(): String
    {
        val lex = lookahead.value
        lookahead = scanner.next()
        return lex
    }

    // հաստատել lookahead-ը և կարդալ հաջորդը
    private fun match(exp: Token): String
    {
        if( see(exp) )
            return pass()

        throw ParseError("Սպասվում է $exp, բայց գրված է «${lookahead.value}»։", scanner.line)
    }

    // որոնել սիմվոլների աղյուսակում
    private fun lookup(name: String): Symbol
    {
        for( sym in symbolTable )
            if( sym.id == name )
                return sym

        throw ParseError("Չհայտարարված փոփոխականի ($name) օգտագործում։", scanner.line)
    }

//    // որոնել տրված անունով ալգորիթմը
//    private fun lookupAlgorithm(sig: Signature): Signature
//    {
//
//    }
}
