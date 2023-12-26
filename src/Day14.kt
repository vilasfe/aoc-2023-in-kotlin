fun main() {

    fun tiltNorth(t_in: Terrain): Terrain {
        var t_out = mutableListOf<MutableList<Char>>()

        t_in.forEach { it -> t_out.add(it.toMutableList()) }

        for (t in 1..t_out.lastIndex) {
            for (c in 0..t_out[0].lastIndex) {
                var fall = t
                if (t_out[fall][c] == 'O') {
                    while (fall > 0 && t_out[fall-1][c] == '.') {
                        t_out[fall-1][c] = t_out[fall][c].also { t_out[fall][c] = t_out[fall-1][c] }
                        fall--
                    }
                }
            }
        }

        //println("Tilted North")
        //t_out.forEach { it.println() }

        return t_out
    }

    fun tiltSouth(t_in: Terrain): Terrain {
        var t_out = mutableListOf<MutableList<Char>>()

        t_in.forEach { it -> t_out.add(it.toMutableList()) }

        for (t in t_out.lastIndex-1 downTo 0) {
            for (c in 0..t_out[0].lastIndex) {
                var fall = t
                if (t_out[fall][c] == 'O') {
                    while (fall < t_out.lastIndex && t_out[fall+1][c] == '.') {
                        t_out[fall+1][c] = t_out[fall][c].also { t_out[fall][c] = t_out[fall+1][c] }
                        fall++
                    }
                }
            }
        }

        //println("Tilted South")
        //t_out.forEach { it.println() }

        return t_out
    }

    fun tiltWest(t_in: Terrain): Terrain {
        var t_out = mutableListOf<MutableList<Char>>()

        t_in.forEach { it -> t_out.add(it.toMutableList()) }

        for (t in 0..t_out.lastIndex) {
            for (c in 1..t_out.lastIndex) {
                var fall = c
                if (t_out[t][c] == 'O') {
                    while (fall > 0 && t_out[t][fall-1] == '.') {
                        t_out[t][fall-1] = t_out[t][fall].also { t_out[t][fall] = t_out[t][fall-1] }
                        fall--
                    }
                }
            }
        }

        //println("Tilted West")
        //t_out.forEach { it.println() }

        return t_out
    }

    fun tiltEast(t_in: Terrain): Terrain {
        var t_out = mutableListOf<MutableList<Char>>()

        t_in.forEach { it -> t_out.add(it.toMutableList()) }

        for (t in 0..t_out.lastIndex) {
            for (c in t_out.lastIndex-1 downTo 0) {
                var fall = c
                if (t_out[t][c] == 'O') {
                    while (fall < t_out[t].lastIndex && t_out[t][fall+1] == '.') {
                        t_out[t][fall+1] = t_out[t][fall].also { t_out[t][fall] = t_out[t][fall+1] }
                        fall++
                    }
                }
            }
        }

        //println("Tilted East")
        //t_out.forEach { it.println() }

        return t_out
    }

    fun calcNorth(t_in: Terrain): Int {
        var total = 0
        for (row in 0..t_in.lastIndex) {
            total += t_in[row].count { it == 'O' } * (t_in.size - row)
        }
        return total
    }

    fun part1(input: List<String>): Int {
        val t = input.map { it.toList() }

        val t_north = tiltNorth(t)

        return calcNorth(t_north)
    }

    fun part2(input: List<String>): Int {
        var t = input.map { it.toList() }

        var cycleList = mutableListOf<Terrain>()

        t = tiltEast(tiltSouth(tiltWest(tiltNorth(t))))
        //println("After 1 iterations")
        //t.forEach { it.println() }

        cycleList.add(t)
        //println("current = ${calcNorth(t)}")

        val MAX_ITER = 1000000000

        for (i in 1..MAX_ITER) {
            t = tiltEast(tiltSouth(tiltWest(tiltNorth(t))))
            //println("After ${i+1} iterations")
            //t.forEach { it.println() }
            //println("current = ${calcNorth(t)}")

            if (cycleList.contains(t)) {
                println("Found a cycle at ${i}")
                val cycleLen = i - cycleList.indexOf(t)

                val cyclesRemaining = (MAX_ITER - i) % cycleLen - 1

                repeat(cyclesRemaining) {
                    t = tiltEast(tiltSouth(tiltWest(tiltNorth(t))))
                }
                return calcNorth(t)
            }
            cycleList.add(t)
        }

        return MAX_ITER
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    println("Part 1: ${part1(testInput)}")
    check(part1(testInput) == 136)
    println("Part 2: ${part2(testInput)}")
    check(part2(testInput) == 64)

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}
