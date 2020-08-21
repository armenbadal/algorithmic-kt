package algorithmic.parser

import algorithmic.engine.Symbol

fun asType(name: String): Symbol.Type =
     when (name) {
        "ԹԻՎ" -> Symbol.Type.NUMBER
        "ՏԵՔՍՏ" -> Symbol.Type.TEXT
        else -> throw ParseError("Անծանոթ տիպ «$name»։")
    }
