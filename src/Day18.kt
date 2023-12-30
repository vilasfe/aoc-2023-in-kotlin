import java.lang.Long.parseLong

fun main() {
    data class Edge(val p1: Pair<Long,Long>, val p2: Pair<Long,Long>)

    fun createEdgeList(input: List<String>): List<Edge> {
        var retval = mutableListOf<Edge>()

        var oldPos = Pair<Long,Long> (0,0)
        var newPos = oldPos

        input.forEach { it ->
            val split_i = it.split("\\s+".toRegex())
            val howFar = split_i[1].toInt()
            newPos = when (split_i[0]) {
                "U" -> Pair(newPos.first, newPos.second - howFar)
                "D" -> Pair(newPos.first, newPos.second + howFar)
                "L" -> Pair(newPos.first - howFar, newPos.second)
                "R" -> Pair(newPos.first + howFar, newPos.second)
                else -> throw IllegalArgumentException("Illegal direction")
            }
            retval.add(Edge(oldPos, newPos))
            oldPos = newPos
        }

        return retval
    }

    fun createPart2EdgeList(input: List<String>): List<Edge> {
        var retval = mutableListOf<Edge>()

        var oldPos = Pair<Long,Long> (0,0)
        var newPos = oldPos

        input.forEach { it ->
            val split_i = it.split("\\s+".toRegex())
            val dir = split_i[2][split_i[2].lastIndex-1].digitToInt() // skip "(#" and read the first number
            val howFar = parseLong(split_i[2].substring(2, split_i[2].lastIndex-1), 16) // get the remaining int
            newPos = when (dir) {
                3 -> Pair(newPos.first, newPos.second - howFar)
                1 -> Pair(newPos.first, newPos.second + howFar)
                2 -> Pair(newPos.first - howFar, newPos.second)
                0 -> Pair(newPos.first + howFar, newPos.second)
                else -> throw IllegalArgumentException("Illegal direction ${dir} from ${it}")
            }
            retval.add(Edge(oldPos, newPos))
            oldPos = newPos
        }

        return retval
    }

    fun calcVolume(edgeList: List<Edge>): Long {
        // go run the shoelace theorem and return the enclosed area
        val twoArea = Math.abs(edgeList.fold(0L) { tot, e ->
            tot + ((e.p1.first+1) * (e.p2.second+1) - (e.p1.second+1) * (e.p2.first+1))
        })
        // commented out bc the input makes a loop already
        //+ Math.abs( (edgeList.last().p2.first+1) * (edgeList.first().p1.second+1) - (edgeList.last().p2.second+1) * (edgeList.first().p1.first+1) )

        // Calculate the boundary size
        val boundary = edgeList.fold(0L) { tot, e ->
            tot + Math.abs(e.p2.second - e.p1.second + e.p2.first - e.p1.first)
        }


        // Now use the area given from the shoelace theorem in Pick's theorem
        // A = i + b/2 -1
        println("2A = ${twoArea}; A = ${twoArea/2}")

        println("b = ${boundary}")

        val holes = 0

        // calculate interior size (same as Day 10)
        val interior = twoArea / 2 - boundary / 2 - holes + 1

        // Add the 1 cubic meter boundary
        val total = interior + boundary

        return total
    }

    fun part1(input: List<String>): Long {
        val edgeList = createEdgeList(input)

        return calcVolume(edgeList)
    }

    fun part2(input: List<String>): Long {
        val edgeList = createPart2EdgeList(input)

        return calcVolume(edgeList)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    part1(testInput).println()
    check(part1(testInput) == 62L)
    part2(testInput).println()
    check(part2(testInput) == 952408144115L)

    val input = readInput("Day18")
    part1(input).println()
    part2(input).println()
}
