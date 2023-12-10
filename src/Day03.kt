fun main() {

    data class CandidateNumber(val value: Int, val colRange: IntRange, val row: Int)
    data class Symbol(val value: Char, val col: Int, val row: Int)

    fun part1(input: List<String>): Int {
        val myGrid = input.map { it.toList() }
        val x_index = myGrid.indices
        val y_index = myGrid[0].indices
        var numbers = mutableListOf<CandidateNumber>()
        var symbols = mutableListOf<Symbol>()

        // find all the symbols and their locations
        for (r in x_index) {
            val locations = myGrid[r].toList().indexesOf { !it.isDigit() && it != '.' }
            for (l in locations) {
                symbols.add(Symbol(myGrid[r][l], l, r))
            }
        }

        // find all the numbers and their locations
        for (r in x_index) {
            var c_start = -1
            var c_end = x_index.max()
            for (c in y_index) {
                if (c_start == -1 && myGrid[r][c].isDigit()) {
                    c_start = c
                }
                if (c_start > -1 && !myGrid[r][c].isDigit()) {
                    c_end = c
                    val num = myGrid[r].subList(c_start, c_end).joinToString(separator="").toInt()
                    numbers.add(CandidateNumber(num, c_start..c_end, r))
                    c_end = x_index.max()
                    c_start = -1
                }
            }
            // Check for number that ends the line
            if (c_start > -1) {
                val num = myGrid[r].subList(c_start, c_end+1).joinToString(separator="").toInt()
                numbers.add(CandidateNumber(num, c_start..c_end, r))
            }
        }

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
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    println(part1(testInput))
    check(part1(testInput) == 4361)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
