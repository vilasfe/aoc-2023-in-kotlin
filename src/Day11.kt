fun main() {

    fun part1(input: List<String>): Int {
        // Get the coordinates of all galaxies indicated by '#'
        val row_index = input.indices
        val col_index = input[0].indices

        var galaxies = mutableListOf<MutablePair<Int,Int>>()
        for (r in row_index) {
            val locations = input[r].toList().indexesOf { it == '#' }
            for (c in locations) {
                galaxies.add(MutablePair(r, c))
            }
        }

        // get the pre-expansion max row and column
        val max_row = galaxies.maxOf { it.first }
        val max_col = galaxies.maxOf { it.second }

        // expand the universe by doubling blank columns
        for (c in max_col downTo 0) {
            if (galaxies.none { it.second == c } ) {
                for (g in galaxies.filter { it.second > c }) {
                    g.second++
                }
            }
        }

        // expand the universe by doubling blank rows
        for (r in max_row downTo 0) {
            if (galaxies.none { it.first == r }) {
                for (g in galaxies.filter { it.first > r }) {
                    g.first++
                }
            }
        }

        //galaxies.println()

        val combinations = galaxies.subList(0, galaxies.lastIndex).mapIndexed { idx, from -> galaxies.subList(idx+1, galaxies.lastIndex+1).map { to -> Pair(from, to) } }.flatten()

        //combinations.println()

        // sum of manhattan distances between pairs of galaxies
        val totalDist = combinations.map { g -> Math.abs(g.second.first - g.first.first) + Math.abs(g.second.second - g.first.second) }.sum()

        return totalDist
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 374)

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}
