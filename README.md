# Ալգորիթմական լեզու

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
```
