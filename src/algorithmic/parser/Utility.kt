package algorithmic.parser

import algorithmic.engine.Operation
import algorithmic.engine.Symbol

fun asType(name: String): Symbol.Type =
     when( name ) {
        "ԹԻՎ" -> Symbol.Type.NUMBER
        "ՏԵՔՍՏ" -> Symbol.Type.TEXT
        else -> throw ParseError("Անծանոթ տիպ «$name»։", 0)
    }

fun asOperation(opn: String): Operation =
    when( opn ) {
        "+" -> Operation.ADD
        "-" -> Operation.SUB
        "*" -> Operation.MUL
        "/" -> Operation.DIV
        "=" -> Operation.EQ
        "<>" -> Operation.NE
        ">" -> Operation.GT
        ">=" -> Operation.GE
        "<" -> Operation.LT
        "<=" -> Operation.LE
        else -> throw ParseError("Անծանոթ գործողություն «$opn»։", 0)
    }

fun typeOf(op: Operation, left: Symbol.Type, right: Symbol.Type): Symbol.Type
{
    if( left == Symbol.Type.NUMBER && right == Symbol.Type.NUMBER ) {
        return Symbol.Type.NUMBER
    }

    return Symbol.Type.VOID
}