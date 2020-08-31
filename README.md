# Ալգորիթմական լեզու

Սա _Ալգորիթմական լեզուն_ է, որն ինչ-որ ժամանակ օգտագործվում էր դպրոցական 
«Ինֆորմատիկա և հաշվողական տեխնիկայի հիմունքներ» առարկայի դասագրքերում։ 
Որքան որ ես տեղյակ եմ, այս լեզուն _հայերեն_ իրականացում չի ունեցել. այն 
օգտագործվել է միայն թղթին կամ գրատախտակին ալգորիթմների նկարագրության և 
ուսումնասիրության համար։

Իմ այս նախագիծը նպատակ չունի տրամադրել Ալգորիթմական լեզվի լիարժեք իրականացում 
ինչպիսին է, օրինակ, նույն լեզվի ռուսական [КуМир](https://www.niisi.ru/kumir/) 
իրականացումը։ Սա պարզապես խաղալիք նախագիծ է, որը ես օգտագործել եմ նախ՝ Կոտլին
([Kotlin](https://kotlinlang.org/)) լեզուն սովորելու, ապա՝ Ջավա վիտուալ մեքենայի
(JVM) բայթ-կոդի հետ աշխատող [BCEL](https://commons.apache.org/proper/commons-bcel/)
գրադարանին ծանոթանալու համար։ 



__Քերականություն__

```
Program = { Algorithm }.
Algorithm = 'ԱԼԳՈՐԻԹՄ' [Type] ԱՆՈՒՆ [Parameters]
            DeclarationList
            'ՍԿԻԶԲ' [StatementList] 'ՎԵՐՋ'.
Parameters = '(' Type ԱՆՈՒՆ {',' Type ԱՆՈՒՆ} ')'.
DeclarationList = Declaration {';' Declaration}.
Declaration = Type ԱՆՈՒՆ {',' ԱՆՈՒՆ}.
Type = 'ԻՐԱԿԱՆ' | 'ՏԵՔՍՏ'. 
StatementList = Statement {';' Statement}.
Statement = ԱՆՈՒՆ ':=' Expression
    | 'ԵԹԵ' Expression 'ԱՊԱ' StatementList
      {'ԻՍԿ' 'ԵԹԵ' Expression 'ԱՊԱ' StatementList}
      ['ԱՅԼԱՊԵՍ' StatementList] 'ԱՎԱՐՏ'
    | 'ՔԱՆԻ' 'ԴԵՌ' Expression 'ԱՊԱ' StatementList 'ԱՎԱՐՏ'
    | 'ԱՐԴՅՈՒՆՔ' Expression
    | ԱՆՈՒՆ '(' [ExpressionList] ')'.
ExpressionList = Expression {',' Expression}.
Expression = Disjunction.
Disjunction = Conjunction {'ԿԱՄ' Conjunction}.
Conjunction = Equality {'ԵՎ' Equality}.
Equality = Comparison [('=' | '<>') Comparison].
Comparison = Addition [('>' | '>=' | '<' | '<=') Addition].
Addition = Multiplication {('-' | '+') Multiplication}.
Multiplication = Factor {('*' | '/') Factor}.
Factor = NUMBER | TEXT | ԱՆՈՒՆ
    | ('+' | '-') Factor
    | ԱՆՈՒՆ '(' [ExpressionList] ')'
    | '(' Expression ')'
    | 'ՃԻՇՏ' | 'ԿԵՂԾ'.
```
