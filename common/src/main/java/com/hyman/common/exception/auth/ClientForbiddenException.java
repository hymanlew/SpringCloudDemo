package com.hyman.common.exception.auth;


import com.hyman.common.constant.CommonConstants;
import com.hyman.common.exception.BaseException;

/**
 * @Author
 * @Date: 2018/3.4/28 16:19
 * @Description:
 */
public class ClientForbiddenException extends BaseException {
    public ClientForbiddenException(String message) {
        super(message, CommonConstants.EX_CLIENT_FORBIDDEN_CODE);
    }

}
