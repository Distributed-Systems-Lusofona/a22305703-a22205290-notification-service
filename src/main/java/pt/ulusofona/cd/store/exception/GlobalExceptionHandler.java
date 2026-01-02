package pt.ulusofona.cd.store.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private String traceId(HttpServletRequest req) {
        String header = req.getHeader("X-Trace-Id");
        return (header != null && !header.isBlank()) ? header : UUID.randomUUID().toString();
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String errorCode, String message, String traceId) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", errorCode);
        body.put("message", message);
        body.put("traceId", traceId);
        return ResponseEntity.status(status).body(body);
    }
//aqui temos a tal lista de erros que estava mencionaada noeenunciado
    // erro 404
    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotificationNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), traceId(req));
    }

    // 422 (validação)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(IllegalArgumentException ex, HttpServletRequest req) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR", ex.getMessage(), traceId(req));
    }

    // 409 (conflitos: duplicados/idempotência/estado inválido)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(IllegalStateException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage(), traceId(req));
    }

    // 400 (JSON malformado)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleBadJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Invalid request body", traceId(req));
    }

    // 500
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", ex.getMessage(), traceId(req));
    }
}
