package com.hyman.zuul.filter;


import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.hyman.common.constant.CommonConstants;
import com.hyman.common.enums.TokenTypeEnum;
import com.hyman.common.exception.ServiceException;
import com.hyman.common.jwt.JWTHelper;
import com.hyman.common.log.MDCConstants;
import com.hyman.common.msg.auth.TokenErrorResponse;
import com.hyman.common.msg.auth.TokenForbiddenResponse;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.MDC;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hyman.common.constant.CommonConstants.*;
import static com.hyman.common.log.MDCConstants.REQUEST_ID_HEADER;

/**
 * @Author:
 * @Date: 2019年03月05日14:05:53
 * @Description:
 */
@Component
@Slf4j
public class AdminAccessFilter extends ZuulFilter {

    // 不校验token的路径
    private Set<String> ignorePathSet, ingoreOpenApis;

    @Value("${zuul.ignore.startWith:''}")
    private String ingorePaths;

    @Value("${zuul.ignore.swagger:''}")
    private String ingoreSwagger;

    @Value("${zuul.ignore.openInterface:''}")
    private String ingoreOpenInterface;

    @Value("${zuul.prefix}")
    private String zuulPrefix;

    @Value("${spring.cloud.config.profile}")
    private String env;
    
//    @Resource
//    private RedisUtil redisUtil;

    //小程序在换取token之前，会先访问/customer/v1/cust/selectCustList ，网关提示登录换取token
    public static final String FIRST_LOGIN_PAGE = "/customer/v1/cust/selectCustList";

    /**
     * 部分开放接口，简单秘钥比对
     */
    private static final String openInterface = "VO43UhpJf5yZEXMARoSNHVqnsVF6r7fT";

    @PostConstruct
    private void setIgnorePath() {
        ignorePathSet = Stream.concat(Splitter.on(',').splitToStream(ingorePaths), Splitter.on(',').splitToStream(ingoreSwagger)).map(String::trim).collect(Collectors.toSet());
        ingoreOpenApis = Splitter.on(',').splitToStream(ingoreOpenInterface).map(String::trim).collect(Collectors.toSet());
        ingorePaths = ingoreSwagger = ingoreOpenInterface = null; // help GC
    }


    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.DEBUG_FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        // 链路追踪使用
        String uuid = IdUtil.randomUUID();
        ctx.addZuulRequestHeader(REQUEST_ID_HEADER, uuid);
        MDC.put(MDCConstants.REQUEST_ID_MDC_KEY, uuid);

        String authToken = StringUtils.isBlank(request.getHeader(TOKEN_HEADER)) ? request.getParameter(TOKEN_HEADER) : request.getHeader(TOKEN_HEADER);
        //增加cookie
        if (null == authToken) {
            Cookie[] cookies = request.getCookies();
            if (null != cookies && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if (CommonConstants.TOKEN.equals(cookie.getName())) {
                        authToken = cookie.getValue();
                        break;
                    }
                }
            }
        }

        final String requestUri = request.getRequestURI().substring(zuulPrefix.length());
        log.info("requestUri的值是:{}", requestUri);

        ctx.addZuulRequestHeader("clientIp", request.getRemoteAddr());

        if (FIRST_LOGIN_PAGE.equals(requestUri) && StringUtils.isBlank(authToken)) {
            setFailedRequest(JSON.toJSONString(new TokenForbiddenResponse("please login!")), 200);
            return null;
        }

        // 验证token,放入header
        String tokenType = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(authToken)) {
            try {
                tokenType = decryptToken(authToken);
            } catch (Exception e) {
                setFailedRequest(JSON.toJSONString(new TokenErrorResponse(e.getMessage())), 200);
                return null;
            }
        }
        

        // 不进行拦截的地址
        if (StringUtils.equals(env, "d2") || isStartWith(requestUri)) {
            log.info("不进行拦截的地址：{}", requestUri);
            return null;
        }

        //开放接口简单秘钥判断
        if (openInterfaceJudge(requestUri)) {
            log.info("开放接口简单秘钥判断 ：{}", requestUri);
            return null;
        }

        if (StringUtils.isBlank(authToken)) {
            setFailedRequest(JSON.toJSONString(new TokenErrorResponse("无权限")), 200);
            return null;
        }
        
        judgingByType(tokenType, requestUri, authToken);
        //验证权限是否被修改
