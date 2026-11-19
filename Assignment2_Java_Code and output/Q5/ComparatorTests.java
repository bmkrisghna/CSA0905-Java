import java.util.*;

/** Data set with ties + a deliberately faulty comparator. */
public class ComparatorTests {
    record T(String name, int points, int goalDiff) { }

    public static void main(String[] args) {
        List<T> teams = List.of(
                new T("Panthers", 9, 4), new T("Eagles", 9, 6), new T("Cobras", 6, 1),
                new T("Bisons", 9, 4), new T("Aces", 6, 1), new T("Zebras", 6, 3));

        Comparator<T> good = Comparator.comparingInt((T t) -> t.points()).reversed()
                .thenComparing(Comparator.comparingInt((T t) -> t.goalDiff()).reversed())
                .thenComparing(T::name);

        Comparator<T> noTieBreaker = Comparator.comparingInt((T t) -> t.points()).reversed()
                .thenComparing(Comparator.comparingInt((T t) -> t.goalDiff()).reversed());   // ignores name

        Comparator<T> subtraction = (a, b) -> b.points() - a.points();    // faulty: overflow risk, no tie-breaks

        System.out.println("Good         : " + sorted(teams, good));
        System.out.println("No tie-break : " + sorted(teams, noTieBreaker));
        System.out.println("Subtraction  : " + sorted(teams, subtraction));

        // overflow demonstration for subtraction
        System.out.println("Subtraction overflow: Integer.MAX_VALUE - (-1) = " + (Integer.MAX_VALUE - (-1)) + "  (negative = wrong!)");
    }

    static List<String> sorted(List<T> in, Comparator<T> c) {
        List<T> copy = new ArrayList<>(in);
        copy.sort(c);
        List<String> names = new ArrayList<>();
        for (T t : copy) names.add(t.name());
        return names;
    }
}
