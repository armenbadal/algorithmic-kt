package algorithmic.ast

// արտահայտությունների բազային դաս
sealed class Expression(val type: Type)

// թվային հաստատուն
class Numeric(val value: Double) : Expression(Scalar.REAL)

// տեքստային հաստատուն
class Text(val value: String) : Expression(Scalar.TEXT)

// տրամաբանական
class Logical(val value: Boolean) : Expression(Scalar.BOOL)

// փոփոխական
class Variable(val sym: Symbol) : Expression(sym.type)

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
}

// ունար գործողություն
class Unary(val operation: Operation, type: Type, val right: Expression) : Expression(type)

// բինար գործողություն
class Binary(val operation: Operation, type: Type, val left: Expression, val right: Expression) : Expression(type)

// ֆունկցիա ալգորիթմի կանչ
class Apply(val callee: Signature, val arguments: List<Expression>) : Expression(callee.resultType)
