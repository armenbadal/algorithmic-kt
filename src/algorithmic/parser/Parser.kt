package algorithmic.parser

import algorithmic.engine.*

class Parser constructor(private val scanner: Scanner) {
    private var lookahead = scanner.next()

    // հայտարարությունները սկսվում են տիպի անունով
    private val firstDecl = arrayOf(Token.ԹԻՎ, Token.ՏԵՔՍՏ)
    private val firstStat = arrayOf(Token.ԱՆՈՒՆ, Token.ԵԹԵ, Token.ՔԱՆԻ, Token.ԱՐԴՅՈՒՆՔ)

    // վերլուծել ծրագիրը
    fun parse()
    {
        while( see(Token.ԱԼԳՈՐԻԹՄ) )
            algorithm()
    }

    // վերլուծել մեկ ալգորիթմ
    private fun algorithm()
    {
        match(Token.ԱԼԳՈՐԻԹՄ)
        val rtype = type(true)
        val name = match(Token.ԱՆՈՒՆ)
        // պարամետրերի ցուցակ
        val params = arrayListOf<Symbol>()
        if( see(Token.ՁԱԽ_ՓԱԿԱԳԻԾ) ) {
            match(Token.ՁԱԽ_ՓԱԿԱԳԻԾ)
            if ( !see(Token.ԱՋ_ՓԱԿԱԳԻԾ) )
                params.addAll(declarationList(true, Token.ՍՏՈՐԱԿԵՏ))
            match(Token.ԱՋ_ՓԱԿԱԳԻԾ)
        }

        val algo = Algorithm(name, rtype, params)
        println(algo)

        // լոկալ անուններ
        if ( !see(Token.ՍԿԻԶԲ) ) {
            val locals = declarationList(false, Token.ԿԵՏ_ՍՏՈՐԱԿԵՏ)
            algo.locals.addAll(locals)
        }

        // մարմին
        match(Token.ՍԿԻԶԲ)
        sequence()
        match(Token.ՎԵՐՋ)
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

        throw ParseError("Սպասվում է տիպի անուն, բայց հանդիպել է ${lookahead.value}։")
    }

    // վերլուծել մեկ հայտարարություն
    private fun declaration(single: Boolean): List<Symbol>
    {
        val ty = type(false)

        val symbols = arrayListOf<Symbol>()
        val nm0 = match(Token.ԱՆՈՒՆ)
        symbols.add(Symbol(ty, nm0))
        if( !single ) {
            while (see(Token.ՍՏՈՐԱԿԵՏ)) {
                match(Token.ՍՏՈՐԱԿԵՏ)
                val nm1 = match(Token.ԱՆՈՒՆ)
                symbols.add(Symbol(ty, nm1))
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
        return Sequence()
    }

    // վերագրում
    private fun assignment(): Assignment
    {
        val name = match(Token.ԱՆՈՒՆ)
        match(Token.ՎԵՐԱԳՐԵԼ)
        val value = expression()
        return Assignment(name, value)
    }

    // ճյուղավորում
    private fun branching()
    {
        match(Token.ԵԹԵ)
        expression()
        match(Token.ԱՊԱ)
        sequence()
        while( see(Token.ԻՍԿ) ) {
            match(Token.ԻՍԿ)
            match(Token.ԵԹԵ)
            expression()
            match(Token.ԱՊԱ)
            sequence()
        }
        if( see(Token.ԱՅԼԱՊԵՍ) ) {
            match(Token.ԱՅԼԱՊԵՍ)
            sequence()
        }
        match(Token.ԱՎԱՐՏ)
    }

    // կրկնություն
    private fun repetition()
    {
        match(Token.ՔԱՆԻ)
        match(Token.ԴԵՌ)
        expression()
        match(Token.ԱՊԱ)
        sequence()
        match(Token.ԱՎԱՐՏ)
    }

    // արտահայտություն
    private fun expression(): Expression
    {
        return factor()
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
                Variable(name)
            }
            else -> {
                throw ParseError("Ստպասվում է պարզ արտահայտություն")
            }
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

        throw ParseError("Սխալ: ${lookahead.line} տողում սպասվում է $exp, բայց գրված է ${lookahead.token}։")
    }
}