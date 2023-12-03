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

    /**
     * replace multiple values in a string
     */
    fun String.replaceDay01(mapping: Map<String, String>): String {
        var str = this

        var found = str.indexOfAny(mapping.keys)
        while (found != -1) {
            loop@ for((k, v) in mapping) {
                if (str.substring(found).startsWith(k)) {
                    val sb = StringBuilder(str)
                    sb.setCharAt(found, v.get(0))
                    str = sb.toString()
                    break@loop
                }
            }
            found = str.indexOfAny(mapping.keys)
        }

        return str
    }

    fun part2(input: List<String>): Int {
        // Same as part 1, but now they can spell out the digits
        val d = mapOf("one" to "1", "two" to "2", "three" to "3",
                      "four" to "4", "five" to "5", "six" to "6",
                      "seven" to "7", "eight" to "8", "nine" to "9")
        val data: List<Int> = input.map { it ->
            val mapped = it.replaceDay01(d)
            println(mapped)
            val firstDigit = mapped.first { c -> c.isDigit() }
            val lastDigit = mapped.last { c -> c.isDigit() }
            val num = firstDigit.digitToInt() * 10 + lastDigit.digitToInt()
            println(num)
            num
        }

        return data.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)
    val testInput2 = readInput("Day01_test2")
    println(part2(testInput2))
    check(part2(testInput2) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
