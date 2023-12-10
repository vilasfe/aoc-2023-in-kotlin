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
