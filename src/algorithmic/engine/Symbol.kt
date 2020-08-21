package algorithmic.engine

class Symbol constructor(val type: Type, val name: String) {
    enum class Type constructor(val text: String) {
        VOID(""),
        NUMBER("ԹԻՎ"),
        TEXT("ՏԵՔՍՏ"),
        ALGORITHM("ԱԼԳ")
    }

    override fun toString(): String =
        "Symbol(type=${type.text}, name=$name)"
}
