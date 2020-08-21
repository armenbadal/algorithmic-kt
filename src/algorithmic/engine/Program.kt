package algorithmic.engine

class Program constructor(sourceFile: String) {
    private val algorithms = mutableListOf<Algorithm>()

    fun add(alg: Algorithm): Algorithm
    {
        algorithms.add(alg)
        return algorithms.last()
    }
}
