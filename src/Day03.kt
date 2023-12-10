fun main() {

    data class CandidateNumber(val value: Int, val colRange: IntRange, val row: Int)
    data class Symbol(val value: Char, val col: Int, val row: Int)

    data class NumbersAndSymbols(val numbers: List<CandidateNumber>, val symbols: List<Symbol>)
    fun parseGrid(input: List<List<Char>>): NumbersAndSymbols {
        val x_index = input.indices
        val y_index = input[0].indices
        var numbers = mutableListOf<CandidateNumber>()
        var symbols = mutableListOf<Symbol>()

        // find all the symbols and their locations
        for (r in x_index) {
            val locations = input[r].toList().indexesOf { !it.isDigit() && it != '.' }
            for (l in locations) {
                symbols.add(Symbol(input[r][l], l, r))
            }
        }

        // find all the numbers and their locations
        for (r in x_index) {
            var c_start = -1
            var c_end = x_index.max()
            for (c in y_index) {
                if (c_start == -1 && input[r][c].isDigit()) {
                    c_start = c
                }
                if (c_start > -1 && !input[r][c].isDigit()) {
                    c_end = c
                    val num = input[r].subList(c_start, c_end).joinToString(separator="").toInt()
                    numbers.add(CandidateNumber(num, c_start..c_end, r))
                    c_end = x_index.max()
                    c_start = -1
                }
            }
            // Check for number that ends the line
            if (c_start > -1) {
                val num = input[r].subList(c_start, c_end+1).joinToString(separator="").toInt()
                numbers.add(CandidateNumber(num, c_start..c_end, r))
            }
        }
        return NumbersAndSymbols(numbers, symbols)
    }

    fun part1(input: List<String>): Int {
        val myGrid = input.map { it.toList() }
        val (numbers, symbols) = parseGrid(myGrid)

        var partNumbers = mutableListOf<Int>()

        // For each number, look for symbols that are within 1 in any direction of its range
        for (n in numbers) {
            val col_start = n.colRange.min()
            val col_end = n.colRange.max()
            for (s in symbols) {
                if (s.row >= n.row - 1 && s.row <= n.row + 1) {
                    // check for overlap
                    if (s.col >= col_start - 1 && s.col <= col_end) {
                        partNumbers.add(n.value)
                    }
                }
            }
        }

        return partNumbers.sum()
    }

    fun part2(input: List<String>): Int {
        val myGrid = input.map { it.toList() }
        val (numbers, symbols) = parseGrid(myGrid)

        var ratios = mutableListOf<Pair<Int,Int>>()

        // find all the "*" symbols adjacent to exactly 2 numbers
        for (s in symbols.filter { it.value == '*' } ) {
            var ratio = mutableListOf<Int>()
            for (n in numbers.filter { it.row >= s.row - 1 && it.row <= s.row + 1 } ) {
                if (s.col >= n.colRange.min() - 1 && s.col <= n.colRange.max()) {
                    ratio.add(n.value)
                }
            }
            if (ratio.size == 2) {
                ratios.add(Pair<Int,Int>(ratio[0], ratio[1]))
            }
        }

        return ratios.fold(0) { sum, i -> sum + i.first * i.second }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    println(part1(testInput))
    check(part1(testInput) == 4361)

    val input = readInput("Day03")
    part1(input).println()

    println(part2(testInput))
    check(part2(testInput) == 467835)
    part2(input).println()
}
