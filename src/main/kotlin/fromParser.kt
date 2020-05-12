import KotlinParser.*

fun notReached(): Nothing = error("Not reached")

fun WhenEntryContext.ast(): Entry {
  if (ELSE() != null) {
    val body = if (controlStructureBody().block() == null) {
      controlStructureBody().expression().text
    } else {
      controlStructureBody().block().text
    }
    return ElseEntry(body)
  }
  val conds = whenCondition().let {
    if (it.isNullOrEmpty()) {
      listOf<Nothing>()
    }
    else
      it.map(WhenConditionContext::ast)
  }
  val bodyCtx = controlStructureBody()
  val body = when {
    bodyCtx.block() != null -> BodyBlock(bodyCtx.block().statements().text)
    bodyCtx.expression() != null -> BodyExpr(bodyCtx.expression().text)
    else -> notReached()
  }
  return CondEntry(conds, body)
}

fun WhenConditionContext.ast(): Condition = when (this) {
  is ExprContext -> Expression(text)
  is RangeContext -> RangeTest(text)
  is IsCheckContext -> with(typeTest().guardedMatch()) {
    TypeTestIs(match().ast(), guard()?.ast())
  }
  else -> notReached()
}

private fun GuardContext.ast() = Guard(expression().text)

private fun MatchContext.ast(): Match = when (this) {
  is InstanceOfContext -> TypeCheckOnly(Type(type().text))
  is DestructureContext -> {
    val t = type()?.let { Type(it.text) }
    val ms = destructuredTupleTypeTest().match().map { it.ast() }
    Destructure(t, ms)
  }
  is DefinitionContext -> Definition(this.identifier().text)
  else -> notReached()
}
