package algorithmic.engine

class Symbol(val id: String, val type: Type) {
    enum class Type(val text: String) {
        VOID(""),
        NUMBER("ԹԻՎ"),
        TEXT("ՏԵՔՍՏ")
    }

    override fun toString(): String =
        "Symbol(type=${type.text}, name=$id)"

    override fun equals(other: Any?): Boolean =
            (other is Symbol) && (other.id == id) && (other.type == type)
}
