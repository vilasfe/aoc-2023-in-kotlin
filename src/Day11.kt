fun main() {

    fun makeYoungUniverse(input: List<String>): List<Pair<Int,Int>> {
        // Get the coordinates of all galaxies indicated by '#'
        val row_index = input.indices

        var galaxies = mutableListOf<Pair<Int,Int>>()
        for (r in row_index) {
            val locations = input[r].toList().indexesOf { it == '#' }
            for (c in locations) {
                galaxies.add(Pair(r, c))
            }
        }

        return galaxies
    }

    fun expandUniverse(galaxies: List<Pair<Int,Int>>, age: Int): List<Pair<Int,Int>> {

        var toUpdate = galaxies.map { it -> MutablePair(it.first, it.second) }

        // get the pre-expansion max row and column
        val max_row = toUpdate.maxOf { it.first }
        val max_col = toUpdate.maxOf { it.second }

        // expand the universe by doubling blank columns
        for (c in max_col downTo 0) {
            if (toUpdate.none { it.second == c } ) {
                for (g in toUpdate.filter { it.second > c }) {
                    g.second += age
                }
            }
        }

        // expand the universe by doubling blank rows
        for (r in max_row downTo 0) {
            if (toUpdate.none { it.first == r }) {
                for (g in toUpdate.filter { it.first > r }) {
                    g.first += age
                }
            }
        }

        val universe = toUpdate.map { it -> Pair(it.first, it.second) }

        return universe
    }

    fun part1(input: List<String>): Int {
        val universe = makeYoungUniverse(input)

        val galaxies = expandUniverse(universe, 1)

        //galaxies.println()

        val combinations = galaxies.subList(0, galaxies.lastIndex).mapIndexed { idx, from -> galaxies.subList(idx+1, galaxies.lastIndex+1).map { to -> Pair(from, to) } }.flatten()

        //combinations.println()

        // sum of manhattan distances between pairs of galaxies
        val totalDist = combinations.map { g -> Math.abs(g.second.first - g.first.first) + Math.abs(g.second.second - g.first.second) }.sum()

        return totalDist
    }

    fun part2(input: List<String>, age: Int): Long {
        val universe = makeYoungUniverse(input)

        val galaxies = expandUniverse(universe, age)

        //galaxies.println()

        val combinations = galaxies.subList(0, galaxies.lastIndex).mapIndexed { idx, from -> galaxies.subList(idx+1, galaxies.lastIndex+1).map { to -> Pair(from, to) } }.flatten()

        //combinations.println()

        // sum of manhattan distances between pairs of galaxies
        val totalDist = combinations.map { g -> Math.abs(g.second.first - g.first.first).toLong() + Math.abs(g.second.second - g.first.second).toLong() }.sum()

        return totalDist

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 374)
    check(part2(testInput, 9) == 1030L)
    check(part2(testInput, 99) == 8410L)

    val input = readInput("Day11")
    part1(input).println()
    part2(input, 999999).println()
}
