package algorithmic.parser

import algorithmic.engine.Operation
import algorithmic.engine.Type

fun asType(name: String): Type =
     when( name ) {
         "ԲՈՒԼՅԱՆ" -> Type.BOOL
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

fun typeOf(op: Operation, right: Type): Type
{
    if( (op == Operation.SUB || op == Operation.ADD) && right == Type.REAL )
        return Type.REAL

    if( op == Operation.NOT && right == Type.BOOL )
        return Type.BOOL

    throw TypeError("Տիպերի անհամապատասխանություն", 0)
}

fun typeOf(op: Operation, left: Type, right: Type): Type
{
    if( left == right )
        return left

    throw TypeError("Տիպերի անհամապատասխանություն", 0)
}
