package algorithmic.parser

import algorithmic.ast.Scalar
import algorithmic.ast.Signature
import algorithmic.ast.Type
import algorithmic.ast.VOID

fun builtIns() =
    mutableMapOf(
        "ներմուծելԲուլյան" to Signature("Algorithmic.inputBoolean", Scalar.BOOL, arrayListOf(Scalar.TEXT)),
        "ներմուծելՏեքստ" to Signature("Algorithmic.inputText", Scalar.TEXT, arrayListOf(Scalar.TEXT)),
        "ներմուծելԻրական" to Signature("Algorithmic.inputReal", Scalar.REAL, arrayListOf(Scalar.TEXT)),

        "արտածելԲուլյան" to Signature("Algorithmic.printBoolean", VOID, arrayListOf(Scalar.BOOL)),
        "արտածելՏեքստ" to Signature("Algorithmic.printText", VOID, arrayListOf(Scalar.TEXT)),
        "արտածելԻրական" to Signature("Algorithmic.printReal", VOID, arrayListOf(Scalar.REAL)),

        "sin" to Signature("Math.sin", Scalar.REAL, arrayListOf(Scalar.REAL)),
        "cos" to Signature("Math.cos", Scalar.REAL, arrayListOf(Scalar.REAL))
    )
