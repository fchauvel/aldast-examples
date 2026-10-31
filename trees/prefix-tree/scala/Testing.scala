// A few helpers to write tests that read like sentences

def assertTrue(condition: Boolean): Unit =
  if !condition then
    throw AssertionError("expected true, but got false")

def assertFalse(condition: Boolean): Unit =
  if condition then
    throw AssertionError("expected false, but got true")

def assertEquals(actual: Any, expected: Any): Unit =
  if actual != expected then
    throw AssertionError(s"expected $expected, but got $actual")

// Runs one test, and reports whether it passed
def test(description: String)(body: => Unit): Unit =
  try
    body
    println("ok      " + description)
  catch
    case failure: Throwable =>
      println("FAILED  " + description + ": " + failure)
      sys.exit(1)
