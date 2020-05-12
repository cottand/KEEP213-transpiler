
sealed class Entry
data class CondEntry(val conditions: List<Condition>, val body: Body) : Entry()
// TODO differentiate block from expression
data class ElseEntry(val bodyText: String) : Entry()
sealed class Condition
data class Expression(val text: String) : Condition()
data class RangeTest(val text: String) : Condition()
data class TypeTestIs(val match: Match, val guard: Guard?) : Condition()

/**
 * The text of an [ElseEntry] is not enclosed by curly braces
 */

sealed class Match
data class TypeCheckOnly(val t: Type) : Match()
data class Definition(val name: String) : Match()
data class Destructure(val t: Type?, val ms: List<Match>) : Match()

data class Type(val name: String)
data class Guard(val pred: String)
sealed class Body(val text: String)

/**
 * The text of a [BodyBlock] is not enclosed by curly braces
 */
class BodyBlock(text: String) : Body(text)
class BodyExpr(text: String) : Body(text)
