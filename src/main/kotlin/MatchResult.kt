/**
 * [conditions]: list of predicates that need to pass
 * [extracted]: (extraction to ident), where extreaction is a chain of
 * `componentN()` calls and ident is the name the user gave the matched identifier.
 * [body]: is tge RHS body of the `when` entry
 */
data class MatchResult(
  val conditions: List<String>,
  val extracted: Map<String, String>,
  val body: Body
) {

  private fun genExtraced() = extracted.map { (chain, ident) ->
    "val $ident = $subject$chain"
  }.joinToString(separator = "\n")

  fun genWhenLine(): String {
    val conds =
      conditions.joinToString(prefix = "(", separator = ") && (", postfix = ")")
    val bodyTxt = """{
      ${genExtraced()}
      ${body.text}
      }
    """.trimIndent()
    return "$conds -> $bodyTxt"
  }
}

fun makePredicate(entry: Entry): MatchResult {
  val (conditions, body) = entry
  val condExpressions = conditions.map { it.asBool() }
  val componetnts =
    conditions.map { it.extractComponents() }.merge()
  return MatchResult(condExpressions, componetnts, body)
}

fun Condition.asBool() = when (this) {
  is RangeTest -> "$subject $text"
  is Expression -> "$subject == ($text)"
  is TypeTestIs -> TODO()
}

fun Condition.extractComponents(): Map<String, String> {
  if (this !is TypeTestIs) return emptyMap()
  if (guard != null) TODO("Guards not implemented")
  return when (match) {
    is TypeCheckOnly -> mapOf()
    is Definition -> error("Can't define a variable in the top level")
    is Destructure -> match.ms.extract()
  }
}

fun Match.extractComponents(): Map<String, String> = when (this) {
  is TypeCheckOnly -> mapOf()
  is Definition -> mapOf("" to name)
  is Destructure -> ms.extract()
}

fun List<Match>.extract() =
  mapIndexed { index, match ->
    match.extractComponents().map { (comps, vName) ->
      ".component$index()$comps" to vName
    }.toMap()
  }.merge()
