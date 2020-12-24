package algorithmic.parser

import algorithmic.ast.*

class Parser constructor(private val scanner: Scanner) {
    // look-a-head սիմվոլը
    private var lookahead = scanner.next()

    // հրամանների FIRST բազմությունը
    private val firstStat = arrayOf(Token.ԱՆՈՒՆ, Token.ԵԹԵ, Token.ՔԱՆԻ, Token.ԱՐԴՅՈՒՆՔ)
    // արտահայտությունների FIRST բազմությունը
    private val firstExpr = arrayOf(Token.ԹՎԱՅԻՆ, Token.ՏԵՔՍՏԱՅԻՆ,
            Token.ԱՆՈՒՆ, Token.ՁԱԽ_ՓԱԿԱԳԻԾ, Token.ADD, Token.SUB)

    // սիմվոլների աղյուսակ
    private val symbolTable = arrayListOf<Symbol>()
    // ընթացիկ վերլուծվող ալգորիթմը
    private var current: String = ""
    // սահմանված կամ հայտարարված ալգորիթմների վերնագրերը
    private val signatures = builtIns()
    // հաջողությամբ վերլուծված ալգորիթմները
    private val algorithms = arrayListOf<Algorithm>()

    // վերլուծել ծրագիրը
    fun parse(): Program
    {
        match(Token.ԾՐԱԳԻՐ)
        val name = match(Token.ԱՆՈՒՆ)

        // ալգորիթմների սահմանումների և հայտարարությունների շարք
        while( see(Token.ԱԼԳՈՐԻԹՄ) )
            algorithm()

        // պարտադիր կատարվող բլոկ
        match(Token.ԿԱՏԱՐԵԼ)
        val body = sequence()
        match(Token.ԱՎԱՐՏ)

        return Program(name, algorithms, body)
    }

    // վերլուծել մեկ ալգորիթմ
    private fun algorithm()
    {
        match(Token.ԱԼԳՈՐԻԹՄ)
        val resultType = type(true)
        val name = match(Token.ԱՆՈՒՆ)

        // մաքրել սիմվոլների աղյուսակը
        symbolTable.clear()

        // պարամետրերի ցուցակ
        if( see(Token.ՁԱԽ_ՓԱԿԱԳԻԾ) ) {
            pass()
            if ( !see(Token.ԱՋ_ՓԱԿԱԳԻԾ) )
                declarationList(true, Token.ՍՏՈՐԱԿԵՏ)
            match(Token.ԱՋ_ՓԱԿԱԳԻԾ)
        }
        val params = symbolTable.map { it }

        // ստեղծել նոր ալգորիթմի նկարագրությունը
        signatures[name] = Signature(name, resultType, params.map { it.type })
        current = name

        // երբ սա պարզապես հայտարարություն է
        if( see(Token.ՍՏՈՐԵՎ) ) {
            pass()
            return
        }

        // լոկալ անուններ
        if ( !see(Token.ՍԿԻԶԲ) )
            declarationList(false, Token.ԿԵՏ_ՍՏՈՐԱԿԵՏ)

        // մարմին
        match(Token.ՍԿԻԶԲ)
        val body = sequence()
        match(Token.ՎԵՐՋ)

        val alg = Algorithm(name, resultType, params, body)
        alg.locals.addAll(symbolTable.filter { !params.contains(it) })
        algorithms.add(alg)
    }

    // տիպի վերլուծություն
    private fun type(opt: Boolean): Type
    {
        if( see(Token.ԻՐԱԿԱՆ) )
            return asType(pass())

        if( see(Token.ՏԵՔՍՏ) )
            return asType(pass())

        if( see(Token.ԲՈՒԼՅԱՆ) )
            return asType(pass())

        if( opt )
            return VOID

        throw ParseError("Սպասվում է տիպի անուն, բայց հանդիպել է ${lookahead.value}։", scanner.getLine())
    }

    // հայտարարությունների շարք
    private fun declarationList(single: Boolean, sep: Token)
    {
        declaration(single)
        while( see(sep) ) {
            pass()
            declaration(single)
        }
    }

    // վերլուծել մեկ հայտարարություն
    private fun declaration(single: Boolean)
    {
        val ty = type(false)

        oneNameWithCheck(ty)
        if( !single ) {
            while( see(Token.ՍՏՈՐԱԿԵՏ) ) {
                match(Token.ՍՏՈՐԱԿԵՏ)
                oneNameWithCheck(ty)
            }
        }
    }

