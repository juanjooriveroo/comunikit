package boardservice.exception;

/**
 * Excepción lanzada cuando un recurso (imagen, pictograma) no es encontrado
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {super(message);}
}
