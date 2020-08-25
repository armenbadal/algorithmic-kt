package algorithmic.engine

// արտահայտությունների ինտերֆեյս
interface Expression {
    fun type(): Symbol.Type
}

// թվային հաստատուն
class Numeric(val value: Double) : Expression {
    override fun type(): Symbol.Type = Symbol.Type.NUMBER

    override fun toString(): String = value.toString()
}

// տեքստային հաստատուն
class Text(val value: String) : Expression {
    override fun type(): Symbol.Type = Symbol.Type.TEXT

    override fun toString(): String = value
}

// փոփոխական
class Variable(val sym: Symbol) : Expression {
    override fun type(): Symbol.Type = sym.type

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
    LE("<=");

    override fun toString(): String = text
}

// ունար գործողություն
class Unary(val operation: Operation, val subexpr: Expression) : Expression {
    override fun type(): Symbol.Type = Symbol.Type.VOID

    override fun toString(): String =
        String.format("(%s %s)", operation, subexpr)
}

// բինար գործողություն
class Binary(val operation: Operation, val left: Expression, val right: Expression) : Expression {
    override fun type(): Symbol.Type = Symbol.Type.VOID

    override fun toString(): String =
        String.format("(%s %s %s)", left, operation, right)
}

// ֆունկցիա ալգորիթմի կանչ
class Apply(val callee: Signature, val arguments: List<Expression>) : Expression {
    override fun type(): Symbol.Type = callee.resultType
}
