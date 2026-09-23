package br.com.ubisafe.notification.presentation.error;

import br.com.ubisafe.notification.application.channel.exception.ChannelNotFoundException;
import br.com.ubisafe.notification.application.port.AlertPublicationException;
import br.com.ubisafe.notification.domain.channel.exception.ChannelAlreadyExistsException;
import br.com.ubisafe.notification.domain.channel.exception.ChannelInactiveException;
import br.com.ubisafe.notification.domain.channel.exception.MissingTemplateParametersException;
import br.com.ubisafe.notification.domain.shared.exception.DomainValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ChannelNotFoundException.class)
    ProblemDetail handleNotFound(ChannelNotFoundException exception) {
        return ApiProblem.of(HttpStatus.NOT_FOUND, "channel-not-found", "Canal não encontrado", exception.getMessage());
    }

    @ExceptionHandler(ChannelAlreadyExistsException.class)
    ProblemDetail handleAlreadyExists(ChannelAlreadyExistsException exception) {
        return ApiProblem.of(HttpStatus.CONFLICT, "channel-already-exists", "Canal duplicado", exception.getMessage());
    }

    @ExceptionHandler(ChannelInactiveException.class)
    ProblemDetail handleInactive(ChannelInactiveException exception) {
        return ApiProblem.of(HttpStatus.UNPROCESSABLE_ENTITY, "channel-inactive", "Canal inativo", exception.getMessage());
    }

    @ExceptionHandler(MissingTemplateParametersException.class)
    ProblemDetail handleMissingParameters(MissingTemplateParametersException exception) {
        ProblemDetail problem = ApiProblem.of(HttpStatus.UNPROCESSABLE_ENTITY, "missing-template-params",
                "Parâmetros do template ausentes", exception.getMessage());
        problem.setProperty("missingParams", exception.missingParameters());
        return problem;
    }

    @ExceptionHandler(DomainValidationException.class)
    ProblemDetail handleDomainValidation(DomainValidationException exception) {
        return ApiProblem.of(HttpStatus.BAD_REQUEST, "validation-error", "Requisição inválida", exception.getMessage());
    }

    @ExceptionHandler(AlertPublicationException.class)
    ProblemDetail handlePublication(AlertPublicationException exception) {
        log.error("Falha na publicação do alerta", exception);
        return ApiProblem.of(HttpStatus.SERVICE_UNAVAILABLE, "alert-publication-unavailable",
                "Serviço de mensageria indisponível", exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception exception) {
        log.error("Erro inesperado", exception);
        return ApiProblem.of(HttpStatus.INTERNAL_SERVER_ERROR, "internal-error", "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException exception,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        List<FieldViolation> violations = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldViolation(error.getField(), error.getDefaultMessage()))
                .toList();
        ProblemDetail problem = ApiProblem.of(HttpStatus.BAD_REQUEST, "validation-error", "Requisição inválida",
                "Um ou mais campos são inválidos");
        problem.setProperty("errors", violations);
        return ResponseEntity.badRequest().body(problem);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException exception,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        ProblemDetail problem = ApiProblem.of(HttpStatus.BAD_REQUEST, "malformed-request", "Requisição inválida",
                "O corpo da requisição está ausente, malformado ou contém valores não suportados");
        return ResponseEntity.badRequest().body(problem);
    }
}
