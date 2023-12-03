fun main() {

    fun maxDrawFromGame(input: String): Map<String, Int> {
      var gameMax = mutableMapOf<String, Int>()
      // break into all draws from ';'
      input.split("; ").forEach { draw ->
          // break into colors on ','
          draw.split(", ").forEach { marble ->
              // Insert or Update max count by color
              val (num, color) = marble.split(" ")
              gameMax[color] = when {
                 gameMax[color] == null -> num.toInt()
                 else -> maxOf(gameMax[color] ?: 0, num.toInt())
              }
          }
      }
      return gameMax
    }

    fun feasible(game: Map<String, Int>, constraint: Map<String, Int>): Boolean {
        for((k,v) in game) {
            if (constraint[k] == null) {
                return false
            } else if (constraint[k] ?: 0 < v) {
                return false
            }
        }
        return true
    }

    fun part1(input: List<String>): Int {
        val maxInput = mapOf("red" to 12, "green" to 13, "blue" to 14)
        var feasibleIndices = mutableListOf<Int>()
        input.forEach { g ->
            val (left, right) = g.split(": ")
            val ( _, idx) = left.split(" ")
            if (feasible(maxDrawFromGame(right), maxInput)) {
                feasibleIndices.add(idx.toInt())
            }
        }
        return feasibleIndices.sum()
    }

    fun part2(input: List<String>): Int {
        val games = input.map { g ->
            val (_, right) = g.split(": ")
            maxDrawFromGame(right).values.reduce { acc, i -> acc * i }
        }
        return games.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
