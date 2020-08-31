package algorithmic.parser

import algorithmic.engine.Operation
import algorithmic.engine.Type

fun asType(name: String): Type =
     when( name ) {
        "ԻՐԱԿԱՆ" -> Type.REAL
        "ՏԵՔՍՏ" -> Type.TEXT
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

fun typeOf(op: Operation, left: Type): Type
{
    if( (op == Operation.SUB || op == Operation.ADD) && left == Type.REAL )
        return Type.REAL

    throw TypeError("Տիպերի անհամապատասխանություն", 0)
}

fun typeOf(op: Operation, left: Type, right: Type): Type
{
    if( left == Type.REAL && right == Type.REAL )
        return Type.REAL

    if (left == Type.TEXT && right == Type.TEXT )
        return Type.TEXT

    throw TypeError("Տիպերի անհամապատասխանություն", 0)
}
