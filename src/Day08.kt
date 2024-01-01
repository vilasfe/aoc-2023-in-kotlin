typealias Network = Map<String, Pair<String,String>>

fun main() {

    fun createNetwork(input: List<String>): Network {
        return input.associate {
            val splitStr = it.split(" = ")
            val splitPair = splitStr[1].split(", ")
            Pair(splitStr[0], Pair(splitPair[0].substring(1, splitPair[0].lastIndex+1), splitPair[1].substring(0, splitPair[1].lastIndex)))
        }
    }

    fun part1(input: List<String>): Int {
        val moveOrder = input[0]

        val network = createNetwork(input.subList(2, input.lastIndex+1))

        var moveCount = 0
        var index = "AAA"
        while (index != "ZZZ") {
            val idx = moveCount % moveOrder.length
            val currPair = network[index]!!
            index = if (moveOrder[idx] == 'R') currPair.second else currPair.first
            moveCount++
        }

        return moveCount
    }

    fun part2(input: List<String>): Long {
        val moveOrder = input[0]

        val network = createNetwork(input.subList(2, input.lastIndex+1))

        var indices = network.keys.filter { it.endsWith("A") }

        var moveCounts = mutableMapOf<String, Long>()

        indices.println()

        for (i in indices) {
            var index = i
            var mc = 0L
            while (!index.endsWith("Z")) {
                val idx = mc % moveOrder.length
                val currPair = network[index]!!
                index = if (moveOrder[idx.toInt()] == 'R') currPair.second else currPair.first
                mc++
            }
            moveCounts[i] = mc
        }

        moveCounts.println()

        moveCounts.values.println()

        moveCounts.values.fold(1L) { total, it -> total * it }.println()

        return findLCMOfListOfNumbers(moveCounts.values.toList())
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day08_test1")
    val testInput2 = readInput("Day08_test2")
    part1(testInput1).println()
    check(part1(testInput1) == 2)
    part1(testInput2).println()
    check(part1(testInput2) == 6)

    val input = readInput("Day08")
    part1(input).println()

    val testInput3 = readInput("Day08_test3")
    part2(testInput3).println()
    check(part2(testInput3) == 6L)
    part2(input).println()
}
