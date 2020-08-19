package algorithmic.parser

import java.lang.StringBuilder
import java.nio.file.Files
import java.nio.file.Paths

class Scanner constructor(filename: String) {
    private val keywords = mapOf(
        "ԱԼԳ" to Token.ԱԼԳՈՐԻԹՄ,
        "ՍԿԻԶԲ" to Token.ՍԿԻԶԲ,
        "ՎԵՐՋ" to Token.ՎԵՐՋ,
        "ԵԹԵ" to Token.ԵԹԵ,
        "ԱՊԱ" to Token.ԱՊԱ,
        "ԻՍԿ" to Token.ԻՍԿ,
        "ԱՅԼԱՊԵՍ" to Token.ԱՅԼԱՊԵՍ,
        "ԱՎԱՐՏ" to Token.ԱՎԱՐՏ,
        "ՔԱՆԻ" to Token.ՔԱՆԻ,
        "ԴԵՌ" to Token.ԴԵՌ,
        "ԹԻՎ" to Token.ԹԻՎ,
        "ՏԵՔՍՏ" to Token.ՏԵՔՍՏ
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

        if( ch.isLetter() )
            return keywordOrIdentifier()

        if( ch.isDigit() )
            return numericLiteral()

        if( ch == '"' || ch == '«' )
            return textLiteral()

        // վերագրում և այլն
        if( ch == ':' )
            return operation()

        return metasymbol()
    }

    // անտեսել բացատները
    private fun whitespaces()
    {
        while( ch.isWhitespace() ) {
            if( ch == '\n' )
                ++line
            ch = read()
        }
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

        return Lexeme(Token.ԱՆԾԱՆՈԹ, "$ch", line)
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