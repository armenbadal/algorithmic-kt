package algorithmic.ast

class Symbol(val id: String, val type: Type) {
    override fun toString(): String =
        "Symbol(type=${type}, name=$id)"

    override fun equals(other: Any?): Boolean =
            (other is Symbol) && (other.id == id) && (other.type == type)
}
