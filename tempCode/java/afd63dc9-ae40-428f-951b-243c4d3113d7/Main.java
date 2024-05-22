import java.util.Scanner;
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt();
        int b = sc.nextInt();
        Thread.sleep(5000);
        System.out.println(a + b);
    }
}
