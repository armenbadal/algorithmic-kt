import java.util.Scanner;

public class Algorithmic {
    private static Scanner scan = new Scanner(System.in);

    public static boolean inputBoolean(String prompt)
    {
        var value = inputText(prompt);
        if( value.equals("ՃԻՇՏ") )
            return true;

        if( value.equals("ԿԵՂԾ") )
            return false;

        System.err.println("«" + value + "»-ը ԲՈՒԼՅԱՆ արժեք չէ։");
        return inputBoolean(prompt);
    }

    public static double inputReal(String prompt)
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

    public static void printBoolean(boolean value)
    {
        System.out.println(value ? "ՃԻՇՏ" : "ԿԵՂԾ");
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
