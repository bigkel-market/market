package com.itchenyang.market.product.handler;

import com.itchenyang.common.utils.R;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.itchenyang.market.product.controller")
public class GlobalExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handlerException(MethodArgumentNotValidException e) {
        Map<String, String> resMap = new HashMap<>();

        BindingResult result = e.getBindingResult();
        result.getFieldErrors().forEach(item -> {
            resMap.put(item.getField(), item.getDefaultMessage());
        });
        return R.error().put("data", resMap);
    }
}
