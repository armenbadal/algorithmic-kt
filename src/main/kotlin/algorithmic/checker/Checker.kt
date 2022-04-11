package algorithmic.checker

import algorithmic.ast.*

class Checker(private val program: Program) {

    fun check() {
        program.algorithms.forEach { check(it) }
    }

    private fun check(node: Algorithm) {
        //
    }

    private fun check(node: Statement) {
        //
    }

    private fun check(node: Expression) {
        //
    }
}