fun main() {
    data class Part(val cool: Int, val music: Int, val aero: Int, val shiny: Int)

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

    data class Rule(val condition: String, val result: String)

    fun String.toRule(): Rule {
        val s = this.split(":")
        if (s.size == 1) {
            return Rule("default", s[0])
        } else {
            return Rule(s[0], s[1])
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
                if (r.condition == "default") {
                    return r.result
                } else {
                    val opLess = when (r.condition[1]) {
                        '<' -> true
                        '>' -> false
                        else -> throw IllegalArgumentException("invalid operand")
                    }
                    val compare = r.condition.substring(2).toInt()
                    when(r.condition[0]) {
                        'x' -> if ((opLess && p.cool < compare) || (!opLess && p.cool > compare)) return r.result else continue
                        'm' -> if ((opLess && p.music < compare) || (!opLess && p.music > compare)) return r.result else continue
                        'a' -> if ((opLess && p.aero < compare) || (!opLess && p.aero > compare)) return r.result else continue
                        's' -> if ((opLess && p.shiny < compare) || (!opLess && p.shiny > compare)) return r.result else continue
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

    fun part1(input: List<String>): Int {
        val gap = input.indexOf("") // find the empty line
        val system = input.subList(0, gap).associate { it ->
            it.substring(0, it.indexOf("{")) to it.substring(it.indexOf("{")).toWorkflow()
        }
        system.println()

        val parts = input.subList(gap+1, input.size).map { it.toPart() }

        // update this
        val acceptedParts = parts.filter { p ->
            applyRules(p, system, "in") == "A"
        }

        return acceptedParts.fold(0) { tot, p ->
            tot + p.cool + p.music + p.aero + p.shiny
        }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 19114)

    val input = readInput("Day19")
    part1(input).println()
    part2(input).println()
}
