package algorithmic.parser

import algorithmic.ast.Signature
import algorithmic.ast.Type

fun builtIns() =
    mutableMapOf(
        "ներմուծելԲուլյան" to Signature("Algorithmic.inputBoolean", Type.BOOL, arrayListOf(Type.TEXT)),
        "ներմուծելՏեքստ" to Signature("Algorithmic.inputText", Type.TEXT, arrayListOf(Type.TEXT)),
        "ներմուծելԻրական" to Signature("Algorithmic.inputReal", Type.REAL, arrayListOf(Type.TEXT)),

        "արտածելԲուլյան" to Signature("Algorithmic.printBoolean", Type.VOID, arrayListOf(Type.BOOL)),
        "արտածելՏեքստ" to Signature("Algorithmic.printText", Type.VOID, arrayListOf(Type.TEXT)),
        "արտածելԻրական" to Signature("Algorithmic.printReal", Type.VOID, arrayListOf(Type.REAL)),

        "sin" to Signature("Math.sin", Type.REAL, arrayListOf(Type.REAL)),
        "cos" to Signature("Math.cos", Type.REAL, arrayListOf(Type.REAL))
    )
