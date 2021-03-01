package algorithmic.parser

import algorithmic.ast.Operation
import algorithmic.ast.Scalar
import algorithmic.ast.Type

fun asType(name: String): Type =
     when( name ) {
         "ԲՈՒԼՅԱՆ" -> Scalar.BOOL
         "ԻՐԱԿԱՆ" -> Scalar.REAL
         "ՏԵՔՍՏ" -> Scalar.TEXT
         else -> throw ParseError("Անծանոթ տիպ «$name»։", 0)
    }

fun asOperation(opn: String): Operation =
    when( opn ) {
        "+" -> Operation.ADD
        "-" -> Operation.SUB
        "*" -> Operation.MUL
        "/" -> Operation.DIV
        "\\" -> Operation.MOD
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
