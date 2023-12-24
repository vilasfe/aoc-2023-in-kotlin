typealias Terrain = List<List<Char>>

fun main() {
    fun makeTerrain(input: List<String>): List<Terrain> {
        var retval = mutableListOf<Terrain>()

        var currentTerrain = mutableListOf<List<Char>>()

        for (i in input) {
            if (i.trim().isEmpty()) {
                retval.add(currentTerrain)
                currentTerrain = mutableListOf<List<Char>>()
            } else {
                currentTerrain.add(i.toList())
            }
        }

        // deal with the last one
        retval.add(currentTerrain)

        return retval
    }

    fun transpose(t: Terrain): Terrain {
        var retval = mutableListOf<List<Char>>()
        var currentRow = mutableListOf<Char>()

        for (c in 0..t[0].size-1) {
            for (r in 0..t.size-1) {
                currentRow.add(t[r][c])
            }
            retval.add(currentRow)
            currentRow = mutableListOf<Char>()
        }
        if (! currentRow.isEmpty()) {
            retval.add(currentRow)
        }
        return retval
    }

    fun findHorizMirror(t: Terrain): Int {
        for (r in 1..t.size-1) {
            if (t[r] == t[r-1]) {
                // found a potential mirror, so proceed until the limit of either top or bottom
                val toEval = minOf(r, t.size - r)
                var found = true
                for (dist in 1..toEval-1) {
                    if (t[r-dist-1] != t[r+dist]) {
                        found = false
                        break
                    }
                }
                if (found) {
                    return r
                }
            }
        }

        // no horizontal mirror found
        return 0
    }

    fun findVertMirror(t: Terrain): Int {
        return findHorizMirror(transpose(t))
    }

    fun part1(input: List<String>): Int {
        val terrainList = makeTerrain(input)

        return terrainList.map { findVertMirror(it) + 100 * findHorizMirror(it) }.sum()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    println("Part 1 test result: ${part1(testInput)}")
    check(part1(testInput) == 405)

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}
