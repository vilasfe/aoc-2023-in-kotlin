fun main() {

    data class Race(val time: Long, val distance: Long)

    fun raceRecords(r: Race): LongRange {
        // the pace matches the number of seconds of winding times the remaining time
        // value = t * (duration - t)
        // so the record setting values are between the roots of
        // 0 = t * (duration - t) - record = -t^2 + duration*t - record
        // using quadratic formula of (-b +- sqrt(b*b - 4*a*x) ) / 2*a
        val a = -1.toDouble()
        val b = r.time.toDouble()
        val c = -r.distance.toDouble()
        val sqrt_input = b*b - 4*a*c
        val root1 = ( -b + Math.sqrt(sqrt_input) ) / 2*a
        val root2 = ( -b - Math.sqrt(sqrt_input) ) / 2*a

        val root1Long = Math.floor(root1).toLong()
        val root2Long = Math.floor(root2).toLong()

        val return1 = if (root1Long.toDouble() < root1) root1Long + 1 else root1Long
        val return2 = if (root2Long.toDouble() < root2) root2Long + 1 else root2Long - 1

        return return1..return2

    }

    fun part1(input: List<String>): Long {
        val times = input[0].split("\\s+".toRegex())
        val distances = input[1].split("\\s+".toRegex())

        val races = (times.subList(1, times.lastIndex+1) zip distances.subList(1, distances.lastIndex+1))
            .map { Race(it.first.toLong(), it.second.toLong()) }

        val records = races.map { raceRecords(it) }

        val acceptableVals = records.map { it.last - it.first }

        val retval = acceptableVals.fold(1L) { prod, it -> prod * it }

        return retval
    }

    fun part2(input: List<String>): Long {
        val times = input[0].split("\\s+".toRegex())
        val distances = input[1].split("\\s+".toRegex())

        val totalTime = times.subList(1, times.lastIndex+1).joinToString(separator="").toLong()
        val totalDist = distances.subList(1, distances.lastIndex+1).joinToString(separator="").toLong()

        val r = Race(totalTime, totalDist)

        val v = raceRecords(r)

        return (v.last - v.first)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288L)

    val input = readInput("Day06")
    part1(input).println()

    check(part2(testInput) == 71503L)
    part2(input).println()
}
