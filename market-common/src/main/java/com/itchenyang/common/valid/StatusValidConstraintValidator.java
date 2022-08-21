package com.itchenyang.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StatusValidConstraintValidator implements ConstraintValidator<StatusValid, Integer> {

    private Set<Integer> set = new HashSet<>();

    @Override
    public void initialize(StatusValid constraintAnnotation) {
        int[] values = constraintAnnotation.values();
        Arrays.stream(values).forEach(item -> set.add(item));
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(integer);
    }
}