//        boolean isNormal = false;
//        try {
//            log.info("校验redis tken start...");
//            isNormal = roleStatusIsNormal(authToken);
//            log.info("校验redis tken result:{}",isNormal);
//        } catch (Exception e) {
//            setFailedRequest(JSON.toJSONString(new TokenErrorResponse(e.getMessage())), 200);
//            return null;
//        }
//        if(!isNormal){
//            setFailedRequest(JSON.toJSONString(new TokenErrorResponse(CommonResultConstant.AUTH_ERROR.getErrorMsg())), CommonResultConstant.AUTH_ERROR.getErrorCode());
//            return null;
//        }
        return null;
    }
    
//    private boolean roleStatusIsNormal(String authToken) {
//        //获取用户Id
//        Claims infoFromToken = JWTHelper.getInfoFromToken(authToken);
//        String userId = objectToStr(infoFromToken.get(JWT_KEY_USER_ID));// 注意： 用户id有负值的存在
//        String loginFlagRedisKey = String.format("%s%s", RedisKeyEnum.AUTH_USER_LOGIN_FLAG.getKey(), userId);
//        Object uuid = redisUtil.get(loginFlagRedisKey);
//        log.info("uid:",uuid);
//        return Objects.nonNull(uuid);
//    }
    
    /**
     * 解析token ,目前有如下几种方式
     * 1，小程序 只有手机号的方式，加密得到的token
     * 2, bss 系统，有用户名，密码，加密得到的 token
     * 3. application 登录
     * <p>
     * 这里解析token,判断用户是哪种方式登录进来的。
     *
     * @param authToken
     */
    private String decryptToken(String authToken) {

        Claims infoFromToken = JWTHelper.getInfoFromToken(authToken);
        String tokenType = setHeaderFromToken(infoFromToken);
        return tokenType;

    }

    /**
     * 解析token中的值，放入header中
     *
     * @param infoFromToken
     * @return
     */
    private String setHeaderFromToken(Claims infoFromToken) {
        RequestContext ctx = RequestContext.getCurrentContext();

        String JWT_KEY_PROFILE_TEMP = objectToStr(infoFromToken.get(JWT_KEY_PROFILE));

        String openId = objectToStr(infoFromToken.get(JWT_KEY_OPENID));
        String userId = objectToStr(infoFromToken.get(JWT_KEY_USER_ID));// 注意： 用户id有负值的存在
        String JWT_KEY_USERNAME_TEMP = objectToStr(infoFromToken.get(JWT_KEY_USERNAME));
        String JWT_KEY_PHONE_TEMP = objectToStr(infoFromToken.get(JWT_KEY_PHONE));
        String type = objectToStr(infoFromToken.get(JWT_KEY_TYPE));
        String busiPermission = String.valueOf(infoFromToken.getOrDefault(USER_BUSI_PERMISSION, StringUtils.EMPTY));
        String systemtype = objectToStr(infoFromToken.get(JWT_KEY_SYSTEMTYPE));

        log.info("profile:{},openid:{},userId:{},username:{},phone:{},type:{}",
                JWT_KEY_PROFILE_TEMP, openId, userId, JWT_KEY_USERNAME_TEMP, JWT_KEY_PHONE_TEMP, type);

        if (!StringUtils.equalsAny(env, "d2", JWT_KEY_PROFILE_TEMP)) {
            throw new ServiceException(200, "当前token不适用该环境");
        }

        ctx.addZuulRequestHeader(JWT_KEY_OPENID, openId);
        ctx.addZuulRequestHeader(JWT_KEY_USER_ID, userId);
        ctx.addZuulRequestHeader(JWT_KEY_TYPE, type);
        ctx.addZuulRequestHeader(JWT_KEY_PHONE, JWT_KEY_PHONE_TEMP);
        ctx.addZuulRequestHeader(JWT_KEY_USERNAME, JWT_KEY_USERNAME_TEMP);
        ctx.addZuulRequestHeader(USER_BUSI_PERMISSION, busiPermission);// 0:梧桐专车 1:梧桐共享
        ctx.addZuulRequestHeader(JWT_KEY_SYSTEMTYPE,systemtype);//系统标识 1:梧桐系统 3:雷鸟系统


        return type;
    }

    /**
     * 根据类型进行判断
     *
     * @param tokenType
     * @param requestUri
     * @param authToken
     */
    private void judgingByType(String tokenType, String requestUri, String authToken) {

        switch (TokenTypeEnum.getByValue(tokenType)) {
            case APP:
                break;
            case Applets:
                break;
            case BSS:
//                permissionCheck(requestUri, authToken);
                break;
            case WuTongApplets:
                break;
            default:
                break;
        }
    }

    /**
     * 权限校验
     */
    private void permissionCheck(String requestUri, String authToken) {
        // 从权限服务器中查询 token,进行权限校验
        Boolean success = true;

        if (!success) {
            setFailedRequest(JSON.toJSONString(new TokenErrorResponse("调用接口验证权限失败")), 200);

            log.warn("url=[{}]无权限，请核对", requestUri);
            setFailedRequest(JSON.toJSONString(new TokenErrorResponse("当前token权限验证失败，没有调用权限;Token Forbidden")), 200);
        }
    }

    /**
     * object转str
     *
     * @param obj
     * @return     
     */
    public static String objectToStr(@Nullable Object obj) {
        String str = io.jsonwebtoken.lang.Objects.getDisplayString(obj);
//        String str = Objects.toString(obj, StringUtils.EMPTY);
        return Strings.isBlank(str) ? "0" : str;
    }

    /**
     * URI是否以什么打头
     *
     * @param requestUri
     * @return
     */
    private boolean isStartWith(@NotBlank String requestUri) {
        return !CollectionUtils.isEmpty(ignorePathSet) && (ignorePathSet.contains(requestUri) || ignorePathSet.parallelStream().anyMatch(e -> StringUtils.startsWith(requestUri, e)));

//        boolean flag = false;
//
//        for (String s : ingorePaths.split(",")) {
//            if (requestUri.startsWith(s.trim())) {
//                return true;
//            }
//        }
//
//        for (String s : ingoreSwagger.split(",")) {
//            if (requestUri.startsWith(s.trim())) {
//                return true;
//            }
//        }
//
//        return flag;
    }

    /**
     * 开放接口简单秘钥判断
     *
     * @param requestUri
     * @return
     */
    private boolean openInterfaceJudge(@NotBlank String requestUri) {
        String firmiana = RequestContext.getCurrentContext().getRequest().getParameter("hyman");
        return StringUtils.equals(firmiana, openInterface) && !CollectionUtils.isEmpty(ingoreOpenApis) && (ingoreOpenApis.contains(requestUri) ||
                ingoreOpenApis.parallelStream().anyMatch(e -> StringUtils.startsWith(requestUri, e)));

//        boolean flag = false;
//
//        for (String s : ingoreOpenInterface.split(",")) {
//            if (requestUri.startsWith(s.trim())) {
//                String hyman = request.getParameter("hyman");
//                if (hyman != null && hyman.equals(openInterface)) {
//                    return true;
//                }
//            }
//        }
//        return flag;
    }

    /**
     * 网关抛异常
     *
     * @param body
     * @param code
     */
    private void setFailedRequest(String body, int code) {
        log.debug("Reporting error ({}): {}", code, body);
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseStatusCode(code);
        ctx.getResponse().setContentType("text/html;charset=UTF-8");
        if (Objects.isNull(ctx.getResponseBody())) {
            ctx.setResponseBody(body);
            ctx.setSendZuulResponse(false);
        }
    }

}
