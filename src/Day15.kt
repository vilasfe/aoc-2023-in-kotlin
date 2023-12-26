fun main() {
    fun reindeerHash(input: String): Int {
        val total = input.toList().fold(0) { tot, it -> ((tot + it.code) * 17) % 256 }
        return total
    }

    fun sumList(input: List<Pair<String, Int>>): Int {
        return input.foldIndexed(0) { idx, t, i ->
            t + (idx+1) * i.second
        }
    }

    fun part1(input: List<String>): Int {
        val parsed = input[0].split(",")

        val hashed = parsed.map { reindeerHash(it) }

        return hashed.sum()
    }

    fun part2(input: List<String>): Int {
        val parsed = input[0].split(",")

        var hashMap = mutableMapOf<Int,MutableList<Pair<String,Int>>>()

        parsed.forEach { p ->
            val key = p.split("=|-".toRegex())[0]
            val op = p[key.length]

            val hashedKey = reindeerHash(key)

            var tmp = hashMap[hashedKey] ?: mutableListOf<Pair<String,Int>>()

            if (op == '-') {
                // remove the value from tmp
                tmp.filter { it.first == key }.forEach { tmp.remove(it) }
            } else if (op == '=') {
                val value = p.substring(key.length+1).toInt()
                // if there is a lens with the value then replace it
                val idx = tmp.indexOfFirst { it.first == key }
                if (idx > -1) {
                    tmp[idx] = Pair(key, value)
                } else {
                // else, add this one to the end
                    tmp.add(Pair(key, value))
                }
            }
            hashMap[hashedKey] = tmp
        }

        var total = hashMap.map { (k,v) ->
            (k + 1) * sumList(v)
        }.sum()

        return total
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    println("Part 1 test: ${part1(testInput)}")
    check(part1(testInput) == 1320)
    println("Part 2 test: ${part2(testInput)}")
    check(part2(testInput) == 145)

    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}
