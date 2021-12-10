package ericghara.week_9;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;


@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Measurement(time = 1, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.SECONDS)
public class BoggleSolverConstructor {

    private static final String DICTIONARY_DIR = "src/test/resources/dictionaries/";
    private static final String[] ITALIAN = WordMap.parseDict(DICTIONARY_DIR + "dictionary-zingarelli2005.txt");
    private static final String[] ENGLISH = WordMap.parseDict(DICTIONARY_DIR + "dictionary-yawl.txt");

   @Benchmark
    public void Italian(Blackhole blackhole){
       BoggleSolver solver = new BoggleSolver(ITALIAN);
       blackhole.consume(solver);
   }

   @Benchmark
   public void English(Blackhole blackhole){
       BoggleSolver solver = new BoggleSolver(ENGLISH);
       blackhole.consume(solver);
   }

}
