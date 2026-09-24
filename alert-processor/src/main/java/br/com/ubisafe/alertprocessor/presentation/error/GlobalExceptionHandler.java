package br.com.ubisafe.alertprocessor.presentation.error;

import br.com.ubisafe.alertprocessor.application.alert.exception.AlertNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AlertNotFoundException.class)
    ProblemDetail handleNotFound(AlertNotFoundException exception) {
        return ApiProblem.of(HttpStatus.NOT_FOUND, "alert-not-found", "Alerta não encontrado", exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception exception) {
        log.error("Erro inesperado", exception);
        return ApiProblem.of(HttpStatus.INTERNAL_SERVER_ERROR, "internal-error", "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.");
    }
}
