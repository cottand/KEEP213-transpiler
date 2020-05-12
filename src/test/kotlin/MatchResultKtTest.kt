import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MatchResultKtTest {

  @Test
  fun extractComponents() {
    val ms = listOf(Definition("x"), Definition("y"))
    val comps = TypeTestIs(Destructure(null, ms), null).extractComponents()
    assertEquals("x", comps[".component0()"])
    assertEquals("y", comps[".component1()"])
  }

  @Test
  fun extractComponentsComplex() {
    val ms = listOf(Destructure(null, listOf(Definition("x"), Definition("_"))), Definition("y"))
    val comps = TypeTestIs(Destructure(null, ms), null).extractComponents()
    assertEquals("x", comps[".component0().component0()"])
    assertEquals("_", comps[".component0().component1()"])
    assertEquals("y", comps[".component1()"])
  }
}