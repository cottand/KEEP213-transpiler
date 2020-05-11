fun <A, B> List<Map<A, B>>.merge() =
  map { it.entries }.toSet().flatten()
    .map { (k, v) -> k to v }
    .toMap()