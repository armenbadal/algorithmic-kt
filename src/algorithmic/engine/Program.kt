package algorithmic.engine

class Program constructor(val name: String) {
    val algorithms = mutableListOf<Algorithm>()

    fun add(alg: Algorithm): Algorithm
    {
        algorithms.add(alg)
        return algorithms.last()
    }
}
