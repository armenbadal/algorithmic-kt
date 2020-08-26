package algorithmic.parser

import algorithmic.engine.Operation
import algorithmic.engine.Symbol

fun asType(name: String): Symbol.Type =
     when( name ) {
        "ԻՐԱԿԱՆ" -> Symbol.Type.REAL
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
        "ԵՎ" -> Operation.AND
        "ԿԱՄ" -> Operation.OR
        "ՈՉ" -> Operation.NOT
        else -> throw ParseError("Անծանոթ գործողություն «$opn»։", 0)
    }

fun typeOf(op: Operation, left: Symbol.Type, right: Symbol.Type): Symbol.Type
{
    if( left == Symbol.Type.REAL && right == Symbol.Type.REAL ) {
        return Symbol.Type.REAL
    }

    return Symbol.Type.VOID
}