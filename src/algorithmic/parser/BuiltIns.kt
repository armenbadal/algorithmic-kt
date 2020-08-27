package algorithmic.parser

import algorithmic.engine.Signature
import algorithmic.engine.Symbol
import algorithmic.engine.Type

fun builtIns(): MutableList<Signature> =
    mutableListOf(
        Signature("ներմուծելՏեքստ", Type.TEXT, arrayListOf(Type.TEXT)),
        Signature("ներմուծելԻրական", Type.REAL, arrayListOf(Type.TEXT)),
        Signature("արտածելՏեքստ", Type.VOID, arrayListOf(Type.TEXT)),
        Signature("արտածելԻրական", Type.VOID, arrayListOf(Type.REAL))
    )
