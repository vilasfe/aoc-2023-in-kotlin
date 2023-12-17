fun main() {

    fun extrapolate(seq: List<Int>): Pair<Int, Int> {
        var differences = mutableListOf<List<Int>>()
        differences.add(seq)
        while (! differences.last().all { it == 0 }) {
            differences.add(differences.last().zipWithNext() { s1, s2 -> s2 - s1 })
        }
        var nextVal = 0
        var prevVal = 0
        for (d in differences.lastIndex-1 downTo 0) {
            nextVal += differences[d].last()
            prevVal = differences[d].first() - prevVal
        }
        return Pair(prevVal, nextVal)
    }

    fun part1(input: List<String>): Int {
        val historyInput = input.map {
            it.split("\\s+".toRegex()).map { i -> i.toInt() }
        }

        val total = historyInput.fold(0) { sum, it -> sum + extrapolate(it).second }

        return total
    }

    fun part2(input: List<String>): Int {
        val historyInput = input.map {
            it.split("\\s+".toRegex()).map { i -> i.toInt() }
        }

        val total = historyInput.fold(0) { sum, it -> sum + extrapolate(it).first }

        return total
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)

    val input = readInput("Day09")
    part1(input).println()

    check(part2(testInput) == 2)
    part2(input).println()
}
