package algorithmic.engine

import algorithmic.parser.typeOf

// արտահայտությունների բազային դաս
sealed class Expression(val type: Type)

// թվային հաստատուն
class Numeric(val value: Double) : Expression(Type.REAL) {
    override fun toString(): String = value.toString()
}

// տեքստային հաստատուն
class Text(val value: String) : Expression(Type.TEXT) {
    override fun toString(): String = value
}

// տրամաբանական
class Logical(val value: String) : Expression(Type.BOOL) {
    override fun toString(): String = value
}

// փոփոխական
class Variable(val sym: Symbol) : Expression(sym.type) {
    override fun toString(): String = sym.id
}

enum class Operation(val text: String) {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),

    EQ("="),
    NE("<>"),
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),

    AND("ԵՎ"),
    OR("ԿԱՄ"),
    NOT("ՈՉ");

    override fun toString(): String = text
}

// ունար գործողություն
class Unary(val operation: Operation, type: Type, val right: Expression) : Expression(type) {
    override fun toString(): String =
        String.format("(%s %s)", operation, right)
}

// բինար գործողություն
class Binary(val operation: Operation, type: Type, val left: Expression, val right: Expression) : Expression(type) {
    override fun toString(): String =
        String.format("(%s %s %s)", left, operation, right)
}

// ֆունկցիա ալգորիթմի կանչ
class Apply(val callee: Signature, val arguments: List<Expression>) : Expression(callee.resultType) {}
