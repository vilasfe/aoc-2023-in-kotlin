fun main() {
    fun part1(input: List<String>): Int {
        // each line contains digits + char noise
        // combine the first and last digit on each line to make a 2 digit num
        // line might have only one number, so first == last, repeat the digit
        val data: List<Int> = input.map { it ->
            val firstDigit = it.first { c -> c.isDigit() }
            val lastDigit = it.last { c -> c.isDigit() }
            val num = firstDigit.digitToInt() * 10 + lastDigit.digitToInt()
            num
        }

        // sum the results
        return data.sum()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
