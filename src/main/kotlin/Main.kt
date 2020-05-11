import KotlinParser.TypeTestContext
import KotlinParser.WhenEntryContext
import KotlinParser.WhenExpressionContext
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

fun Iterable<String>.joinLines() = joinToString(separator = "\n")

fun main(args: Array<String>) {
  val filename = args[0]
  require(filename.takeLast(4) == ".kts") {
    "This tool can only transpile .kts scripts, sorry!"
  }
  val input = File(filename)
  if (!(input.exists() && input.isFile)) {
    throw IllegalArgumentException("No such file $filename")
  }
  val stream = CharStreams.fromPath(input.toPath())
  val lexer = KotlinLexer(stream)
  val tokens = CommonTokenStream(lexer)
  val scriptTree = KotlinParser(tokens).script()
  val visitor = Visitor()
  visitor.visit(scriptTree)
  val newText = visitor.replacings.entries
    // Go through entries and replace matches one by one on file
    .fold(input.readText()) { text, (replaced, transpiled) ->
      text.replace(replaced, transpiled)
    }

  val newFileName = filename.dropLast(4) + "_gen.kts"
  File(newFileName).writeText(newText)
}

class Visitor : KotlinParserBaseVisitor<Unit>() {
  val replacings = mutableMapOf<String, String>()
  override fun visitWhenExpression(ctx: WhenExpressionContext?) {
    ctx ?: return
    val entries = ctx.whenEntry()
    // ignore this `when` if no condition is a TypeTest
    if (!entries.any { entry -> entry.whenCondition().any { it is TypeTestContext } })
      return
    val toBeReplaced = ctx.text
    val lines = entries
      .map(WhenEntryContext::ast)
      .map { makePredicate(it).genWhenLine() }
      .joinLines()

    val subjectExpression = ctx.expression().text
    val transpiled = """
      ($subjectExpression).let { $subject ->
        when {
          $lines
        }
      }
    """.trimIndent()
    replacings += (toBeReplaced to transpiled)
  }
}

const val subject = "_x_dont_use_"

