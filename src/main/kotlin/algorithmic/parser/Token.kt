package algorithmic.parser

@Suppress("NonAsciiCharacters")
enum class Token {
    ԱՆԾԱՆՈԹ,

    ԹՎԱՅԻՆ,
    ՏԵՔՍՏԱՅԻՆ,
    ՃԻՇՏ,
    ԿԵՂԾ,
    ԱՆՈՒՆ,

    ԾՐԱԳԻՐ,
    ԳՐԱԴԱՐԱՆ,
    ԿԱՏԱՐԵԼ,
    ԱԼԳՈՐԻԹՄ,
    ՍՏՈՐԵՎ,
    ՍԿԻԶԲ,
    ՎԵՐՋ,
    ԻՐԱԿԱՆ,
    ՏԵՔՍՏ,
    ԲՈՒԼՅԱՆ,
    ԱՂՅՈՒՍԱԿ,
    ԵԹԵ,
    ԱՊԱ,
    ԻՍԿ,
    ԱՅԼԱՊԵՍ,
    ԱՎԱՐՏ,
    ՔԱՆԻ,
    ԴԵՌ,
    ԱՐԴՅՈՒՆՔ,

    ADD, // +
    SUB, // -
//    AMP, // &
    MUL, // *
    DIV, // /
    MOD, // \
//    POW, // ^

    EQ, // =
    NE, // <>
    GT, // >
    GE, // >=
    LT, // <
    LE, // <=

    ԵՎ,
    ԿԱՄ,
    ՈՉ,

    ՎԵՐԱԳՐԵԼ, // :=
    ՎԵՐՋԱԿԵՏ, // :, ։
    ՍՏՈՐԱԿԵՏ,
    ԿԵՏ_ՍՏՈՐԱԿԵՏ, // ;
    ՁԱԽ_ՓԱԿԱԳԻԾ, // (
    ԱՋ_ՓԱԿԱԳԻԾ,  // )
    ՁԱԽ_ԻՆԴԵՔՍ, // [
    ԱՋ_ԻՆԴԵՔՍ,  // ]
    ՀԱՐՑԱԿԱՆ, // ?

    ՖԱՅԼԻ_ՎԵՐՋ
}