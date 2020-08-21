package algorithmic.engine

// արտահայտությունների ինտերֆեյս
interface Expression

// թվային հաստատուն
class Numeric(val value: Double) : Expression {
    override fun toString(): String = value.toString()
}

// տեքստային հաստատուն
class Text(val value: String) : Expression {
    override fun toString(): String = value
}

// փոփոխական
class Variable(val sym: Symbol) : Expression {
    override fun toString(): String = sym.name
}

enum class Operation(val text: String) {
    ADDITION("+"),
    SUBTRACTION("-"),
    MULTIPLICATION("*"),
    DIVISION("/");

    override fun toString(): String = text
}

// ունար գործողություն


// բինար գործողություն
class Binary(val operation: Operation, val left: Expression, val right: Expression) : Expression {
    override fun toString(): String =
        String.format("(%s %s %s)", left, operation, right)
}
