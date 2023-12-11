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

    fun part1(input: List<String>): Long {
        val parsed = parseInput(input)

        var foundLocations = mutableListOf<Long>()

        val SEED = "seed"
        val LOCATION = "location"
        for (s in parsed.seedList) {
            var curIndex = s
            var mapKey = SEED
            var myMap = parsed.locationMap.filter { (k,_) -> k.first == mapKey }
            var newKey = myMap.keys.first().second

            depthfirst@ while (true) {
                var found = myMap.get(Pair(mapKey, newKey))?.filter { (k, _) -> k.contains(curIndex) }

                if (found == null || found.size == 0) {
                    // just use the input index
                } else {
                    val offset = curIndex - found.keys.first().start
                    curIndex = found.values.first() + offset
                }
                mapKey = newKey
                myMap = parsed.locationMap.filter { (k,_) -> k.first == mapKey }
                if (myMap.size == 0) {
                    if (mapKey == LOCATION) {
                        foundLocations.add(curIndex)
                    }
                    break@depthfirst
                }
                newKey = myMap.keys.first().second
            }
        }

        return foundLocations.min()
    }

    fun part2(input: List<String>): Long {
        return input.size.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    part1(testInput).println()
    check(part1(testInput) == 35L)

    val input = readInput("Day05")
    part1(input).println()

    part2(testInput).println()
    //check(part2(testInput) == 1L)
    part2(input).println()
}
