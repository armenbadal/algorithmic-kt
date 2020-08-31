package algorithmic.engine

import algorithmic.parser.typeOf

// արտահայտությունների բազային դաս
sealed class Expression {
    var type: Type = Type.VOID
}

// թվային հաստատուն
class Numeric(val value: Double) : Expression() {
    init { type = Type.REAL }

    override fun toString(): String = value.toString()
}

// տեքստային հաստատուն
class Text(val value: String) : Expression() {
    init { type = Type.TEXT }

    override fun toString(): String = value
}

// փոփոխական
class Variable(val sym: Symbol) : Expression() {
    init { type = sym.type }

    override fun toString(): String = sym.id
}

// տրամաբանական
class Logical(val value: String) : Expression() {
    init { type = Type.BOOL; }

    override fun toString(): String = value
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
class Unary(val operation: Operation, val right: Expression) : Expression() {
    init { type = typeOf(operation, right.type) }

    override fun toString(): String =
        String.format("(%s %s)", operation, right)
}

// բինար գործողություն
class Binary(val operation: Operation, val left: Expression, val right: Expression) : Expression() {
    init { type = typeOf(operation, left.type, right.type) }

    override fun toString(): String =
        String.format("(%s %s %s)", left, operation, right)
}

// ֆունկցիա ալգորիթմի կանչ
class Apply(val callee: Signature, val arguments: List<Expression>) : Expression() {
    init { type = callee.resultType }
}
