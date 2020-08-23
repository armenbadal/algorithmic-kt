package algorithmic.engine

interface Statement

// հրամանների շարք
class Sequence : Statement {
    val items = arrayListOf<Statement>()
}

// վերագրման կառուցվածքը
class Assignment(val sym: Symbol, val value: Expression) : Statement {
    override fun toString(): String = "${sym.name} := $value"
}

// ճյուղավորման կառուցվածքը
class Branching(val condition: Expression, val decision: Statement, var alternative: Statement) : Statement {
}

// կրկնման կառուցվածքը
class Repetition(val condition: Expression, val body: Statement) : Statement {}

// արդյունքի կառուցվածքը
class Result(val value: Expression) : Statement {
    override fun toString(): String =
        String.format("ԱՐԴՅՈՒՆՔ %s", value)
}

// ալգորիթմի կիրառում
class Call(val arguments: ArrayList<Expression>) : Statement {}
