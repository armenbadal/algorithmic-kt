package algorithmic.parser

import algorithmic.engine.Signature
import algorithmic.engine.Symbol

fun builtIns(): MutableList<Signature> =
    mutableListOf(
        Signature("ներմուծել", Symbol.Type.TEXT, arrayListOf(Symbol.Type.TEXT)),
        Signature("ներմուծել", Symbol.Type.REAL, arrayListOf(Symbol.Type.TEXT)),
        Signature("արտածել", Symbol.Type.VOID, arrayListOf(Symbol.Type.TEXT)),
        Signature("արտածել", Symbol.Type.VOID, arrayListOf(Symbol.Type.REAL))
    )
