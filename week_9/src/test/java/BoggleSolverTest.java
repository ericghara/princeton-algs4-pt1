import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@TestInstance(Lifecycle.PER_CLASS)
class BoggleSolverTest {
    private static final String BOARD_DIR = "src/test/resources/boards";
    private static final String DICTIONARY_DIR = "src/test/resources/dictionaries";
    private static final String DICTIONARY_FILTER = "dictionary-";
    private static final String DEFAULT_DICTIONARY = "dictionary-yawl.txt";  // used by all but the constructor unit test
    private static final String BOARD_FILTER = "board-points"; // used to identify board files
    private static final String BOARD_EXT = ".txt";


    /**
     * General purpose method get a list of filenames in a directory that contain a specific substring
     *
     * @param dir - path to directory to be searched
     * @param filter - a unique substring that is only contained in the desired files
     * @return - a list of files matching the filter
     */
    private static List<String> getFiles(String dir, String filter) {
        List<String> files;
        Path path = Paths.get(dir);
        try {
            // Get board filenames
            files = Files.find(path, 1, (p, matcher) -> matcher.isRegularFile())
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter((x) -> x.contains(filter))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not open the specified path");
        }
        return files;
    }

    /**
     * Imports all dictionary files from the {@code DICTIONARY_DIR} containing the {@code DICTIONARY_FILTER}
     * in their filename.  The dictionary is imported as a String[].
     *
     * @return arguments(String [] importedDict,String filename)
     */
    public Stream<Arguments> importDicts() {
        List<String> files = getFiles(DICTIONARY_DIR, DICTIONARY_FILTER);

        List<Arguments> args = new LinkedList<>();
        for (String filename : files) {
            String fullPath = DICTIONARY_DIR + "/" + filename;
            String[] importedDict = WordMap.parseDict(fullPath);
            args.add(Arguments.arguments( (Object) importedDict, filename ));
        }
        return args.stream();
    }

    @ParameterizedTest( name = "{index} - {1}" )
    @Timeout( value = 5, unit = SECONDS )
    @MethodSource( "importDicts" )
    public void constructor(String[] dictionary, String filename) {
                // filename arg just used to label test
                BoggleSolver solver =  new BoggleSolver(dictionary);
                // really just trying to get timing info, making sure compiler doesn't optimize away an unused class.
                Assertions.assertTrue( solver.scoreOf("hat") >= 0 );
    }

    @TestInstance(Lifecycle.PER_CLASS)
    @Nested
    class BoggleSolverSearchTest{
        private final BoggleSolver solver;

        public BoggleSolverSearchTest() {
            String dictPath = DICTIONARY_DIR + "/" + DEFAULT_DICTIONARY; // all tests use this dictionary
            solver = new BoggleSolver( WordMap.parseDict(dictPath) );
        }

        /**
         * Imports all board text files from the {@code BOARD_DIR} containing the {@code BOARD_FILTER}
         * in their filename.  The files are converted to a BoggleBoard type.  Board names should <em>only<em/>
         * list the expected number of points when solved with {@code DEFAULT_DICTIONARY}  after the
         * {@code BOARD_FILTER} and before {@code BOARD_EXT}.  The expected result for the unit test is parsed
         * from the filename. For example: BOARD_FILTER: "board-points", BOARD_EXT: ".txt"
         * acceptable filename: xyzBoggle-<u>board-points</u>123<u>.txt</u>,
         * unacceptable filename xyzBoggle-<u>board-points</u>123xyz<u>.txt</u>.
         *
         * @return arguments(BoggleBoard board,int points)
         */
        public Stream<Arguments> importBoards() {
            List<String> files = getFiles(BOARD_DIR, BOARD_FILTER);
            LinkedList<Arguments> args = new LinkedList<>();
            final int N = BOARD_FILTER.length();
            final int E = BOARD_EXT.length();

            // create board objects, parse expected number of points
            for (String filename : files) {
                String fullPath = BOARD_DIR + "/" + filename;
                BoggleBoard board = new BoggleBoard(fullPath);
                int i = filename.lastIndexOf(BOARD_FILTER) + N;
                int points = Integer.parseInt(filename.substring(i, filename.length() - E));
                args.add( Arguments.arguments(board, points));
            }
            return args.stream();
        }

        @ParameterizedTest( name = "{index} - {1} points" )
        @MethodSource( "importBoards")
        void getAllValidWords(BoggleBoard board, int points) {
            Iterable<String> words = solver.getAllValidWords(board);
            int score = 0;
            for (String s: words) { score += solver.scoreOf(s); };
            assertEquals(score, points );
        }
    }
}