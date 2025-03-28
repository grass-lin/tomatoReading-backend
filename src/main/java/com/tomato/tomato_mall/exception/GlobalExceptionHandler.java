package com.tomato.tomato_mall.exception;

import com.tomato.tomato_mall.vo.ResponseVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 全局异常处理器
 * <p>
 * 该类负责捕获并处理整个应用程序中抛出的异常，将其转换为标准化的HTTP响应。
 * 通过Spring的{@link RestControllerAdvice}注解，实现了集中式的异常处理机制，
 * 确保所有REST接口返回一致的错误格式，提高了API的可用性和可维护性。
 * </p>
 * <p>
 * 处理的异常类型包括：
 * - 请求参数验证异常
 * - 资源不存在异常
 * - 认证凭据异常
 * - 用户名冲突异常
 * - 参数格式异常
 * - 权限不足异常
 * - 其他未预期的系统异常
 * </p>
 * <p>
 * 所有响应都使用统一的{@link ResponseVO}格式，包含状态码、错误消息和相关数据。
 * </p>
 *
 * @author Team CBDDL
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理请求参数验证失败异常
     * <p>
     * 当请求体中的数据未通过{@code @Valid}或{@code @Validated}注解的校验规则时，
     * 捕获{@link MethodArgumentNotValidException}异常并将其转换为包含详细字段错误信息的响应。
     * </p>
     *
     * @param ex 参数验证异常对象，包含所有验证失败的字段和错误信息
     * @return 包含HTTP 400状态码和错误详情映射的响应实体
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseVO<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseVO.error(400, "Validation failed", errors));
    }

    /**
     * 处理资源不存在异常
     * <p>
     * 当请求的资源（如用户、商品等）不存在时，捕获{@link NoSuchElementException}异常
     * 并返回HTTP 404错误响应。
     * </p>
     *
     * @param ex 资源不存在异常对象
     * @return 包含HTTP 404状态码和错误信息的响应实体
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseVO<Void>> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseVO.error(404, ex.getMessage()));
    }

    /**
     * 处理认证凭据错误异常
     * <p>
     * 当用户提供的认证信息（如用户名和密码）不正确时，捕获{@link BadCredentialsException}异常
     * 并返回HTTP 401未授权错误响应。
     * </p>
     *
     * @param ex 认证凭据异常对象
     * @return 包含HTTP 401状态码和统一错误提示的响应实体
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseVO<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        // Bad practice
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseVO.error(400, ex.getMessage()));
        // return ResponseEntity
        // .status(HttpStatus.UNAUTHORIZED)
        // .body(ResponseVO.error(401, ex.getMessage()));
    }

    /**
     * 处理用户名已存在异常
     * <p>
     * 当尝试创建的用户名已被占用时，捕获{@link UsernameAlreadyExistsException}异常
     * 并返回HTTP 409冲突错误响应。
     * </p>
     *
     * @param ex 用户名已存在异常对象
     * @return 包含HTTP 409状态码和具体冲突信息的响应实体
     */
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ResponseVO<Void>> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResponseVO.error(409, ex.getMessage()));
    }

    /**
     * 处理参数格式错误异常
     * <p>
     * -当请求参数格式不正确或违反业务规则时，捕获{@link IllegalArgumentException}异常
     * 并返回HTTP 400错误响应。
     * </p>
     *
     * @param ex 参数格式错误异常对象
     * @return 包含HTTP 400状态码和参数错误详情的响应实体
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseVO<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseVO.error(400, ex.getMessage()));
    }

    /**
     * 处理权限不足异常
     * <p>
     * 当已认证的用户尝试访问无权限的资源或方法时，捕获{@link AccessDeniedException}异常
     * 并返回HTTP 403禁止访问错误响应。这通常由方法级安全注解(如@PreAuthorize)触发。
     * </p>
     *
     * @param ex 权限不足异常对象
     * @return 包含HTTP 403状态码和权限错误信息的响应实体
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseVO<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ResponseVO.error(403, "Forbidden: Access Denied"));
    }

    /**
     * 处理请求路径不存在异常
     * <p>
     * 当客户端请求的URL路径没有对应的处理器时，捕获{@link NoHandlerFoundException}异常
     * 并返回HTTP 404错误响应，表示请求的资源不存在。
     * </p>
     *
     * @param ex 请求路径不存在异常对象
     * @return 包含HTTP 404状态码和路径错误信息的响应实体
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ResponseVO<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseVO.error(404, "The requested path '" + ex.getRequestURL() + "' does not exist"));
    }

    /**
     * 处理静态资源不存在异常
     * <p>
     * 当客户端请求的静态资源文件不存在时，捕获{@link NoResourceFoundException}异常
     * 并返回HTTP 404错误响应，表示请求的静态资源不存在。
     * </p>
     *
     * @param ex 静态资源不存在异常对象
     * @return 包含HTTP 404状态码和资源错误信息的响应实体
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseVO<Void>> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseVO.error(404, "The requested resource '" + ex.getResourcePath() + "' does not exist"));
    }

    /**
     * 处理所有未明确捕获的其他异常
     * <p>
     * 作为最后的防线，捕获所有其他未预期的异常，确保API不会返回原始的错误堆栈信息。
     * 将这些异常转换为通用的HTTP 500内部服务器错误响应。
     * </p>
     * <p>
     * 注意：生产环境中应当记录这些未预期的异常以便诊断问题。
     * </p>
     *
     * @param ex 未预期的异常对象
     * @return 包含HTTP 500状态码和通用错误信息的响应实体
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseVO<Void>> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseVO.error(500, "Internal server error"));
    }
}