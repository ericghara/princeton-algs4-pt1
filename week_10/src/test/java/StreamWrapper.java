import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * A collection of static methods which facilitate parameterized testing of methods which rely on reading and writing
 * from an {@code InputStream} to an {@code PrintStream}.
 */
public class StreamWrapper {

    static final InputStream inBackup = System.in;
    static final PrintStream outBackup = System.out;

    /**
     * This method is intended for testing methods that read from an input stream and write to an output stream.
     * Three tasks are sequentially performed: 1) write the provided {@code input} to an input stream,
     * 2) run the provided {@code runnable} 3) read the entire output stream.
     *
     * @param input string to be written to an input stream
     * @param runnable a method which will consume the input stream and write to an output stream
     * @return complete contents of the output stream as a String
     */
    public static String run(String input, Runnable runnable) {
        ByteArrayOutputStream out = streamInStreamOut(input);
        runnable.run();
        return out.toString();
    }

    /**
     * Provide a string and this will write it to an input stream and return a fresh output stream, which the
     * calling method can write to.  For a majority of cases, it is recommended to use the {@code run} method
     * instead of this.
     *
     * @param input String to convert to a {@code ByteArrayInputStream}
     * @return an empty {@code ByteArrayOutputStream}
     */
    public static ByteArrayOutputStream streamInStreamOut(String input) {
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes() );
        System.setIn(in);
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        return out;
    }

    /**
     * Sets System input and output streams to their initial targets (i.e. when {@code SteamHandler} [i]class[/i] was loaded).
     */
    public static void resetAll() {
        System.setIn(inBackup);
        System.setOut(outBackup);
    }
}
