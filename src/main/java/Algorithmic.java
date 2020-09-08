import java.util.Scanner;

public class Algorithmic {
    private static Scanner scan = new Scanner(System.in);

    public static Double inputReal(String prompt)
    {
        System.out.printf("%s: ", prompt);
        return scan.nextDouble();
    }

    public static String inputText(String prompt)
    {
        System.out.printf("%s: ", prompt);
        var value = scan.nextLine();
        while( value.isEmpty() )
            value = scan.nextLine();
        return value;
    }

    public static void printReal(double value)
    {
        System.out.println(value);
    }

    public static void printText(String value)
    {
        System.out.println(value);
    }

    public static boolean eq(String x, String y)
    {
        return x.equals(y);
    }

    public static boolean ne(String x, String y)
    {
        return !eq(x,y);
    }
}
