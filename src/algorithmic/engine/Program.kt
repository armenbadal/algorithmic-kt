package algorithmic.engine

class Program constructor(val name: String) {
    private val algorithms = mutableListOf<Algorithm>()

    fun add(alg: Algorithm): Algorithm
    {
        algorithms.add(alg)
        return algorithms.last()
    }
}
