// A few helpers to write tests that read like sentences
public class Testing {

    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("expected true, but got false");
        }
    }

    public static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("expected false, but got true");
        }
    }

    public static void assertEquals(Object actual, Object expected) {
        if (!actual.equals(expected)) {
            throw new AssertionError("expected " + expected + ", but got " + actual);
        }
    }

    // Runs one test, and reports whether it passed
    public static void test(String description, Runnable body) {
        try {
            body.run();
            System.out.println("ok      " + description);
        } catch (Throwable failure) {
            System.out.println("FAILED  " + description + ": " + failure);
            System.exit(1);
        }
    }
}
