# Վերլուծության ծառ

Ալգորիթմական լեզվով գրված ծրագիրը կոմպիլյացնելիս շարահյուսական վերլուծիչը
կառուցում է _աբստրակտ քերականական ծառ_ (_abstract syntax tree_, AST)։ Այդ 
ծառի հանգույցները նկարագրող դասերը սահմանված են `algorithmic.ast` փաթեթում։
Ամենախոշոր միավորը _ծրագիրը_ ներկայացնող դասն է.

```kotlin
class Program(
        val name: String,                // ծրագրի անունը
        val algorithms: List<Algorithm>, // ալգորիթմների ցուցակը
        val body: Statement              // մարմինը՝ պարտադիր կատարվող բլոկը
    )
```

Ծրագիրն իր մեջ պարունակում է _ալգորիթմներ_, որոնք ներկայացնում են `Algorithm` դասով.

```kotlin
class Algorithm(
        val name: String,             // անունը
        val returnType: Type,         // արդյունքի տիպը
        val parameters: List<Symbol>, // պարամետրերը
        val body: Statement           // մարմինը
    ) {
        val locals = arrayListOf<Symbol>() // լոկալ անունները
}
```

Թարգմանության ժամանակ ալգորիթմների կիրառությունները (կանչերը) ապահովելու համար 
օգտագործվում է ալգորիթմի տիպը նկարագրող `Signature` դասը.

```kotlin
class Signature(
        val name: String,               // ալգորիթմի անունը
        val resultType: Type,           // արդյուքի տիպը
        val parametersTypes: List<Type> // պարամետրերի տիպերը
    ) { /* ... */ }
```

Փոփոխականների (անունների) տիպերի ներկայացման համար սահմանված է `Type` թվարկումը.

```kotlin
enum class Type(val text: String) {
    VOID(""),        // «դատարկ», արդյունք չստեղծող ալգորիթմների համար 
    REAL("ԻՐԱԿԱՆ"),  // կրկնակի ճշտության իրական թվեր
    TEXT("ՏԵՔՍՏ"),   // տեքստային մեծություններ
    BOOL("ԲՈՒԼՅԱՆ")  // տրամաբանական մեծություններ
}
```

__Ղեկավարող կառուցվածքների__՝ հրամանների ընտանիքի հիմքը `Statement` _sealed_[^1] 
դասն է. բոլոր հրամանները այս դասի ժառանգներն են։

```kotlin
sealed class Statement
```

Հրամանների _հաջորդականությունը_՝ `Sequence` պարզապես `Statement`-ների ցուցակ է.

```kotlin
class Sequence : Statement() {
    val items = arrayListOf<Statement>()
}
``` 

_Վերագրման_ հրամանը երկու անդամ ունի՝ սիմվոլ և վերագրվող արժեք.

```kotlin
class Assignment(val sym: Symbol, val value: Expression) : Statement()
```

Սիմվոլը՝ որին պիտի վերագրվի `value` արժեքը սահմանված է `Symbol` դասով.

```kotlin
class Symbol(
        val id: String, // անունը
        val type: Type  // տիպը
    )
```
 

_Ճյուղավորման_ կառուցվածքում պայմանն է՝ `condition`, պայմանի դրական լինելու դեպքում 
կատարվելու համար սահմանված հրամանների բլոկը՝ `decision` և այլընտրանքային հրամանների
բլոկը՝ `alternative`, որը պետք է կատարվի երբ պայմանը բացասական է։
 

```kotlin
class Branching(
        val condition: Expression,  // պայման
        val decision: Statement,    // դրական պայմանի հրամանները
        val alternative: Statement  // բացասական պայմանի հրամանները
    ) : Statement()
```

_Կրկնության_ կառուցվածքը նախապայմանով ցիկլ է. քանի դեռ `condition` պայմանը դրական է,
պետք է կատարվի `body` հրամանների բլոկը։

```kotlin
class Repetition(
        val condition: Expression, // կրկնության պայման
        val body: Statement        // հրամանների կրկնվող բլոկ
    ) : Statement()
```

Ալգորիթմի կատարման արդյունքը ներկայացնում է `Result` կառուցվածքը (նույնն է ինչ դասական 
`return` հրամանը)։

