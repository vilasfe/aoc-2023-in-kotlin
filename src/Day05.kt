fun main() {

    data class SeedMap(val seedList: List<Long>, val locationMap: Map<Pair<String,String>, Map<LongRange, Long>>)

    fun parseInput(input: List<String>): SeedMap {
        val seedStrList = input[0].split("\\s+".toRegex())
        val seedList = seedStrList.subList(1, seedStrList.size).map { it.toLong() }

        var locationMap = mutableMapOf<Pair<String, String>, Map<LongRange, Long>>()
        var currentMap = mutableMapOf<LongRange, Long>()
        var from = ""
        var to = ""

        for (s in input.subList(2, input.size)) {
            if (s == "") {
                locationMap.put(Pair<String,String>(from, to), currentMap)
                currentMap = mutableMapOf<LongRange, Long>()
                from = ""
                to = ""
            }
            else if (s.endsWith(" map:")) {
                val fromTo = s.split("\\s+".toRegex())[0]
                val fromToParsed = fromTo.split("-")
                from = fromToParsed[0]
                to = fromToParsed[2]
            }
            else {
                val parsed = s.split("\\s+".toRegex()).map { it.toLong() }
                currentMap.put(parsed[1]..parsed[1]+parsed[2], parsed[0])
            }
        }
        // get the last one at EOF
        locationMap.put(Pair<String,String>(from, to), currentMap)

        return SeedMap(seedList = seedList, locationMap = locationMap)
    }

    fun findLocationForSeed(seed: Long, locationMap: Map<Pair<String,String>, Map<LongRange, Long>>): Long {
        val SEED = "seed"
        val LOCATION = "location"
        var curIndex = seed
        var mapKey = SEED
        var myMap = locationMap.filter { (k,_) -> k.first == mapKey }
        var newKey = myMap.keys.first().second

        while (true) {
            var found = myMap.get(Pair(mapKey, newKey))?.filter { (k, _) -> k.contains(curIndex) }

            if (found == null || found.size == 0) {
                // just use the input index
            } else {
                val offset = curIndex - found.keys.first().start
                curIndex = found.values.first() + offset
            }
            mapKey = newKey
            myMap = locationMap.filter { (k,_) -> k.first == mapKey }
            if (myMap.size == 0) {
                if (mapKey == LOCATION) {
                    return curIndex
                }
                return -1
            }
            newKey = myMap.keys.first().second
        }
    }

    fun intersect(r1: LongRange, r2: LongRange): LongRange {
        if (r1.start <= r2.last && r1.last >= r2.start) {
            return maxOf(r1.start, r2.start)..minOf(r1.last, r2.last)
        }
        return LongRange.EMPTY
    }

    fun remapRange(inputRange: LongRange, destRanges: Map<LongRange, Long>): List<LongRange> {
        var outputRanges = mutableListOf<LongRange>()
        for ((r, v) in destRanges) {
            val overlap = intersect(r, inputRange)
            if (!overlap.isEmpty()) {
                val offset = overlap.start - r.start
                outputRanges.add((v + offset)..(v + overlap.last - overlap.first + offset))
            }
        }
        // add a passthrough if the remap didn't happen
        var noIntersects = mutableSetOf<LongRange>()
        noIntersects.add(inputRange)
        for (r in destRanges.keys) {
            var next = mutableSetOf<LongRange>()
            for (noIntRange in noIntersects) {
                if (noIntRange.first < r.start) {
                    next.add(noIntRange.first..minOf(noIntRange.last, r.start-1))
                }
                if (noIntRange.last > r.last) {
                    next.add(maxOf(noIntRange.first, r.last)..noIntRange.last)
                }
            }
            noIntersects.clear()
            noIntersects.addAll(next)
        }
        outputRanges.addAll(noIntersects)

        return outputRanges
    }

    fun findLocationRangeForSeedRange(seedRange: LongRange, locationMap: Map<Pair<String,String>, Map<LongRange, Long>>): List<LongRange> {
        val SEED = "seed"
        val LOCATION = "location"
        var mapKey = SEED
        var myMap = locationMap.filter { (k,_) -> k.first == mapKey }
        var newKey = myMap.keys.first().second
        var currentRangeList = mutableListOf<LongRange>()
        currentRangeList.add(seedRange)

        var nextRangeList = mutableListOf<LongRange>()

        while (true) {
            var destRanges = myMap.get(Pair(mapKey, newKey))!!

            for (r in currentRangeList) {
                nextRangeList.addAll(remapRange(r, destRanges).toSet())
            }

            mapKey = newKey
            myMap = locationMap.filter { (k,_) -> k.first == mapKey }
            if (myMap.size == 0) {
                if (mapKey == LOCATION) {
                    return nextRangeList
                }
                return nextRangeList
            }
            newKey = myMap.keys.first().second
            currentRangeList = nextRangeList
            nextRangeList = mutableListOf<LongRange>()
        }
    }

    fun part1(input: List<String>): Long {
        val parsed = parseInput(input)

        var foundLocations = parsed.seedList.map { findLocationForSeed(it, parsed.locationMap) }

        return foundLocations.min()
    }

    fun part2(input: List<String>): Long {
        val parsed = parseInput(input)

        var minLocation = Long.MAX_VALUE

        for (p in parsed.seedList.chunked(2)) {
            val locationList = findLocationRangeForSeedRange(p[0]..p[0]+p[1]-1, parsed.locationMap)
            val newMin = locationList.minWith(Comparator.comparingLong { it.start } ).start
            minLocation = minOf(minLocation, newMin)
        }

        return minLocation
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    part1(testInput).println()
    check(part1(testInput) == 35L)

    val input = readInput("Day05")
    part1(input).println()

    part2(testInput).println()
    check(part2(testInput) == 46L)
    part2(input).println()
}