    // հայտարարված մեկ անունի ստուգելը
    private fun oneNameWithCheck(ty: Type)
    {
        val line = scanner.getLine()
        val nm = match(Token.ԱՆՈՒՆ)
        val sym = Symbol(nm, ty)

        if( symbolTable.contains(sym) )
            throw ParseError("«${sym.id}» անունն արդեն հայտարարված է", line)
        symbolTable.add(sym)
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
    private fun statement() =
        when {
            see(Token.ԱՆՈՒՆ) -> assignmentOrCall()
            see(Token.ԵԹԵ) -> branching(true)
            see(Token.ՔԱՆԻ) -> repetition()
            see(Token.ԱՐԴՅՈՒՆՔ) -> result()
            else -> throw ParseError("Սպասվում էր ԱՆՈՒՆ, ԵԹԵ, ՔԱՆԻ կամ ԱՐԴՅՈՒՆՔ, բայց հանդիպել է ${lookahead.value}։", scanner.getLine())
        }

    // վերագրում կամ ալգորիթմի կիրառում
    private fun assignmentOrCall(): Statement
    {
        val name = match(Token.ԱՆՈՒՆ)

        if( see(Token.ՎԵՐԱԳՐԵԼ) )
            return assignment(name)

        if( see(Token.ՁԱԽ_ՓԱԿԱԳԻԾ) )
            return algorithmCall(name)

        throw ParseError("Սպասվում էր «:=» կամ «(», բայց հանդիպել է «${lookahead.value}»", scanner.getLine())
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
        if( !signatures.containsKey(name) )
            throw ParseError("«$name» ալգորիթմը հայտարարված կամ սհմանված չէ", scanner.getLine())

        match(Token.ՁԱԽ_ՓԱԿԱԳԻԾ)
        val arguments = expressionList()
        match(Token.ԱՋ_ՓԱԿԱԳԻԾ)

        val candidate = signatures.getValue(name)
        if( !candidate.isApplicable(arguments) )
            throw TypeError("«$name» ալգորիթմը սահմանված է «$candidate» պարամետրով:", scanner.getLine())

        return Call(candidate, arguments)
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
        val decision = sequence()
        val alternative = when {
            see(Token.ԻՍԿ) -> {
                pass()
                branching(false)
            }
            see(Token.ԱՅԼԱՊԵՍ) -> {
                pass()
                sequence()
            }
            else -> {
                Sequence()
            }
        }
        if( closing )
            match(Token.ԱՎԱՐՏ)
        return Branching(cond, decision, alternative)
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
        val expType = signatures.getValue(current).resultType

        if( expType == VOID )
            throw ParseError("ԱՐԴՅՈՒՆՔ հրամանը չի կարելի օգտագործել այս ալգորիթմում։", scanner.getLine())

        match(Token.ԱՐԴՅՈՒՆՔ)
        val value = expression()
        if( value.type != expType )
            throw TypeError("ԱՐԴՅՈՒՆՔ հրամանին պետք է տալ ${expType} tipi ar=eq", scanner.getLine())
        return Result(value)
    }

    // արտահայտություն
    private fun expression() =
        disjunction()

    // դիզյունկցիա
    private fun disjunction(): Expression
    {
        var left = conjunction()
        while( see(Token.ԿԱՄ) ) {
            pass()
            val oper = asOperation("ԿԱՄ")
            val right = conjunction()
            if( left.type != Scalar.BOOL || right.type != Scalar.BOOL )
                throw TypeError("«${oper.text} գործողությունը կիրառելի է ԲՈՒԼՅԱՆ արժեքներին։»", scanner.getLine())
            left = Binary(oper, Scalar.BOOL, left, right)
        }
        return left
    }

    // կոնյունկցիա
    private fun conjunction(): Expression
    {
        var left = equality()
        while( see(Token.ԵՎ) ) {
            pass()
            val oper = asOperation("ԵՎ")
            val right = equality()
            if( left.type != Scalar.BOOL || right.type != Scalar.BOOL )
                throw TypeError("«${oper.text} գործողությունը կիրառելի է ԲՈՒԼՅԱՆ արժեքներին։»", scanner.getLine())
            left = Binary(oper, Scalar.BOOL, left, right)
        }
        return left
    }

    // հավասարություն
    private fun equality(): Expression
    {
        var left = comparison()
        if( see(Token.EQ, Token.NE) ) {
            val oper = asOperation(pass())
            val right = comparison()
            if( left.type != right.type )
                throw TypeError("«${oper.text}» գործողության երկու կողմերում պետք է լինեն նույն տիպի արժեքներ։", scanner.getLine())
            left = Binary(oper, Scalar.BOOL, left, right)
        }
        return left
    }

    // համեմատություն
    private fun comparison(): Expression
    {
        var left = addition()
        if( see(Token.GT, Token.GE, Token.LT, Token.LE) ) {
            val oper = asOperation(pass())
            val right = addition()
            // տիպերի ստուգում
            if( left.type == Scalar.TEXT || right.type == Scalar.TEXT )
                throw TypeError("«${oper.text}» գործողությունը կիրառելի չէ ՏԵՔՍՏային արժեքներին։", scanner.getLine())
            if( left.type == Scalar.BOOL || right.type == Scalar.BOOL )
                throw TypeError("«${oper.text}» գործողությունը կիրառելի չէ ԲՈՒԼՅԱՆ արժեքներին։", scanner.getLine())
            if( left.type != right.type )
                throw TypeError("«${oper.text}» գործողության երկու կողմերում պետք է լինեն նույն տիպի արժեքներ։", scanner.getLine())
            left = Binary(oper, Scalar.BOOL, left, right)
        }
        return left
    }

    // գումար
    private fun addition(): Expression
    {
        var left = multiplication()
        while( see(Token.ADD, Token.SUB) ) {
            val oper = asOperation(pass())
            val right = multiplication()
            if( left.type != Scalar.REAL || right.type != Scalar.REAL )
                throw TypeError("«${oper.text}» գործողությունը թույլատրելի է ԻՐԱԿԱՆ թվերի համար։", scanner.getLine())
            left = Binary(oper, Scalar.REAL, left, right)
        }
        return left
    }

    // արտադրյալ
    private fun multiplication(): Expression
    {
        var left = factor()
        while( see(Token.MUL, Token.DIV, Token.MOD) ) {
            val oper = asOperation(pass())
            val right = factor()
            if( left.type != Scalar.REAL || right.type != Scalar.REAL )
                throw TypeError("«${oper.text}» գործողությունը թույլատրելի է ԻՐԱԿԱՆ թվերի համար։", scanner.getLine())
            left = Binary(oper, Scalar.REAL, left, right)
        }
        return left
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
                if( see(Token.ՁԱԽ_ՓԱԿԱԳԻԾ) )
                    apply(name)
                else if( see(Token.ՁԱԽ_ԻՆԴԵՔՍ) ) {
                    pass()
                    val inx = expression()
                    match(Token.ԱՋ_ԻՆԴԵՔՍ)
                    val sa = lookup(name)
                    Binary(Operation.INDEX, sa.type, Variable(sa), inx)
                }
                else
                    Variable(lookup(name))
            }
            Token.ՃԻՇՏ, Token.ԿԵՂԾ -> {
                val value = pass()
                Logical(value)
            }
            Token.SUB, Token.ADD, Token.ՈՉ -> {
                val oper = asOperation(pass())
                val right = factor()
                // տիպերի ստուգում
                if( oper == Operation.NOT && right.type != Scalar.BOOL )
                    throw TypeError("«ՈՉ» գործողությունը կիրառելի է միայն ԲՈՒԼՅԱՆ արժեքներին։", scanner.getLine())
                if( (oper == Operation.SUB || oper == Operation.ADD) && right.type != Scalar.REAL )
                    throw TypeError("«${oper.text}» գործողությունը կիրառելի է միայն ԻՐԱԿԱՆ արժեքներին։", scanner.getLine())
                Unary(oper, right.type, right)
            }
            Token.ՁԱԽ_ՓԱԿԱԳԻԾ -> {
                pass()
                val expr = expression()
                match(Token.ԱՋ_ՓԱԿԱԳԻԾ)
                expr
            }
            else -> {
                throw ParseError("Ստպասվում է պարզ արտահայտություն", scanner.getLine())
            }
        }

