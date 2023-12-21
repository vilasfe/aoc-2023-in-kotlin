
typealias DistanceGrid = MutableList<MutableList<Int>>

fun main() {

    data class DijkstraResult(val distanceGrid: DistanceGrid, val predecessors: Map<Pair<Int,Int>,Pair<Int,Int>>)

    fun dijkstra(myGrid: List<List<Char>>, start: Pair<Int,Int>): DijkstraResult {

        var distanceGrid = MutableList<MutableList<Int>>(myGrid.size) { MutableList<Int>(myGrid[0].size) { Int.MAX_VALUE } }

        var predecessors = mutableMapOf<Pair<Int,Int>, Pair<Int,Int>>()

        distanceGrid[start.first][start.second] = 0

        var toVisit = ArrayDeque<Pair<Int,Int>>()

        (0..myGrid.size-1).forEach { row -> (0..myGrid[0].size-1).forEach { col -> toVisit.add(Pair(row, col)) } }

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
                    // predecessor[neighbor] = current

            var neighbors = mutableListOf<Pair<Int,Int>>()
            when (myGrid[currentNode.first][currentNode.second]) {
                '|' -> neighbors.addAll(listOf(Pair(currentNode.first-1,currentNode.second), Pair(currentNode.first+1,currentNode.second)))
                '-' -> neighbors.addAll(listOf(Pair(currentNode.first,currentNode.second-1), Pair(currentNode.first,currentNode.second+1)))
                'L' -> neighbors.addAll(listOf(Pair(currentNode.first-1,currentNode.second), Pair(currentNode.first,currentNode.second+1)))
                'J' -> neighbors.addAll(listOf(Pair(currentNode.first-1,currentNode.second), Pair(currentNode.first,currentNode.second-1)))
                '7' -> neighbors.addAll(listOf(Pair(currentNode.first+1,currentNode.second), Pair(currentNode.first,currentNode.second-1)))
                'F' -> neighbors.addAll(listOf(Pair(currentNode.first+1,currentNode.second), Pair(currentNode.first,currentNode.second+1)))
                'S' -> {
                    if (currentNode.first > 0 && "|7F".contains(myGrid[currentNode.first-1][currentNode.second])) {
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
                        predecessors[n] = currentNode
                    }
                }
            }
        }

        return DijkstraResult(distanceGrid, predecessors)
    }

    fun shortestPath(source: Pair<Int,Int>, target: Pair<Int,Int>, dr: DijkstraResult): List<Pair<Int,Int>>? {
        var path = mutableListOf<Pair<Int,Int>>()
        var current = target
        while (current != source) {
            path.add(current)
            if (dr.predecessors.get(current) == null) {
                return null
            }
            current = dr.predecessors[current]!!
        }
        // remember the source
        path.add(source)

        return path.reversed()
    }

    fun part1(input: List<String>): Int {
        val myGrid = input.map { it.toList() }
        // find the starting point
        val startRow = myGrid.indexOfFirst { it.contains('S') }
        val startCol = myGrid[startRow].indexOf('S')

        val start = Pair(startRow, startCol)

        val (distanceGrid, _) = dijkstra(myGrid, start)

        return distanceGrid.map { it.filter { i -> i < Int.MAX_VALUE }.maxOrNull() }.filterNotNull().max()
    }

    fun part2(input: List<String>): Int {
        val myGrid = input.map { it.toList() }
        // find the starting point
        val startRow = myGrid.indexOfFirst { it.contains('S') }
        val startCol = myGrid[startRow].indexOf('S')

        val start = Pair(startRow, startCol)

        val dr = dijkstra(myGrid, start)

        val maxVal = dr.distanceGrid.map { it.filter { i -> i < Int.MAX_VALUE }.maxOrNull() }.filterNotNull().max()

        val row = dr.distanceGrid.indexOfFirst { it.contains(maxVal) }
        val col = dr.distanceGrid[row].indexOf(maxVal)

        var neighbors = mutableListOf<Pair<Int,Int>>()
        if ("|7F".contains(myGrid[row-1][col])) {
            neighbors.add(Pair(row-1,col))
        }
        if ("|LJ".contains(myGrid[row+1][col])) {
            neighbors.add(Pair(row+1,col))
        }
        if (col > 0 && "-LF".contains(myGrid[row][col-1])) {
            neighbors.add(Pair(row,col-1))
        }
        if ("-J7".contains(myGrid[row][col+1])) {
            neighbors.add(Pair(row,col+1))
        }

        // Now get the list of exterior points of the loop so we can do shoelace
        val pathList = neighbors.mapNotNull { shortestPath(start, it, dr) }

        if (pathList.size != 2) {
            println("pathList incorrect for loop")
            pathList.println()
        }

        start.println()
        var points = mutableListOf<Pair<Int,Int>>()
        points.addAll(pathList[0])
        points.add(Pair(row,col))
        points.addAll(pathList[1].reversed())

        val twoArea = Math.abs(points.zipWithNext() { p1, p2 -> (p1.first+1) * (p2.second+1) - (p1.second+1) * (p2.first+1) }.sum())

        println("2A = ${twoArea}; A = ${twoArea/2}")

        println("b = ${points.size-1}")

        val holes = 0

        val interior = twoArea / 2 - (points.size-1) / 2 - holes + 1

        return interior
    }

    // test if implementation meets criteria from the description, like:
    val test1Input = readInput("Day10_test1")
    val test2Input = readInput("Day10_test2")
    check(part1(test1Input) == 4)
    check(part1(test2Input) == 8)

    val input = readInput("Day10")
    part1(input).println()

    val test3Input = readInput("Day10_test3")
    check(part2(test3Input) == 4)
    val test4Input = readInput("Day10_test4")
    check(part2(test4Input) == 8)
    val test5Input = readInput("Day10_test5")
    check(part2(test5Input) == 10)

    part2(input).println()
}
