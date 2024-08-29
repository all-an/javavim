public class Javavim {
    static {
        System.loadLibrary("javavim");
    }

    public native void sayHello();

    public static void main(String[] args) {

        new Javavim().sayHello(); //
    }
}