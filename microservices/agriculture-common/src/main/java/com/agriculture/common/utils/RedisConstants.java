package com.agriculture.common.utils;

public class RedisConstants {
    // 短信验证码相关
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 5L;
    
    // 用户会话管理
    public static final String LOGIN_USER_KEY = "login:user:";
    public static final Long LOGIN_USER_TTL = 36000L; // 10小时
    
    // Token黑名单
    public static final String LOGIN_TOKEN_BLACKLIST_KEY = "login:token:blacklist:";
    public static final Long LOGIN_TOKEN_BLACKLIST_TTL = 36000L; // 10小时
    
    // 登录失败次数限制
    public static final String LOGIN_FAIL_COUNT_KEY = "login:fail:count:";
    public static final Long LOGIN_FAIL_COUNT_TTL = 15L; // 15分钟
    public static final Integer MAX_LOGIN_FAIL_COUNT = 5; // 最大失败次数
    
    // 账户锁定
    public static final String LOGIN_LOCK_KEY = "login:lock:";
    public static final Long LOGIN_LOCK_TTL = 30L; // 锁定30分钟
    
    // 在线用户统计
    public static final String ONLINE_USER_KEY = "online:user:";
    public static final String ONLINE_USER_COUNT_KEY = "online:user:count";
    public static final Long ONLINE_USER_TTL = 30L; // 30分钟无操作自动下线
    
    // 用户信息缓存
    public static final String USER_INFO_KEY = "user:info:";
    public static final Long USER_INFO_TTL = 60L; // 1小时
    
    // 通用缓存
    public static final Long CACHE_NULL_TTL = 2L;
    public static final Long CACHE_SHOP_TTL = 30L;
    public static final String CACHE_SHOP_KEY = "cache:shop:";
    public static final String LOCK_SHOP_KEY = "lock:shop:";
    public static final Long LOCK_SHOP_TTL = 10L;
    
    // 其他业务
    public static final String SECKILL_STOCK_KEY = "seckill:stock:";
    public static final String BLOG_LIKED_KEY = "blog:liked:";
    public static final String FEED_KEY = "feed:";
    public static final String SHOP_GEO_KEY = "shop:geo:";
    public static final String USER_SIGN_KEY = "sign:";
}
