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

    fun listDiff(l1: List<Char>, l2: List<Char>): Int {
        var diff = 0
        for (z in l1.zip(l2)) {
            if (z.first != z.second) {
                diff++
            }
        }
        //println("Comparing $l1 vs $l2 = $diff")
        return diff
    }

    fun findMirror(t: Terrain, smudges: Int): Int? {
        //t.forEach { it.println() }
        //println("---")
        for (r in 0..t.size-2) {
            var s = smudges
            val toEval = (r downTo 0).zip(r+1..t.lastIndex)
            //toEval.println()
            var found = true
            for (e in toEval) {
                val d = listDiff(t[e.first], t[e.second])
                if (d <= s) {
                    //println("reducing ${s} by ${d}")
                    s -= d
                } else {
                    found = false
                    break
                }
            }
            if (found && s == 0) {
                //println("found mirror at ${r}")
                return r+1
            }
        }

        // no mirror found
        return null
    }

    fun findHorizMirror(t: Terrain, smudges: Int): Int? {
        val ret = findMirror(t, smudges)

        if (ret != null) {
            return 100 * ret
        }
        return null
    }

    fun findVertMirror(t: Terrain, smudges: Int): Int? {
        return findMirror(transpose(t), smudges)
    }

    fun part1(input: List<String>): Int {
        val terrainList = makeTerrain(input)

        return terrainList.map { findHorizMirror(it, 0) ?: findVertMirror(it, 0) ?: throw IllegalStateException("No Mirror Found") }.sum()
    }

    fun part2(input: List<String>): Int {
        val terrainList = makeTerrain(input)

        return terrainList.map { findHorizMirror(it, 1) ?: findVertMirror(it, 1) ?: throw IllegalStateException("No Mirror Found for ${it}") }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    println("Part 1 test result: ${part1(testInput)}")
    check(part1(testInput) == 405)
    println("Part 2 test result: ${part2(testInput)}")
    check(part2(testInput) == 400)

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}
