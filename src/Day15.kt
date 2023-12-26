fun main() {
    fun reindeerHash(input: String): Int {
        val total = input.toList().fold(0) { tot, it -> ((tot + it.code) * 17) % 256 }
        return total
    }

    fun part1(input: List<String>): Int {
        val parsed = input[0].split(",")

        val hashed = parsed.map { reindeerHash(it) }

        return hashed.sum()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    println("Part 1 test: ${part1(testInput)}")
    check(part1(testInput) == 1320)

    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}
