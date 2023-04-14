package algorithmic.ast

interface Type

object VOID : Type {
    override fun toString(): String = ""
}

enum class Scalar(val text: String) : Type {
    REAL("իրական"),
    TEXT("տեքստ"),
    BOOL("բուլյան");

    override fun toString(): String = text

    companion object {
        fun from(text: String): Scalar =
            when(text) {
                "ԲՈՒԼՅԱՆ" -> Scalar.BOOL
                "ԻՐԱԿԱՆ" -> Scalar.REAL
                "ՏԵՔՍՏ" -> Scalar.TEXT
                else -> throw UnknownType("Անծանոթ տիպ «$text»։")
            }
    }
}

class Array(val size: Int, val base: Type) : Type {
    override fun equals(other: Any?): Boolean =
        other is Array && other.size == size && other.base == base

    override fun toString(): String =
            "$base աղս [$size]"
}
