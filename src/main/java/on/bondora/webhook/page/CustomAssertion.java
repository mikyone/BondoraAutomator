package on.bondora.webhook.page;

public class CustomAssertion {
    public static void customAssertTrue(boolean assertion, String msg) {
        if(!assertion){
            throw new IllegalArgumentException(msg);
        }

    }

    public static void customFail(String msg) {
        throw new IllegalArgumentException(msg);
    }
}