```kotlin
class Result(
        val value: Expression // ալգորիթմի արդյունքը
    ) : Statement()
```

Ալգորիթմի կիրառման ու որպես ֆունկցիայի կանչի կառուցվածքներն իրար նման են։ Հենց դրա 
համար էլ `Call` կառուցվածքն իրականացրել եմ որպես `Apply` արտահայտության _թաղանթ_ 
(կամ՝ wrapper):

```kotlin
class Call(
        callee: Signature,           // կիրառվղ ալգորիթմի նկարագրիչը
        arguments: List<Expression>  // կիրառման արգումենտները
    ) : Statement() {
    val apply = Apply(callee, arguments)
}
```

__Արտահայտությունների__ ընտանիքի հիմքում `Expression` sealed դասն է։ Այն ունի միակ 
հատկություն՝ արտահայտության տիպը։ 

```kotlin
sealed class Expression(
        val type: Type  // արտահայտության տիպը
    )
```

Թվային (իրական), տեքստային և տրամաբանական հաստատունները ներկայացված են համապատասխանաբար
`Numeric`, `Text` և `Logical` դասերով։ Դրանցից ամեն մեկը `Expression` դասի կոնստրուկտորին
է փոխանցում իր տիպը։

```kotlin
class Numeric(val value: Double) : Expression(Type.REAL) 
class Text(val value: String) : Expression(Type.TEXT) 
class Logical(val value: String) : Expression(Type.BOOL)
```

Ալգորիթմում հայտարարված ամեն մի փոփոխականի համար քերականական ծառում ստեղծվում է `Variable`
տիպի օբյեկտ.

```kotlin
class Variable(
        val sym: Symbol       // Փոփոխականի անունն ու տիպը 
    ) : Expression(sym.type)
```

Արտահայտություններն իրար են կապվում և ստեղծվում են ավելի բարդ արտահայտություններ՝ 
`Operation` թվարկմամբ սահմանված գործողություններով։

```kotlin
enum class Operation(val text: String) {
    ADD("+"),  // գումարում
    SUB("-"),  // հանում, նաև ունար մինուս
    MUL("*"),  // բազմապատկում
    DIV("/"),  // բաժանում

    EQ("="),   // հավասար է
    NE("<>"),  // հավասա չէ
    GT(">"),   // մեծ է
    GE(">="),  // մեծ է կամ հավասար
    LT("<"),   // փոքր է
    LE("<="),  // փոքր է կամ հավասար

    AND("ԵՎ"), // կոնյունկցիա
    OR("ԿԱՄ"), // դիզյունկցիա
    NOT("ՈՉ"); // ժխտւմ (ունար)
}
```

Միտեղանի (ունար) գործողության դասը երկու անդամ ունի. գործողության կոդը՝ `operation` 
և ենթակա արտահայտությունը՝ `right`։ `Unary` դասի կոնստրուկտորը ստանում է նաև `type` 
արժեքը, որը փոխանցվում է `Expression`-ի կոնստրուկտորին։

```kotlin
class Unary(
        val operation: Operation,
        type: Type,
        val right: Expression
    ) : Expression(type)
```

Երկտեղանի (բինար) գործողության մոդելը ներառում է գործողության կոդը, ինչպես նաև գործողության
նշանի աջ ու ձախ կողմերում գրածված արտահայտությունների ծառերը։ Այս դեպքում էլ նույնպես `type`
արժեքը փոխանցվում է `Expression`-ին։

```kotlin
class Binary(
        val operation: Operation,
        type: Type,
        val left: Expression,
        val right: Expression
    ) : Expression(type)
```

Բաղադրյալ արտահայտություններում կարող են մասնակցել նաև ֆունկցիա-ալգորիթմների կանչեր՝ ներկայացված
`Apply` դասով։ Սրա երկու անդամներն են կանչվող ալգորիթմի նկարագրիչը և կանչի արգումենտները։

```kotlin
class Apply(
        val callee: Signature,           // կանչվող ալգորիթմի նկարագրիչ
        val arguments: List<Expression>  // կանչի արգումենտներ
    ) : Expression(callee.resultType)
```

----
[^1]: Առայժմ չեմ գտնում `sealed class` տերմինի իմաստային թարգմանությունը։ Միգուցե՝ «փակ դաս»։
