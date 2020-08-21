package algorithmic.engine

interface Statement
typealias StatementList = ArrayList<Statement>

// վերագրման կառուցվածքը
class Assignment constructor(val sym: Symbol, val value: Expression) : Statement {
    override fun toString(): String =
            "$sym := $value"
}

// ճյուղավորման կառուցվածքը
class Branching constructor(val condition: Expression, val decision: StatementList) : Statement {
}

// կրկնման կառուցվածքը
class Repetition constructor(val condition: Expression, val body: StatementList) {
}

// արդյունքի կառուցվածքը
class Result constructor(val value: Expression) : Statement {
}
