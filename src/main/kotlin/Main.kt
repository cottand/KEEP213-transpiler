import KotlinParser.WhenExpressionContext
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import kotlin.collections.Map.Entry

fun main(args: Array<String>) {
  val filename = args[0]
  val input = File(filename)
  if (!(input.exists() && input.isFile)) {
    throw IllegalArgumentException("No such file $filename")
  }
  val stream = CharStreams.fromPath(input.toPath())
  val lexer = KotlinLexer(stream)
  val tokens = CommonTokenStream(lexer)
  val parser = KotlinParser(tokens)
}

class Visitor : KotlinParserBaseVisitor<Unit>() {
  val replacings = emptyMap<String, String>()
  override fun visitWhenExpression(ctx: WhenExpressionContext?) {
    ctx?:return

  }

}