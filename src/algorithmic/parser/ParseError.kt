package algorithmic.parser

class ParseError constructor(msg: String, private val line: Int) : Exception(msg) {
    override fun toString(): String =
        "ՍԽԱԼ [$line]։ $message"
}
