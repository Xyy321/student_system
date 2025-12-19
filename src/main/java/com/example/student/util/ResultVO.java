package com.example.student.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultVO<T> {

    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public static <T> ResultVO<T> success() {
        return ResultVO.<T>builder()
                .code(200)
                .message("操作成功")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> ResultVO<T> success(T data) {
        return ResultVO.<T>builder()
                .code(200)
                .message("操作成功")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> ResultVO<T> success(String message, T data) {
        return ResultVO.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> ResultVO<T> error(String message) {
        return ResultVO.<T>builder()
                .code(500)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> ResultVO<T> error(Integer code, String message) {
        return ResultVO.<T>builder()
                .code(code)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> ResultVO<T> unauthorized(String message) {
        return ResultVO.<T>builder()
                .code(401)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> ResultVO<T> forbidden(String message) {
        return ResultVO.<T>builder()
                .code(403)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> ResultVO<T> notFound(String message) {
        return ResultVO.<T>builder()
                .code(404)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
