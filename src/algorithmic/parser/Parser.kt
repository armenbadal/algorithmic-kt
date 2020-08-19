package algorithmic.parser

import algorithmic.engine.Expression
import algorithmic.engine.Numeric
import algorithmic.engine.Text
import algorithmic.engine.Variable

class Parser constructor(private val scanner: Scanner) {
    private var lookahead = scanner.next()

    private val firstDecl = arrayOf(Token.ԹԻՎ, Token.ՏԵՔՍՏ)

    // վերլուծել ծրագիրը
    fun parse()
    {
        algorithm()
    }

    // վերլուծել մեկ ալգորիթմ
    private fun algorithm()
    {
        match(Token.ԱԼԳՈՐԻԹՄ)
        match(Token.ԱՆՈՒՆ)
        // պարամետրերի ցուցակ
        if( see(Token.ՁԱԽ_ՓԱԿԱԳԻԾ) ) {
            match(Token.ՁԱԽ_ՓԱԿԱԳԻԾ)
            if ( see(*firstDecl) ) {
                declaration()
                while( see(Token.ԿԵՏ_ՍՏՈՐԱԿԵՏ) ) {
                    match(Token.ԿԵՏ_ՍՏՈՐԱԿԵՏ)
                    declaration()
                }
            }
            match(Token.ԱՋ_ՓԱԿԱԳԻԾ)
        }
        // լոկալ անուններ
        declaration(); // TODO
        // մարմին
        match(Token.ՍԿԻԶԲ)
        sequence()
        match(Token.ՎԵՐՋ)
    }

    // վերլուծել մեկ հայտարարություն
    private fun declaration()
    {
        match(lookahead.token)
        match(Token.ԱՆՈՒՆ)
        while( see(Token.ՍՏՈՐԱԿԵՏ) ) {
            match(Token.ՍՏՈՐԱԿԵՏ)
            match(Token.ԱՆՈՒՆ)
        }
    }

    // հրամանների հաջորդականություն
    private fun sequence()
    {}

    // վերագրում
    private fun assignment()
    {
        match(Token.ԱՆՈՒՆ)
        match(Token.ՎԵՐԱԳՐԵԼ)
        expression()
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