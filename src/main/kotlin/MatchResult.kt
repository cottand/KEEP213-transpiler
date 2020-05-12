/**
 * [conditionsLHS]: chains of componentN() calls associated to RHS of predicates
 * that need to pass
 * [extractedIdent]: (extraction to ident), where extreaction is a chain of
 * `componentN()` calls and ident is the name the user gave the matched identifier.
 * [body]: is tge RHS body of the `when` entry
 */
data class MatchResult(
  val conditionsLHS: Map<List<Int>, String>,
  val extractedIdent: Map<List<Int>, String>,
  val body: Body
) {

  private fun genExtracted() = extractedIdent.map { (chain, ident) ->
    "val $ident = $subject$chain"
  }.joinToString(separator = "\n")

  fun genIfChain(): String {
    TODO()
  }
}

fun makeMatchResult(entry: CondEntry): MatchResult {
  val (conditions, body) = entry
  if (conditions.size != 1) error("Disjunctions are not supported by this prototype")
  val cond = conditions.first().ifChain()
  val components =
    conditions.map { it.extractComponents() }.merge()
  return MatchResult(cond, components, body)
}

fun Condition.ifChain(): Map<List<Int>, String> = when (this) {
  is RangeTest -> mapOf(listOf<Nothing>() to text)
  is Expression -> mapOf(listOf<Nothing>() to "== ($text)")
  is TypeTestIs -> {
    if (guard != null) TODO("Guards not implemented yet")
    match.extractConditions()
  }
}

private fun Match.extractConditions(): Map<List<Int>, String> {
  TODO()
}

fun Condition.extractComponents(): Map<List<Int>, String> {
  if (this !is TypeTestIs) return emptyMap()
  if (guard != null) TODO("Guards not implemented")
  return when (match) {
    is TypeCheckOnly -> mapOf()
    is Definition -> error("Can't define a variable in the top level")
    is Destructure -> match.ms.extractComponents()
  }
}

fun Match.extractComponents(): Map<List<Int>, String> = when (this) {
  is TypeCheckOnly -> mapOf()
  is Definition -> mapOf(listOf<Nothing>() to name)
  is Destructure -> ms.extractComponents()
}

fun List<Match>.extractComponents() =
  mapIndexed { index, match ->
    match.extractComponents().map { (comps, vName) ->
      listOf(index) + comps to vName
    }.toMap()
  }.merge()
