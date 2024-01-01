import java.util.PriorityQueue

enum class PulseType {
    LOW,
    HIGH
}

fun main() {

    data class Pulse(val type: PulseType, val prev: String, val next: String)

    abstract class Module(val name: String, val tgtList: List<String>) {
        abstract fun processPulse(p: Pulse): List<Pair<Int,Pulse>>
    }

    class FlipFlop(val n: String, val tgt: List<String>) : Module(n, tgt) {
        var stateOn = false

        override fun processPulse(p: Pulse): List<Pair<Int,Pulse>> {
            if (p.type == PulseType.LOW) {
                stateOn = !stateOn
                return buildList{
                    var count = 0
                    tgtList.forEach { add(Pair(count, Pulse( if (stateOn) PulseType.HIGH else PulseType.LOW, p.next, it))) }
                }
            }
            return emptyList()
        }
    }

    class Conjunction(val n: String, val tgt: List<String>) : Module(n, tgt) {
        var stateOn = mutableMapOf<String, PulseType>()

        // track if we have seen a high pulse at all, since the input state might reset before
        // the cycle completes so we would never see it if we inspect below
        var seenHigh = mutableMapOf<String, Boolean>()

        fun addPort(port: String) {
            stateOn[port] = PulseType.LOW
            seenHigh[port] = false
        }

        override fun processPulse(p: Pulse): List<Pair<Int,Pulse>> {
            // update the memory for the input pulse
            // then if all remembered pulses are high then send low
            // else send high
            //if (name == "ft") {
            //    println("${name} setting stateOn[${p.prev}] to ${p.type}")
            //}
            stateOn[p.prev] = p.type

            if (p.type == PulseType.HIGH) {
                seenHigh[p.prev] = true
            }

            val toSend = if (stateOn.values.all { it == PulseType.HIGH }) PulseType.LOW else PulseType.HIGH

            return buildList {
                var count = 0
                tgtList.forEach { add(Pair(count, Pulse(toSend, p.next, it))) }
            }
        }
    }

    class Broadcast(val n: String, val tgt: List<String>) : Module(n, tgt) {
        override fun processPulse(p: Pulse): List<Pair<Int,Pulse>> {
            return buildList{
                var count = 0
                tgtList.forEach { add(Pair(count, Pulse( p.type, p.next, it))) }
            }
        }
    }

    class Sink(val n: String, val tgt: List<String>) : Module(n, tgt) {
        var lowPulseRx = 0
        override fun processPulse(p: Pulse): List<Pair<Int,Pulse>> {
            if (p.type == PulseType.LOW) {
                lowPulseRx++
            }
            return emptyList()
        }
    }

    fun String.toModule(): Module {
        val split_s = this.split(" -> ")
        val targets = split_s[1].split(",\\s+".toRegex())

        if (split_s[0].trim() == "broadcaster") {
            return Broadcast(split_s[0], targets)
        } else {
            return when (split_s[0][0]) {
                '%' -> FlipFlop(split_s[0].substring(1), targets)
                '&' -> Conjunction(split_s[0].substring(1), targets)
                else -> throw IllegalArgumentException("invalid gate type: ${split_s[0]}")
            }
        }
    }

    class Machine {
        var lowPulses = 0L
        var highPulses = 0L

        // message queue based on the "first" field
        var _messageQ = PriorityQueue<Pair<Int,Pulse>>(compareBy { it.first })
        var _modules = mutableMapOf<String, Module>()

        fun isValid(): Boolean {
            return false
        }

        fun connectAndGates() {
            _modules.forEach { (k,v) ->
                v.tgtList.forEach { t ->
                    val tgt = _modules[t]
                    if (tgt != null && tgt is Conjunction) {
                        tgt.addPort(k)
                    }
                }
            }
        }

        fun connectSinks() {
            var toAdd = mutableListOf<Pair<String, Module>>()
            _modules.values.forEach { v ->
                v.tgtList.forEach { t ->
                    if (! _modules.containsKey(t)) {
                        toAdd.add(Pair(t, Sink(t, emptyList())))
                    }
                }
            }
            _modules.putAll(toAdd.associate { it })
        }

        fun isQuiet(): Boolean {
            return _messageQ.isEmpty()
        }

        fun pushButton() {
            // send a low pulse to the broadcaster
            _messageQ.add(Pair(1, Pulse(PulseType.LOW, "in", "broadcaster")))
            // now run the machine
            while (! isQuiet()) {
                val pulse = _messageQ.poll()!!
                //pulse.println()
                _messageQ.addAll(_modules[pulse.second.next]!!.processPulse(pulse.second).map{Pair(it.first + pulse.first, it.second)})
                if (pulse.second.type == PulseType.LOW) {
                    lowPulses++
                } else {
                    highPulses++
                }
            }
        }
    }

    fun part1(input: List<String>): Long {

        // build the machine
        var m = Machine()

        input.forEach {
            val mod = it.toModule()
            m._modules[mod.name] = mod
        }

        // make sure it is valid
        m.connectAndGates()
        m.connectSinks()

        //m._modules.println()

        // push the button
        m.pushButton()

        // solve for 1000 iterations
        val MAX_ITER = 1000

        var count = 1
        while (count < MAX_ITER) {
            m.pushButton()
            count++
        }

        // return the product of the pulse counts
        println("low: ${m.lowPulses} hi: ${m.highPulses}")
        return m.lowPulses * m.highPulses
    }

    fun part2(input: List<String>): Long {

        // build the machine
        var m = Machine()

        input.forEach {
            val mod = it.toModule()
            m._modules[mod.name] = mod
        }

        // make sure it is valid
        m.connectAndGates()
        m.connectSinks()

        val sourceForRx = m._modules.values.filter { v -> v.tgtList.contains("rx") }[0].name
            .also(::println)

        var feeders = (m._modules[sourceForRx] as Conjunction).stateOn.keys.associate { tgt -> tgt to 0L }.toMutableMap()
            .also(::println)

        var count = 0L
        while ((m._modules["rx"] as Sink).lowPulseRx == 0 && feeders.values.any { it == 0L }) {
            //feeders.println()
            for ((f,_) in feeders.filterValues { v -> v == 0L }) {
                if ((m._modules[sourceForRx] as Conjunction).seenHigh[f]!!) {
                    Pair(f, count).println()
                    (m._modules[sourceForRx] as Conjunction).stateOn.println()
                    (m._modules[sourceForRx] as Conjunction).seenHigh.println()
                    feeders[f] = count
                }
            }

            m.pushButton()
            count++
        }

        return feeders.values.reduce(Long::lcm)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    val testInput2 = readInput("Day20_test2")
    check(part1(testInput).also(::println) == 32000000L)
    check(part1(testInput2).also(::println) == 11687500L)

    val input = readInput("Day20")
    part1(input).println()
    part2(input).println()
}
