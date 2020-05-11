import KotlinParser.TypeTestContext
import KotlinParser.WhenEntryContext
import KotlinParser.WhenExpressionContext
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

fun main(args: Array<String>) {
  val filename = args[0]
  val input = File(filename)
  if (!(input.exists() && input.isFile)) {
    throw IllegalArgumentException("No such file $filename")
  }
  val stream = CharStreams.fromPath(input.toPath())
  val lexer = KotlinLexer(stream)
  val tokens = CommonTokenStream(lexer)
  val scriptTree = KotlinParser(tokens).script()
  Visitor().visit(scriptTree)
}

class Visitor : KotlinParserBaseVisitor<Unit>() {
  val replacings = emptyMap<String, String>()
  override fun visitWhenExpression(ctx: WhenExpressionContext?) {
    ctx ?: return
    val entries = ctx.whenEntry()
    // ignore this `when` if no condition is a TypeTest
    if (!entries.any { entry -> entry.whenCondition().any { it is TypeTestContext } })
      return
    val toBeReplaced = ctx.text
    val ast = entries.map(WhenEntryContext::ast).map(Entry::makePredicate)
    val (header, footer) = "(${ctx.expression().text}).let { $x -> \n" to "\n}"
  }
}

val whenBody = "when {\n" to "\n}\n"
const val x = "__x_"

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
    "val $ident = $x.$chain"
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

fun Entry.makePredicate(): MatchResult = TODO()
