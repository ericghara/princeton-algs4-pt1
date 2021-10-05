public class HelloGoodbye {
    public static void main(String[] args) {
        System.out.printf("Hello %1$s and %2$s.%n", args[0], args[1]);
        System.out.printf("Goodbye %2$s and %1$s.%n", args[0], args[1]);
    }
}
