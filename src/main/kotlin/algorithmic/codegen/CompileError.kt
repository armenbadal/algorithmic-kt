package algorithmic.codegen

class CompileError(msg: String) : Exception(msg) {
    override fun toString(): String =
        "ՍԽԱԼ։ $message"
}
