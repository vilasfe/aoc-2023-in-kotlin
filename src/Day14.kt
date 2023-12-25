typealias Terrain = List<List<Char>>

fun main() {

    fun tiltNorth(t_in: Terrain): Terrain {
        var t_out = mutableListOf<MutableList<Char>>()

        for (line in t_in) {
            t_out.add(line.toMutableList())
        }

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

        return t_out
    }

    fun calcNorth(t_in: Terrain): Long {
        var total = 0L
        for (row in 0..t_in.lastIndex) {
            total += (t_in[row].count { it == 'O' } * (t_in.size - row)).toLong()
        }
        return total
    }

    fun part1(input: List<String>): Long {
        var t = input.map { it.toList() }

        var t_north = tiltNorth(t)

        return calcNorth(t_north)
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 136L)

    val input = readInput("Day14")
    part1(input).println()
    part2(input).println()
}
