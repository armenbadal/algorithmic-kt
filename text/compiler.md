# Բայթ-կոդի ստեղծումը

Ալգորիթմական լեզվի կոմպիլյատորը _բայթ-կոդ_ (bytecode) է ստեղծում Ջավա վիրտուալ 
մեքենայի (Java Virtual Machine, JVM) համար։ `ByteCode` դասը նախատեսված է աբստրակտ 
քերականական ծառից բայթ-կոդ՝ `*.class` ֆայլ, գեներացնելու համար։ Որպես օժանդակ 
գրադարան ես ընտրել եմ [Apache Commons BCEL™](https://commons.apache.org/proper/commons-bcel/)-ը։

__Ծրագիրը__ թարգմանվում է default փաթեթի public դասի։ Այդ դասի անունը որոշվում 
է `ԾՐԱԳԻՐ` ծառայողական բառից հետո գրված իդենտիֆիկատորով։ Ծրագրի մարմինը
թարգմանվում է նույն դասի `public static void main(String[] args)` մեթոդի։ Օրինակ, 
եթե հետևյալ ծրագիրը թարգմանենք `algwrithmic-kt` կոմպիլյատորով.

```
ԾՐԱԳԻՐ Ողջունող
ՍԿԻԶԲ
    արտածելՏեքստ(«Ողջո՜ւյն, աշխա՛րհ։»)
ՎԵՐՋ
```  

ապա ստացված `*.class` ֆայլը վերծանենք `javap -c` հրամանով, ապա կստանանք.

```
public class Ողջունող {
  public static void main(java.lang.String[]);
    Code:
       0: ldc           #8                  // String Ողջո՜ւյն, աշխա՛րհ։
       2: invokestatic  #14                 // Method Algorithmic.printText:(Ljava/lang/String;)V
       5: return
}
```

__Ալգորիթմը__ թարգմանվում է public static մեթոդի։ Օրինակ, եթե ծրագրում սահմանված է
հետևյալ ալգորիթմը.

```
...
    { Շրջանի պարագիծը հաշվող ալգորիթմ }
    ԱԼԳՈՐԻԹՄ ԻՐԱԿԱՆ պարագիծ(ԻՐԱԿԱՆ շառավիղ)
        ԻՐԱԿԱՆ π
    ՍԿԻԶԲ
    |    π := 3.1415;
    |    ԱՐԴՅՈՒՆՔ 2 * π * շառավիղ
    ՎԵՐՋ
...
```

`algorithmic-kt` կոմպիլյատորը գեներացնում այսպիսի բայթ-կոդ.

```
...
  public static double պարագիծ(double);
    Code:
       0: dconst_0
       1: dstore_2
       2: ldc2_w        #7                  // double 3.1415d
       5: dstore_2
       6: ldc2_w        #16                 // double 2.0d
       9: dload_2
      10: dmul
      11: dload_0
      12: dmul
      13: dreturn
...
```
