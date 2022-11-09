package com.itchenyang.market.member.exception;

/**
 * @author BigKel
 * @createTime 2022/11/7
 */
public class UserNameExistException extends RuntimeException{

    public UserNameExistException() {
        super("用户名存在");
    }
}
