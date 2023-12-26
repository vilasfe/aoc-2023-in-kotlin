fun main() {
    data class Snake(var head: Pair<Int,Int>, var move: Pair<Int,Int>)

    fun part1(input: List<String>): Int {
        val mirrorMap = input.map { it.toList() }

        var energyMap = mutableListOf<MutableList<Int>>()
        mirrorMap.forEach { row ->
            energyMap.add(MutableList<Int>(row.size) {0} )
        }

        var snakes = mutableListOf<Snake>()
        snakes.add(Snake(Pair(0,0), Pair(0, 1)))

        var totalMoves = 0
        var totalEnergized = Pair(-1, -1)
        while (snakes.isNotEmpty()) {
            //println("Snakes: ${snakes}")
            // trim snakes that crossed a boundary on last time step
            snakes.filter {
                it.head.first < 0 ||
                it.head.first == mirrorMap.size ||
                it.head.second < 0 ||
                it.head.second == mirrorMap[0].size
            }.forEach { snakes.remove(it) }

            // remove duplicate snakes
            snakes = snakes.distinct().toMutableList()

            // energize the current snake locations
            snakes.forEach {
                energyMap[it.head.first][it.head.second]++
            }

            //println("Trimmed Snakes: ${snakes}")

            var newSnakes = mutableListOf<Snake>()
            newSnakes.clear()

            // Update snake movement
            snakes.forEach {
                // check to see if they encountered any reflectors and update the movement
                if ('/' == mirrorMap[it.head.first][it.head.second]) {
                    if (it.move.first != 0) {
                        it.move = Pair(0, -it.move.first)
                    } else if (it.move.second != 0) {
                        it.move = Pair(-it.move.second, 0)
                    }
                } else if ('\\' == mirrorMap[it.head.first][it.head.second]) {
                    if (it.move.first != 0) {
                        it.move = Pair(0, it.move.first)
                    } else if (it.move.second != 0) {
                        it.move = Pair(it.move.second, 0)
                    }
                }

                // check for splitters and update the movement plus add a new snake
                else if ('-' == mirrorMap[it.head.first][it.head.second] && it.move.first != 0) {
                    it.move = Pair(0, it.move.first)
                    newSnakes.add(Snake(it.head, Pair(0, -it.move.second)))
                } else if ('|' == mirrorMap[it.head.first][it.head.second] && it.move.second != 0) {
                    it.move = Pair(it.move.second, 0)
                    newSnakes.add(Snake(it.head, Pair(-it.move.first, 0)))
                }
            }
            snakes.addAll(newSnakes)

            // move the snakes
            snakes.forEach {
                it.head = Pair(it.head.first + it.move.first, it.head.second + it.move.second)
            }

            totalMoves++
            if (totalMoves > mirrorMap.size * mirrorMap[0].size) {
                break
            }

            // println("${energyMap.map { it.count { it > 0 } }.sum()} == ${energyMap.map { it.count { it > 1 } }.sum()} with ${snakes.size} snakes")

            if (energyMap.map { it.count { it > 0 } }.sum() ==
                energyMap.map { it.count { it > 1 } }.sum()) {
                  // not learning anything new, so break
                  break
            }

            //  Check for 3 of the same values in a row
            if (totalEnergized.first == totalEnergized.second &&
                totalEnergized.first == energyMap.map { it.count { it > 0 } }.sum()) {
              break
            }

            totalEnergized = Pair(totalEnergized.second, energyMap.map { it.count { it > 0 } }.sum())
        }

        //energyMap.forEach { it.println() }

        //val printMe = energyMap.map { it -> it.map { i -> if (i > 0) '#' else '.' } }
        //printMe.forEach { it.println() }

        return energyMap.map { it -> it.count { it > 0 } }.sum()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    println("Part 1 test: ${part1(testInput)}")
    check(part1(testInput) == 46)

    val input = readInput("Day16")
    part1(input).println()
    part2(input).println()
}
