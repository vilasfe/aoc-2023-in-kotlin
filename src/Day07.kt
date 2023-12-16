fun main() {

    class Card {
        val value: Int
        constructor(c: Char) {
            value = if (c.isDigit()) c.digitToInt() else when(c) {
                'T' -> 10
                'J' -> 11
                'Q' -> 12
                'K' -> 13
                'A' -> 14
                else -> throw IllegalArgumentException("Illegal Card")
            }
        }
    }

    class Hand {
        val cards: List<Card>
        val bid: Int
        val type: Int

        constructor(cardStr: String, bidStr: String) {
            cards = cardStr.map { Card(it) }
            bid = bidStr.toInt()
            val qty = cards.groupingBy { it.value }.eachCount().values
            if  (qty.contains(5)) {
                type = 7
            } else if (qty.contains(4)) {
                type = 6
            } else if (qty.contains(3) && qty.contains(2)) {
                type = 5
            } else if (qty.contains(3)) {
                type = 4
            } else if (qty.count { it == 2 } == 2) {
                type = 3
            } else if (qty.contains(2)) {
                type = 2
            } else {
                type = 1
            }
        }
    }

    class HandComparatorPart1: Comparator<Hand> {
        override fun compare(h1: Hand, h2: Hand): Int {
            if (h1.type < h2.type) {
                return -1
            } else if (h1.type > h2.type) {
                return 1
            } else {
                // the hands are the same, so compare the hands by card in order
                for ((l,r) in h1.cards zip h2.cards) {
                    val c = l.value.compareTo(r.value)
                    if (c != 0) {
                        return c
                    }
                }
            }
            return 0
        }
    }

    fun part1(input: List<String>): Int {
        val game = input.map {
            val s = it.split(" ")
            Hand(s[0], s[1])
        }

        //val comparator = compareBy<Hand> { it.type }.thenBy { it.cards }
        val comparator = HandComparatorPart1()

        val sortedGames = game.sortedWith(comparator)

        //sortedGames.forEach { println("${it.type}, ${it.cards.fold("") { s, i -> s + "${i.value.toString()} " }}: ${it.bid}") }

        val total = sortedGames.foldIndexed(0) { idx, total, it -> total + (idx + 1) * it.bid }

        return total
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    println("Test Part 1: ${part1(testInput)}")
    check(part1(testInput) == 6440)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
