package algorithmic.engine

class Symbol(val name: String, val type: Type) {
    enum class Type(val text: String) {
        VOID(""),
        NUMBER("ԹԻՎ"),
        TEXT("ՏԵՔՍՏ")
    }

    override fun toString(): String =
        "Symbol(type=${type.text}, name=$name)"
}
