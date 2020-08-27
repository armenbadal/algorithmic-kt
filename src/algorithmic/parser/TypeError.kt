package algorithmic.parser

class TypeError constructor(msg: String, private val line: Int) : Exception(msg) {
    override fun toString(): String =
            "ՍԽԱԼ [$line]։ $message"
}
