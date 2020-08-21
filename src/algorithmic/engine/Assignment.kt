package algorithmic.engine

class Assignment constructor(val sym: Symbol, val value: Expression) : Statement {
    override fun toString(): String =
        "$sym := $value"
}