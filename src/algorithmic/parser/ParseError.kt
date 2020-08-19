package algorithmic.parser

data class ParseError constructor(val msg: String) : Exception(msg)
