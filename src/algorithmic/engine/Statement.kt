package algorithmic.engine

interface Statement
typealias StatementList = ArrayList<Statement>

// վերագրման կառուցվածքը
class Assignment(val sym: Symbol, val value: Expression) : Statement {
    override fun toString(): String = "${sym.name} := $value"
}

// ճյուղավորման կառուցվածքը
class Branching(val condition: Expression, val decision: StatementList) : Statement {
}

// կրկնման կառուցվածքը
class Repetition(val condition: Expression, val body: StatementList) {
}

// արդյունքի կառուցվածքը
class Result(val value: Expression) : Statement {
    override fun toString(): String =
        String.format("ԱՐԴՅՈՒՆՔ %s", value)
}
