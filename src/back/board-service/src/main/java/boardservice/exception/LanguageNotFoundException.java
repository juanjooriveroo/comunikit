package boardservice.exception;

/**
 * Excepción lanzada cuando un idioma no es encontrado
 */
public class LanguageNotFoundException extends RuntimeException {
    public LanguageNotFoundException(String message) {super(message);}
}
