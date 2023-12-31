fun main() {
    data class Part(val cool: Int, val music: Int, val aero: Int, val shiny: Int)

    data class PartRange(val cool: List<IntRange>, val music: List<IntRange>, val aero: List<IntRange>, val shiny: List<IntRange>)

    fun String.toPart(): Part {
        val s = this.filterNot { it == '{' || it == '}' }
        val split_s = s.split(",")
        var cool = 0
        var music = 0
        var aero = 0
        var shiny = 0

        split_s.forEach { attrib ->
            val num = attrib.substring(2).toInt()
            when (attrib[0]) {
                'x' -> cool = num
                'm' -> music = num
                'a' -> aero = num
                's' -> shiny = num
                else -> throw IllegalArgumentException("invalid part factor")
            }
        }
        return Part(cool, music, aero, shiny)
    }

    data class Rule(val attribute: Char, val trueRange: IntRange, val result: String)

    fun String.toRule(): Rule {
        val s = this.split(":")
        if (s.size == 1) {
            return Rule('d', (1..4000), s[0])
        } else {
            val compare = s[0].substring(2).toInt()
            val opLess = when (s[0][1]) {
                '<' -> true
                '>' -> false
                else -> throw IllegalArgumentException("invalid operand")
            }
            val valid = if (opLess) 1..compare-1 else compare+1..4000

            return Rule(s[0][0], valid, s[1])
        }
    }

    data class Workflow(val rules: List<Rule>)

    fun String.toWorkflow(): Workflow {
        val s = this.filterNot { it == '{' || it == '}' }
        val split_s = s.split(",")
        return Workflow(split_s.map { it.toRule() })
    }

    fun applyRules(p: Part, system: Map<String, Workflow>, start: String): String {
        fun applyWorkflow(p: Part, w: Workflow): String {
            for (r in w.rules) {
                if (r.attribute == 'd') {
                    return r.result
                } else {
                    when (r.attribute) {
                        'x' -> if (p.cool in r.trueRange) return r.result else continue
                        'm' -> if (p.music in r.trueRange) return r.result else continue
                        'a' -> if (p.aero in r.trueRange) return r.result else continue
                        's' -> if (p.shiny in r.trueRange) return r.result else continue
                        else -> throw IllegalArgumentException("invalid part factor")
                    }
                }
            }
            // we should never be here, so reject the part
            return "R"
        }

        var next = start
        while(next != "R" && next != "A") {
            next = applyWorkflow(p, system[next]!!)
        }
        return next
    }

    fun intersect(r1: IntRange, r2: IntRange): IntRange {
        if (r1.start <= r2.last && r1.last >= r2.start) {
            return maxOf(r1.start, r2.start)..minOf(r1.last, r2.last)
        }
        return IntRange.EMPTY
    }

    fun union(r1: IntRange, r2: IntRange): IntRange {
        if (r1.start <= r2.last && r1.last >= r2.start) {
            return minOf(r1.start, r2.start)..maxOf(r1.last, r2.last)
        }
        return IntRange.EMPTY
    }

    fun splitRanges(r: List<IntRange>, trueRange: IntRange): Pair<List<IntRange>, List<IntRange>> {
        var pass = mutableListOf<IntRange>()
        var fail = mutableListOf<IntRange>()

        r.forEach {
            val valid = intersect(it, trueRange)
            if (valid.isEmpty()) {
                fail.add(it)
            } else if (valid == it) {
                pass.add(it)
            } else {
                if (it.first < valid.first) {
                    fail.add(it.first..valid.first-1)
                }
                if (it.last > valid.last) {
                    fail.add(valid.endInclusive+1..it.endInclusive)
                }
                pass.add(valid)
            }
        }
        return Pair(pass, fail)
    }


    fun applyRuleset(p: PartRange, ruleset: List<Rule>): List<Pair<String, PartRange>> {
        when (ruleset[0].attribute) {
            // pass case then fail case
            'x' -> {
                val newX = splitRanges(p.cool, ruleset[0].trueRange)
                return listOf(Pair(ruleset[0].result, PartRange(newX.first, p.music, p.aero, p.shiny))).plus(applyRuleset(PartRange(newX.second, p.music, p.aero, p.shiny), ruleset.subList(1, ruleset.size)))
            }
            'm' -> {
                val newM = splitRanges(p.music, ruleset[0].trueRange)
                return listOf(Pair(ruleset[0].result, PartRange(p.cool, newM.first, p.aero, p.shiny))).plus(applyRuleset(PartRange(p.cool, newM.second, p.aero, p.shiny), ruleset.subList(1, ruleset.size)))
            }
            'a' -> {
                val newA = splitRanges(p.aero, ruleset[0].trueRange)
                return listOf(Pair(ruleset[0].result, PartRange(p.cool, p.music, newA.first, p.shiny))).plus(applyRuleset(PartRange(p.cool, p.music, newA.second, p.shiny), ruleset.subList(1, ruleset.size)))
            }
            's' -> {
                val newS = splitRanges(p.shiny, ruleset[0].trueRange)
                return listOf(Pair(ruleset[0].result, PartRange(p.cool, p.music, p.aero, newS.first))).plus(applyRuleset(PartRange(p.cool, p.music, p.aero, newS.second), ruleset.subList(1, ruleset.size)))
            }
            // default range
            else -> return if (ruleset[0].result == "R") listOf<Pair<String, PartRange>>() else listOf(Pair(ruleset[0].result, p))
        }
    }

    fun applyRulesToRange(p: PartRange, system: Map<String, Workflow>, start: String): List<PartRange> {

        var tmp = ArrayDeque<Pair<String, PartRange>>()
        var accepted = mutableListOf<PartRange>()

        tmp.add(Pair(start, p))

        while (! tmp.isEmpty() ) {
            val toProcess = tmp.removeFirst()
            when (toProcess.first) {
                "A" -> accepted.add(toProcess.second)
                "R" -> continue
                else -> {
                    val w = system[toProcess.first]!!
                    tmp.addAll(applyRuleset(toProcess.second, w.rules))
                }
            }
        }

        //accepted.println()

        return accepted
    }

    fun part1(input: List<String>): Int {
        val gap = input.indexOf("") // find the empty line
        val system = input.subList(0, gap).associate { it ->
            it.substring(0, it.indexOf("{")) to it.substring(it.indexOf("{")).toWorkflow()
        }

        val parts = input.subList(gap+1, input.size).map { it.toPart() }

        // find the accepted parts
        val acceptedParts = parts.filter { p ->
            applyRules(p, system, "in") == "A"
        }

        return acceptedParts.fold(0) { tot, p ->
            tot + p.cool + p.music + p.aero + p.shiny
        }
    }

    fun part2(input: List<String>): Long {
        val gap = input.indexOf("") // find the empty line
        val system = input.subList(0, gap).associate { it ->
            it.substring(0, it.indexOf("{")) to it.substring(it.indexOf("{")).toWorkflow()
        }

        // start from a PartRange where every value is valid
        // apply the "in" rule (sample separates on 's<1351')
        val startRange = PartRange(listOf((1..4000)), listOf((1..4000)), listOf((1..4000)), listOf((1..4000)))
        val result = applyRulesToRange(startRange, system, "in")

        result.println()

        // now we need to go through the overlap of all things in the accepted results
        return result.fold(0L) { total, pr ->
            val goodX = pr.cool.fold(0) { tot, it -> tot + it.endInclusive - it.start + 1 }
            val goodM = pr.music.fold(0) { tot, it -> tot + it.endInclusive - it.start + 1 }
            val goodA = pr.aero.fold(0) { tot, it -> tot + it.endInclusive - it.start + 1 }
            val goodS = pr.shiny.fold(0) { tot, it -> tot + it.endInclusive - it.start + 1 }
            total + goodX.toLong() * goodM.toLong() * goodA.toLong() * goodS.toLong()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 19114)
    part2(testInput).println()
    check(part2(testInput) == 167409079868000L)

    val input = readInput("Day19")
    part1(input).println()
    part2(input).println()
}
