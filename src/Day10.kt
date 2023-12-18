fun main() {
    fun part1(input: List<String>): Int {
        val myGrid = input.map { it.toList() }
        // find the starting point
        val startRow = myGrid.indexOfFirst { it.contains('S') }
        val startCol = myGrid[startRow].indexOf('S')

        val start = Pair(startRow, startCol)

        // now start Dijkstra
        var distanceGrid = MutableList<MutableList<Int>>(input.size) { MutableList<Int>(input[0].length) { Int.MAX_VALUE } }

        distanceGrid[start.first][start.second] = 0

        var toVisit = ArrayDeque<Pair<Int,Int>>()

        (0..input.size-1).forEach { row -> (0..input[0].length-1).forEach { col -> toVisit.add(Pair(row, col)) } }

        while (toVisit.isNotEmpty()) {
            // find the entry with the lowest distance, ties broken arbitrarily
            // set it to current and take it out of the queue
            val currentNode = toVisit.minByOrNull { distanceGrid[it.first][it.second] }!!
            if (distanceGrid[currentNode.first][currentNode.second] == Int.MAX_VALUE) {
                // we should never jump to an unseen node unless we are done with the main graph
                break
            }
            toVisit.remove(currentNode)

            // for each neighbor not yet visited
                // alt = distance to current + edge from current to neighbor
                // if alt < distance[neighbor]
                    // distance[neighbor] = alt

            var neighbors = mutableListOf<Pair<Int,Int>>()
            when (myGrid[currentNode.first][currentNode.second]) {
                '|' -> neighbors.addAll(listOf(Pair(currentNode.first-1,currentNode.second), Pair(currentNode.first+1,currentNode.second)))
                '-' -> neighbors.addAll(listOf(Pair(currentNode.first,currentNode.second-1), Pair(currentNode.first,currentNode.second+1)))
                'L' -> neighbors.addAll(listOf(Pair(currentNode.first-1,currentNode.second), Pair(currentNode.first,currentNode.second+1)))
                'J' -> neighbors.addAll(listOf(Pair(currentNode.first-1,currentNode.second), Pair(currentNode.first,currentNode.second-1)))
                '7' -> neighbors.addAll(listOf(Pair(currentNode.first+1,currentNode.second), Pair(currentNode.first,currentNode.second-1)))
                'F' -> neighbors.addAll(listOf(Pair(currentNode.first+1,currentNode.second), Pair(currentNode.first,currentNode.second+1)))
                'S' -> {
                    if ("|7F".contains(myGrid[currentNode.first-1][currentNode.second])) {
                        neighbors.add(Pair(currentNode.first-1,currentNode.second))
                    }
                    if ("|LJ".contains(myGrid[currentNode.first+1][currentNode.second])) {
                        neighbors.add(Pair(currentNode.first+1,currentNode.second))
                    }
                    if (currentNode.second > 0 && "-LF".contains(myGrid[currentNode.first][currentNode.second-1])) {
                        neighbors.add(Pair(currentNode.first,currentNode.second-1))
                    }
                    if ("-J7".contains(myGrid[currentNode.first][currentNode.second+1])) {
                        neighbors.add(Pair(currentNode.first,currentNode.second+1))
                    }
                }
            }

            for (n in neighbors) {
                if (n in toVisit) {
                    val alt = distanceGrid[currentNode.first][currentNode.second] + 1
                    if (alt < distanceGrid[n.first][n.second]) {
                        distanceGrid[n.first][n.second] = alt
                    }
                }
            }
        }

        return distanceGrid.map { it.filter { i -> i < Int.MAX_VALUE }.maxOrNull() }.filterNotNull().max()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val test1Input = readInput("Day10_test1")
    val test2Input = readInput("Day10_test2")
    check(part1(test1Input) == 4)
    check(part1(test2Input) == 8)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
