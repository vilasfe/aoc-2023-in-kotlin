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
        val cards: List<Card> = input.map { it -> parseCard(it) }

        var copies = mutableMapOf<Int, Int>()

        for (c in cards) {
            // add the original card
            copies.put(c.index, copies.getOrDefault(c.index, 0) + 1)
            val matches = c.winning.intersect(c.numbers).size
            // number of matches = number of future cards to update
            // qty of this card = qty to add to future cards
            val toAdd = copies.getOrDefault(c.index, 1)
            for (i in 1..matches) {
                copies.put(i+c.index, copies.getOrDefault(i+c.index, 0) + toAdd)
            }
        }

        val maxIndex = cards.maxWith(Comparator.comparingInt { it.index } ).index

        return copies.filter { (k,_) -> k <= maxIndex }.values.fold(0) { sum, v -> sum + v }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    part1(testInput).println()
    check(part1(testInput) == 13)

    val input = readInput("Day04")
    part1(input).println()

    part2(testInput).println()
    check(part2(testInput) == 30)
    part2(input).println()
}
