package algorithmic.engine

class Symbol(val type: Type, val name: String) {
    enum class Type(val text: String) {
        VOID(""),
        NUMBER("ԹԻՎ"),
        TEXT("ՏԵՔՍՏ"),
        ALGORITHM("ԱԼԳ")
    }

    override fun toString(): String =
        "Symbol(type=${type.text}, name=$name)"
}
