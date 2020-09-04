package algorithmic.ast

sealed class Statement

// հրամանների շարք
class Sequence : Statement() {
    val items = arrayListOf<Statement>()
}

// վերագրման կառուցվածքը
class Assignment(val sym: Symbol, val value: Expression) : Statement()

// ճյուղավորման կառուցվածքը
class Branching(val condition: Expression, val decision: Statement, val alternative: Statement) : Statement()

// կրկնման կառուցվածքը
class Repetition(val condition: Expression, val body: Statement) : Statement()

// արդյունքի կառուցվածքը
class Result(val value: Expression) : Statement()

// ալգորիթմի կիրառում
class Call(callee: Signature, arguments: List<Expression>) : Statement() {
    val apply = Apply(callee, arguments)
}
