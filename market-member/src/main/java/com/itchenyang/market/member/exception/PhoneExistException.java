package com.itchenyang.market.member.exception;

/**
 * @author BigKel
 * @createTime 2022/11/7
 */
public class PhoneExistException extends RuntimeException{

    public PhoneExistException() {
        super("手机号存在");
    }
}
