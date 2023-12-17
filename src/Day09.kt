fun main() {
    fun part1(input: List<String>): Int {
        val historyInput = input.map {
            it.split("\\s+".toRegex()).map { i -> i.toInt() }
        }

        var total = 0
        for (h in historyInput) {
            var differences = mutableListOf<List<Int>>()
            differences.add(h)
            while (! differences.last().all { it == 0 }) {
                differences.add(differences.last().zipWithNext() { s1, s2 -> s2 - s1 })
            }
            var nextVal = 0
            for (d in differences.lastIndex-1 downTo 0) {
                nextVal += differences[d].last()
            }
            total += nextVal
        }

        return total
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
