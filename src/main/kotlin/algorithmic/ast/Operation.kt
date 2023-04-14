package algorithmic.ast

enum class Operation(val text: String) {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    MOD("\\"),

    EQ("="),
    NE("<>"),
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),

    AND("ԵՎ"),
    OR("ԿԱՄ"),
    NOT("ՈՉ"),

    INDEX("[]");

    override fun toString(): String = text

    companion object {
        fun from(text: String): Operation {
            for(op in values())
                if(op.text == text)
                    return op

            throw UnknownOperation("Անծանոթ գործողություն «$text»։")
        }
    }
}
