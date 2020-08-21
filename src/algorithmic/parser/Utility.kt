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
        "+" -> Operation.ADDITION
        "-" -> Operation.SUBTRACTION
        "*" -> Operation.MULTIPLICATION
        "/" -> Operation.DIVISION
        else -> throw ParseError("Անծանոթ գործողություն «$opn»։", 0)
    }