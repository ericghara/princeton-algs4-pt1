package ericghara.week_9;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class BoggleSolverGetAllValidWords {
    private static final String DICTIONARY_DIR = "src/test/resources/dictionaries/";
    private static final String[] ENGLISH = WordMap.parseDict(DICTIONARY_DIR + "dictionary-yawl.txt");

    private final BoggleSolver solver = new BoggleSolver(ENGLISH);

    @State(Scope.Thread)
    public static class ThisState {
        BoggleBoard board;

        @Setup(Level.Invocation)
        public void setup() {
            board = new BoggleBoard(4,4);
        }

        @TearDown(Level.Invocation)
        public void teardown() {
            board = null;
        }
    }

    @BenchmarkMode(Mode.Throughput)
    @Measurement(time = 2, timeUnit = TimeUnit.SECONDS)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Benchmark
    public void random4x4(ThisState state, Blackhole blackhole) {
        Iterable<String> res = solver.getAllValidWords(state.board);
        blackhole.consume(res);
    }
}