    // ֆունկցիա ալգորիթմի կանչ
    private fun apply(name: String): Expression
    {
        if( !signatures.containsKey(name) )
            throw ParseError("«$name» ալգորիթմը հայտարարված կամ սահմանված չէ", scanner.getLine())

        match(Token.ՁԱԽ_ՓԱԿԱԳԻԾ)
        val arguments = expressionList()
        match(Token.ԱՋ_ՓԱԿԱԳԻԾ)

        val candidate = signatures.getValue(name)
        if( !candidate.isApplicable(arguments) )
            throw TypeError("«$name» ալգորիթմը սահմանված է «$candidate» պարամետրով:", scanner.getLine())

        return Apply(candidate, arguments)
    }

    private fun see(exp: Token): Boolean =
        lookahead.token == exp

    private fun see(vararg exps: Token): Boolean =
        exps.contains(lookahead.token)

    // կարդալ հաջորդ լեքսեմը և վերադարձնել նախորդի արժեքը
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

        throw ParseError("Սպասվում է $exp, բայց գրված է «${lookahead.value}»։", scanner.getLine())
    }

    // որոնել սիմվոլների աղյուսակում
    private fun lookup(name: String): Symbol
    {
//        for( sym in symbolTable )
//            if( sym.id == name )
//                return sym
        return symbolTable.find { it.id == name } ?:
            throw ParseError("Չհայտարարված փոփոխականի ($name) օգտագործում։", scanner.getLine())
    }
}
