import java.util.PriorityQueue

enum class Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT,
}

fun main() {

    data class Vertex(val row: Int, val col: Int, val dir: Direction, val history: Int)

    // T should be a vertex type
    data class DijkstraResult<T>(val distanceGrid: Map<T, Int>, val predecessors: Map<T, T>)

    fun <T> dijkstra(graph: Map<T, Map<T, Int>>, start: T): DijkstraResult<T> {
        var predecessors = mutableMapOf<T, T>()

        // set all nodes to a distance of max int
        var distanceGrid = graph.keys.associate { it to Int.MAX_VALUE }.toMutableMap()

        distanceGrid[start] = 0

        data class Work(val v: T, val d: Int) : Comparable<Work> {
            override fun compareTo(other: Work): Int {
                return d - other.d
            }
        }

        var toVisit = PriorityQueue<Work>()
        var seen = mutableSetOf<T>()
        toVisit.add(Work(start, 0))

        // Add all the nodes to the toVisit priority queue
        // Since all the distances are MAX_INT except the start node at 0
        // we should be starting at the start node
        //toVisit.addAll(distanceGrid.keys)

        while (toVisit.isNotEmpty()) {
            // find the entry with the lowest distance, ties broken arbitrarily
            // set it to current and take it out of the queue
            val (currentNode, _) = toVisit.poll()
            if (distanceGrid[currentNode] == Int.MAX_VALUE) {
                // we should never jump to an unseen node unless we are done with the main graph
                break
            }

            // for each neighbor not yet visited
                // alt = distance to current + edge from current to neighbor
                // if alt < distance[neighbor]
                    // distance[neighbor] = alt
                    // predecessor[neighbor] = current

            for ((n,d) in graph[currentNode]!!) {
                if (!(n in seen)) {
                    val alt = distanceGrid[currentNode]!! + d
                    if (alt < distanceGrid[n]!!) {
                        distanceGrid[n] = alt
                        predecessors[n] = currentNode
                        toVisit.add(Work(n, alt))
                    }
                }
            }
            seen.add(currentNode)
        }

        return DijkstraResult(distanceGrid, predecessors)
    }

    fun <T> shortestPath(source: T, target: T, dr: DijkstraResult<T>): List<T>? {
        var path = mutableListOf<T>()
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
        val lossMap = input.map { it.toList().map { i -> i.digitToInt() } }

        var vertexList = mutableSetOf<Vertex>()

        // setup the graph
        var graph = mutableMapOf<Vertex, Map<Vertex, Int>>()

        // setup vertex list
        (0..lossMap.lastIndex).forEach { r ->
            (0..lossMap[r].lastIndex).forEach { c ->
                (1..3).forEach { v ->
                    vertexList.add(Vertex(r, c, Direction.UP, v))
                    vertexList.add(Vertex(r, c, Direction.RIGHT, v))
                    vertexList.add(Vertex(r, c, Direction.DOWN, v))
                    vertexList.add(Vertex(r, c, Direction.LEFT, v))
                }
            }
        }

        // remove bonus vertices
        vertexList.filter { v ->
            v.row > lossMap.lastIndex || v.col > lossMap[0].lastIndex || v.row < 0 || v.col < 0
        }.forEach { vertexList.remove(it) }

        // now setup the actual edges
        vertexList.forEach { v ->
            val tmpList =
            vertexList.filter { it != v }.filter { v2 ->
                (v.row == v2.row && v.col == v2.col-1 && (v.dir == Direction.UP || v.dir == Direction.DOWN) && v2.dir == Direction.RIGHT && v2.history == 1) || // change from vert to right
                (v.row == v2.row && v.col == v2.col+1 && (v.dir == Direction.UP || v.dir == Direction.DOWN) && v2.dir == Direction.LEFT && v2.history == 1) || // change from vert to left
                (v.row == v2.row+1 && v.col == v2.col && (v.dir == Direction.LEFT || v.dir == Direction.RIGHT) && v2.dir == Direction.UP && v2.history == 1) || // change from horiz to up
                (v.row == v2.row-1 && v.col == v2.col && (v.dir == Direction.LEFT || v.dir == Direction.RIGHT) && v2.dir == Direction.DOWN && v2.history == 1) || // change from horiz to down
                (v.row == v2.row && v.col == v2.col-1 && v.dir == Direction.RIGHT && v2.dir == Direction.RIGHT && v.history == v2.history-1) || // continue right
                (v.row == v2.row && v.col == v2.col+1 && v.dir == Direction.LEFT && v2.dir == Direction.LEFT && v.history == v2.history-1) || // continue left
                (v.row == v2.row-1 && v.col == v2.col && v.dir == Direction.DOWN && v2.dir == Direction.DOWN && v.history == v2.history-1) || // continue down
                (v.row == v2.row+1 && v.col == v2.col && v.dir == Direction.UP && v2.dir == Direction.UP && v.history == v2.history-1) // continue up
            }
            graph[v] = tmpList.associate { it -> Pair(it, lossMap[it.row][it.col]) }
        }

        //graph.println()

        // now go run dijkstra for the possible starts
        val possibleStart = listOf(Vertex(0,0, Direction.RIGHT, 1), Vertex(0,0, Direction.DOWN, 1))

        val results = possibleStart.map { dijkstra<Vertex>(graph, it) }

        val possibleEnd = vertexList.filter { it.row == lossMap.lastIndex && it.col == lossMap[0].lastIndex }

        // return the total path cost
        val possibleDistances = results.map { it.distanceGrid }.map { it -> it.filterKeys { k -> k in possibleEnd } }[0]

        //possibleDistances.println()

        //val possiblePaths = mutableListOf<Pair<List<Vertex>,Int>>()
        //possibleStart.forEach { ps ->
        //    possibleDistances.forEach { (pe, d) ->
        //        results.forEach { r ->
        //            val tmp = shortestPath(ps, pe, r)
        //            if (tmp != null) {
        //                possiblePaths.add(Pair(tmp, d))
        //            }
        //        }
        //    }
        //}
        //possiblePaths.forEach { (p,d) ->
        //    println("Path of ${d}:")
        //    p.forEach { v ->
        //        print("(${v.row},${v.col},${v.dir},${v.history})")
        //    }
        //    println()
        //}

        val myMin = possibleDistances.minOf { (_,v) -> v }

        myMin.println()

        return myMin
    }

    fun part2(input: List<String>): Int {
        val lossMap = input.map { it.toList().map { i -> i.digitToInt() } }

        //lossMap.forEach { it.println() }

        var vertexList = mutableSetOf<Vertex>()

        // setup the graph
        var graph = mutableMapOf<Vertex, Map<Vertex, Int>>()

        // setup vertex list
        (0..lossMap.lastIndex).forEach { r ->
            (0..lossMap[r].lastIndex).forEach { c ->
                (1..10).forEach { v ->
                    vertexList.add(Vertex(r, c, Direction.UP, v))
                    vertexList.add(Vertex(r, c, Direction.RIGHT, v))
                    vertexList.add(Vertex(r, c, Direction.DOWN, v))
                    vertexList.add(Vertex(r, c, Direction.LEFT, v))
                }
            }
        }

        // Remove anything under 4 steps of history for the last vertex bc it needs 4 steps to stop
        vertexList.filter { v ->
            v.row == lossMap.lastIndex && v.col == lossMap[0].lastIndex && v.history < 4
        }.forEach { vertexList.remove(it) }

        // remove bonus vertices
        vertexList.filter { v ->
            v.row > lossMap.lastIndex || v.col > lossMap[0].lastIndex || v.row < 0 || v.col < 0
        }.forEach { vertexList.remove(it) }

        // now setup the actual edges
        vertexList.forEach { v ->
            val tmpList =
            vertexList.filter { it != v }.filter { v2 ->
                (v.row == v2.row && v.col == v2.col-1 && (v.dir == Direction.UP || v.dir == Direction.DOWN) && v2.dir == Direction.RIGHT && v.history > 3 && v2.history == 1) || // change from vert to right after at least 4
                (v.row == v2.row && v.col == v2.col+1 && (v.dir == Direction.UP || v.dir == Direction.DOWN) && v2.dir == Direction.LEFT && v.history > 3 && v2.history == 1) || // change from vert to right after at least 4
                (v.row == v2.row+1 && v.col == v2.col && (v.dir == Direction.LEFT || v.dir == Direction.RIGHT) && v2.dir == Direction.UP && v.history > 3 && v2.history == 1) || // change from horiz to up after at least 4
                (v.row == v2.row-1 && v.col == v2.col && (v.dir == Direction.LEFT || v.dir == Direction.RIGHT) && v2.dir == Direction.DOWN && v.history > 3 && v2.history == 1) || // change from horiz to down after at least 4
                (v.row == v2.row && v.col == v2.col-1 && v.dir == Direction.RIGHT && v2.dir == Direction.RIGHT && v.history == v2.history-1) || // continue right
                (v.row == v2.row && v.col == v2.col+1 && v.dir == Direction.LEFT && v2.dir == Direction.LEFT && v.history == v2.history-1) || // continue left
                (v.row == v2.row-1 && v.col == v2.col && v.dir == Direction.DOWN && v2.dir == Direction.DOWN && v.history == v2.history-1) || // continue down
                (v.row == v2.row+1 && v.col == v2.col && v.dir == Direction.UP && v2.dir == Direction.UP && v.history == v2.history-1) // continue up
            }
            graph[v] = tmpList.associate { it -> Pair(it, lossMap[it.row][it.col]) }
        }

        //graph.println()

        // now go run dijkstra for the possible starts
        val possibleStart = listOf(Vertex(0,0, Direction.RIGHT, 1), Vertex(0,0, Direction.DOWN, 1))

        val results = possibleStart.map { dijkstra<Vertex>(graph, it) }

        val possibleEnd = vertexList.filter { it.row == lossMap.lastIndex && it.col == lossMap[0].lastIndex }

        // return the total path cost
        val possibleDistances = results.map { it.distanceGrid }.map { it -> it.filterKeys { k -> k in possibleEnd } }[0]

        //possibleDistances.println()

        //val possiblePaths = mutableListOf<Pair<List<Vertex>,Int>>()
        //possibleStart.forEach { ps ->
        //    possibleDistances.forEach { (pe, d) ->
        //        results.forEach { r ->
        //            val tmp = shortestPath(ps, pe, r)
        //            if (tmp != null) {
        //                possiblePaths.add(Pair(tmp, d))
        //            }
        //        }
        //    }
        //}
        //possiblePaths.forEach { (p,d) ->
        //    println("Path of ${d}:")
        //    p.forEach { v ->
        //        print("(${v.row},${v.col},${v.dir},${v.history})=${lossMap[v.row][v.col]}")
        //    }
        //    println()
        //}

        val myMin = possibleDistances.minOf { (_,v) -> v }

        myMin.println()

        return myMin
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    println("Part 1 test: ${part1(testInput)}")
    check(part1(testInput) == 102)
    println("Part 2 test: ${part2(testInput)}")
    check(part2(testInput) == 94)

    val testInput2 = readInput("Day17_test2")
    println("Part 2 test 2: ${part2(testInput2)}")
    check(part2(testInput2) == 71)

    val input = readInput("Day17")
    val part1Result = part1(input)
    part1Result.println()

    part2(input).println()
}
