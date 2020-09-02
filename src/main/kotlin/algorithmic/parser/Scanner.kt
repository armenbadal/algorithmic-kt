package algorithmic.parser

import java.lang.StringBuilder
import java.nio.file.Files
import java.nio.file.Paths

class Scanner constructor(val filename: String) {
    private val keywords = mapOf(
            "ԾՐԱԳԻՐ" to Token.ԾՐԱԳԻՐ,
            "ԳՐԱԴԱՐԱՆ" to Token.ԳՐԱԴԱՐԱՆ,
            "ԱԼԳՈՐԻԹՄ" to Token.ԱԼԳՈՐԻԹՄ,
            "ՍՏՈՐԵՎ" to Token.ՍՏՈՐԵՎ,
            "ՍԿԻԶԲ" to Token.ՍԿԻԶԲ,
            "ՎԵՐՋ" to Token.ՎԵՐՋ,
            "ԵԹԵ" to Token.ԵԹԵ,
            "ԱՊԱ" to Token.ԱՊԱ,
            "ԻՍԿ" to Token.ԻՍԿ,
            "ԱՅԼԱՊԵՍ" to Token.ԱՅԼԱՊԵՍ,
            "ԱՎԱՐՏ" to Token.ԱՎԱՐՏ,
            "ՔԱՆԻ" to Token.ՔԱՆԻ,
            "ԴԵՌ" to Token.ԴԵՌ,
            "ԱՐԴՅՈՒՆՔ" to Token.ԱՐԴՅՈՒՆՔ,
            "ԵՎ" to Token.ԵՎ,
            "ԿԱՄ" to Token.ԿԱՄ,
            "ՈՉ" to Token.ՈՉ,
            "ԻՐԱԿԱՆ" to Token.ԻՐԱԿԱՆ,
            "ՏԵՔՍՏ" to Token.ՏԵՔՍՏ,
            "ԲՈՒԼՅԱՆ" to Token.ԲՈՒԼՅԱՆ,
            "ՃԻՇՏ" to Token.ՃԻՇՏ,
            "ԿԵՂԾ" to Token.ԿԵՂԾ,
            "ԱՂՅՈՒՍԱԿ" to Token.ԱՂՅՈՒՍԱԿ,
            // դասական ուղղագրութեամբ տարբերակները
            "ՍՏՈՐԵՒ" to Token.ՍՏՈՐԵՎ,
            "ԵԹԷ" to Token.ԵԹԵ,
            "ԱՅԼԱՊԷՍ" to Token.ԱՅԼԱՊԵՍ,
            "ԱՒԱՐՏ" to Token.ԱՎԱՐՏ,
            "ԱՐԴԻՒՆՔ" to Token.ԱՐԴՅՈՒՆՔ,
            "ԵՒ" to Token.ԵՎ,
            "ԲՈՒԼԵԱՆ" to Token.ԲՈՒԼՅԱՆ,
            "ԱՂԻՒՍԱԿ" to Token.ԱՂՅՈՒՍԱԿ
    )

    // ներմուծման հոսք
    private val input = Files.newBufferedReader(Paths.get(filename))
    // հերթական սիմվոլը
    private var ch: Char = read()
    // տողի համարը
    private var line: Int = 1

    //
    fun next(): Lexeme
    {
        if( !input.ready() )
            return Lexeme(Token.ՖԱՅԼԻ_ՎԵՐՋ, "<ՖԱՅԼԻ ՎԵՐՋ>", line)

        whitespaces()

        if( ch == '{' ) {
            comment()
            return next()
        }

        if( ch.isLetter() )
            return keywordOrIdentifier()

        if( ch.isDigit() )
            return numericLiteral()

        if( ch == '"' || ch == '«' )
            return textLiteral()

        // վերագրում և այլն
        if( ":=<>+-*/".contains(ch) )
            return operation()

        return metasymbol()
    }

    // տողի համարը
    fun getLine(): Int = line

    // անտեսել բացատները
    private fun whitespaces()
    {
        while( ch.isWhitespace() || ch == '|' ) {
            if( ch == '\n' )
                ++line
            ch = read()
        }
    }

    // մեկնաբանություն
    private fun comment()
    {
        while( ch != '}' )
            ch = read()
        ch = read()
    }

    // ծառայողական բառ կամ անուն
    private fun keywordOrIdentifier(): Lexeme
    {
        val sb = StringBuilder()

        while( ch.isLetterOrDigit() ) {
            sb.append(ch)
            ch = read()
        }

        val lex = sb.toString()
        val tok = keywords.getOrDefault(lex, Token.ԱՆՈՒՆ)
        return Lexeme(tok, lex, line)
    }

    // թվային լիտերալ
    private fun numericLiteral(): Lexeme
    {
        val sb = StringBuilder()

        while( ch.isDigit() ) {
            sb.append(ch)
            ch = read()
        }
        if( ch == '.' ) {
            sb.append('.')
            ch = read()
            while( ch.isDigit() ) {
                sb.append(ch)
                ch = read()
            }
        }

        val lex = sb.toString()
        return Lexeme(Token.ԹՎԱՅԻՆ, lex, line)
    }

    // տեքստային լիտերալ
    private fun textLiteral(): Lexeme
    {
        val sb = StringBuilder()

        val end = if( ch == '«' ) '»' else '"'
        ch = read()
        while( ch != end ) {
            sb.append(ch)
            ch = read()
        }
        ch = read()

        val lex = sb.toString()
        return Lexeme(Token.ՏԵՔՍՏԱՅԻՆ, lex, line)
    }

    // գործողություններ
    private fun operation(): Lexeme
    {
        if( ch == ':' ) {
            ch = read()
            if( ch == '=' ) {
                ch = read()
                return Lexeme(Token.ՎԵՐԱԳՐԵԼ, ":=", line)
            }
            return Lexeme(Token.ՎԵՐՋԱԿԵՏ, ":", line)
        }

        if( ch == '=' ) {
            ch = read()
            return Lexeme(Token.EQ, "=", line)
        }

        if( ch == '<' ) {
            ch = read()
            if( ch == '>' ) {
                ch = read()
                return Lexeme(Token.NE, "<>", line)
            }
            else if( ch == '=' ) {
                ch = read()
                return Lexeme(Token.LE, "<=", line)
            }
            return Lexeme(Token.LT, "<", line)
        }

        if( ch == '>' ) {
            ch = read()
            if( ch == '=' ) {
                ch = read()
                return Lexeme(Token.GE, ">=", line)
            }
            return Lexeme(Token.GT, ">", line)
        }

        val ms = ch
        val tok = when( ms ) {
            '+' -> Token.ADD
            '-' -> Token.SUB
            '*' -> Token.MUL
            '/' -> Token.DIV
            else -> Token.ԱՆԾԱՆՈԹ
        }
        ch = read()

        return Lexeme(tok, "$ms", line)
    }

    // մետասիմվոլներ
    private fun metasymbol(): Lexeme
    {
        val ms = ch
        val kind = when(ms) {
            '(' -> Token.ՁԱԽ_ՓԱԿԱԳԻԾ
            ')' -> Token.ԱՋ_ՓԱԿԱԳԻԾ
            ':' -> Token.ՎԵՐՋԱԿԵՏ
            ',' -> Token.ՍՏՈՐԱԿԵՏ
            ';' -> Token.ԿԵՏ_ՍՏՈՐԱԿԵՏ
            else -> Token.ԱՆԾԱՆՈԹ
        }
        ch = read()

        return Lexeme(kind, ms.toString(), line)
    }

    // կարդալ մեկ նիշ
    private fun read() = input.read().toChar()
}