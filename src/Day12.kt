fun main() {

    fun detectCombinations(conditions: String, groups: List<Int>): Int {

        // declare the parsing functions as local functions within this function for scope handling
        fun handleOperational(): Int {
            //println("handle Operational")
            // just skip this one
            return detectCombinations(conditions.substring(1), groups)
        }

        fun handleDamaged(): Int {
            //println("handle Damaged")
            // make sure this group can fit
            if (conditions.length < groups[0] || conditions.substring(0, groups[0]).replace('?', '#') != "#".repeat(groups[0])) {
                return 0
            }

            // if this is the rest of the record is the last group then done
            if (conditions.length == groups[0]) {
                return if (groups.size == 1) 1 else 0
            }

            // if the character after this group can be a separator then recurse
            // using the next group and consuming any matching chars for this group
            if ("?.".contains(conditions[groups[0]])) {
                return detectCombinations(conditions.substring(groups[0]+1), groups.subList(1, groups.lastIndex+1))
            }

            // default if we somehow got here
            return 0
        }

        // BASE CASE processing for recursion
        //println("base case")
        // if there are no more groups to investigate
        if (groups.isEmpty()) {
            // if there are lso are not any more damaged springs
            if (! conditions.contains('#')) {
                // there is a spring
                return 1
            } else {
                // damage prevents us from making a spring
                return 0
            }
        }

        // now we know we have more groups, so if there are nor more
        // conditions then we cannot fit a spring solution
        if (conditions.isEmpty()) {
            return 0
        }

        // now go into the recursive case
        val retval = when (conditions[0]) {
            '.' -> handleOperational();
            '#' -> handleDamaged();
            '?' -> handleOperational() + handleDamaged()
            else -> throw IllegalArgumentException("Illegal spring notation")
        }

        //println("$conditions : $groups, $retval")
        return retval
    }

    fun part1(input: List<String>): Int {
        val parsedLists = input.map { it.split("\\s+".toRegex()) }
        val parsedPairs = parsedLists.map { Pair(it[0], it[1].split(",").map { g -> g.toInt() }) }

        val combos = parsedPairs.map { it -> detectCombinations(it.first, it.second)
        //    .also { println("---") }
        }
        return combos.sum()
    }

    fun detectCombinationsPart2(conditions: String, groups: List<Int>): Long {
        // Setup the list of good springs with a leading good one
        val goodSprings = mutableListOf('.')
        goodSprings.addAll(conditions.dropWhile { it == '.' }.toList())

        // possible combos starts with 1 at each location
        var possibleCombos = MutableList(goodSprings.size + 1) { 1L }

        // Set the possible combos to 0 if the spring is damaged
        for ((idx, _) in goodSprings.withIndex().dropWhile { (_, c) -> c != '#' }) {
            possibleCombos[idx + 1] = 0
        }

        // Now check for each group and start updating
        for (g in groups) {
            // create a new row in the test matrix
            val nextCombos = MutableList(goodSprings.size + 1) { 0L }
            var runSize = 0

            for ((idx, c) in goodSprings.withIndex().drop(1)) {
                if (c == '.') {
                    runSize = 0
                } else {
                    runSize += 1
                }

                // evaluate the rest of the list in this row
                val groupCanFit = runSize >= g
                val prevIdx = (idx - g).coerceAtLeast(0) // make sure indexes are nonnegative
                val hasDamageGap = goodSprings[prevIdx] != '#'
                val isNotDamaged = c != '#'
                val dmgCanFit = groupCanFit && hasDamageGap

                nextCombos[idx+1] =
                    if (isNotDamaged && dmgCanFit) {
                        nextCombos[idx] + possibleCombos[prevIdx]
                    } else if (c == '#' && dmgCanFit) {
                        possibleCombos[prevIdx]
                    } else if (isNotDamaged) {
                        nextCombos[idx]
                    } else {
                        0L
                    }
            }

            // move to the next layer
            possibleCombos = nextCombos
        }

        return possibleCombos.last()
    }

    fun part2(input: List<String>): Long {
        val parsedLists = input.map { it.split("\\s+".toRegex()) }
        val parsedPairs = parsedLists.map { s ->
            Pair((s[0]+"?").repeat(4) + s[0],
                List(5) { s[1].split(",").map { g -> g.toInt() }}.flatten() )
        }

        //parsedPairs[0].println()

        val combos = parsedPairs.map { it -> detectCombinationsPart2(it.first, it.second)
            //.also { println("---") }
        }
        return combos.sum()

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    println("Part1 test: ${part1(testInput)}")
    check(part1(testInput) == 21)
    println("Part2 test: ${part2(testInput)}")
    check(part2(testInput) == 525152L)

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
