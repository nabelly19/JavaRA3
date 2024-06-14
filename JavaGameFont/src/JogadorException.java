public class JogadorException extends Exception {
    public JogadorException(){
        super();
    }

    public JogadorException(String message){
        super(message);
    }

    public JogadorException(String message, Throwable cause){
        super(message, cause);
    }
}
