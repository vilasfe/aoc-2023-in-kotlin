import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

/**
 * replace multiple values in a string
 */
fun String.replaceGreedy(mapping: Map<String, String>): String {
    var str = this

    var found = str.indexOfAny(mapping.keys)
    while (found != -1) {
        loop@ for((k, v) in mapping) {
          if (str.substring(found).startsWith(k)) {
            str = str.replaceFirst(k, v)
            break@loop
          }
        }
        found = str.indexOfAny(mapping.keys)
    }

    return str
}

/**
 * replace multiple disjoint values in a string
 */
fun String.replace(mapping: Map<String, String>): String {
    var str = this
    mapping.forEach { str = str.replace(it.key, it.value) }
    return str
}

/**
 * Find indexes of items matching predicate
 */
fun <E> Iterable<E>.indexesOf(predicate: (E) -> Boolean) = mapIndexedNotNull { index, elem -> index.takeIf { predicate(elem) } }

/**
 * MutablePair so we can modify entries in a list
 */
data class MutablePair<T, U>(var first: T, var second: U)

/**
 * Make a terrain grid for reuse
 */
typealias Terrain = List<List<Char>>

/**
 * Least Common Multiple calculations
 */
fun Long.lcm(b: Long): Long {
    val larger = if (this > b) this else b
    val maxLcm = this * b
    var lcm_ret = larger
    while (lcm_ret <= maxLcm) {
        if (lcm_ret % this == 0L && lcm_ret % b == 0L) {
            return lcm_ret
        }
        lcm_ret += larger
    }
    return maxLcm
}

fun findLCMOfListOfNumbers(numbers: List<Long>): Long {
    return numbers.reduce(Long::lcm)
}

