package algorithmic.parser

import algorithmic.engine.Signature
import algorithmic.engine.Type

fun builtIns() =
    mutableMapOf(
        "ներմուծելՏեքստ" to Signature("inputText", Type.TEXT, arrayListOf(Type.TEXT)),
        "ներմուծելԻրական" to Signature("inputReal", Type.REAL, arrayListOf(Type.TEXT)),
        "արտածելՏեքստ" to Signature("printText", Type.VOID, arrayListOf(Type.TEXT)),
        "արտածելԻրական" to Signature("printReal", Type.VOID, arrayListOf(Type.REAL)),

        "sin" to Signature("Math.sin", Type.REAL, arrayListOf(Type.REAL)),
        "cos" to Signature("Math.cos", Type.REAL, arrayListOf(Type.REAL))
    )
