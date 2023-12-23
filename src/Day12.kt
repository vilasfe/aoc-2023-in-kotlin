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
                if (groups.size == 1) {
                    return 1
                } else {
                    return 0
                }
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

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    println("Part1 test: ${part1(testInput)}")
    check(part1(testInput) == 21)

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
