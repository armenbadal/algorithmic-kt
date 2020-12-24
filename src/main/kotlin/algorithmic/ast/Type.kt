package algorithmic.ast

interface Type {}

object VOID : Type {
    override fun toString(): String = ""
}

enum class Scalar(val text: String) : Type {
    REAL("իրական"),
    TEXT("տեքստ"),
    BOOL("բուլյան");

    override fun toString(): String = text
}

class Array(val size: Int, val base: Type) : Type {
    override fun equals(other: Any?): Boolean
    {
        if( other is Array )
            return other.size == size && other.base == base

        return false
    }

    override fun toString(): String =
        "$base աղս [$size]"
}
