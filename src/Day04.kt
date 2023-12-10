fun main() {

    data class Card(val index: Int, val winning: List<Int>, val numbers: List<Int>)
    fun parseCard(input: String): Card {
        // get index
        val idx_split = input.split(": ")
        val index = idx_split[0].split("\\s+".toRegex())[1].toInt()

        val num_split = idx_split[1].split("|").map { it -> it.trim() }
        val winning = num_split[0].split("\\s+".toRegex()).map { it.trim().toInt() }
        val numbers = num_split[1].split("\\s+".toRegex()).map { it.trim().toInt() }
        return Card(index, winning, numbers)
    }

    fun cardValue(card: Card): Int {
        var value = 0
        val matches = card.winning.intersect(card.numbers).size
        if (matches > 0) {
            value = 1
            for (i in 0..matches-2) {
                value = value * 2
            }
        }
        return value
    }

    fun part1(input: List<String>): Int {
        val cards: List<Card> = input.map { it -> parseCard(it) }

        return cards.fold(0) { sum, it -> sum + cardValue(it) }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    part1(testInput).println()
    check(part1(testInput) == 13)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
