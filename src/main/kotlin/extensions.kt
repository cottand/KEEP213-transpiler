import java.util.UUID

fun <A, B> List<Map<A, B>>.merge() =
  map { it.entries }.toSet().flatten()
    .map { (k, v) -> k to v }
    .toMap()

fun quickUUID() = UUID.randomUUID().toString().take(6)

fun Iterable<String>.joinLines() = joinToString(separator = "\n")
fun Sequence<String>.joinLines() = joinToString(separator = "\n")

