package com.sinosoft.common;

/**
 * key命名方式为：模块名_用途名_PREFIX
 * value命名方式为：系统名:模块名:用途名:
 * @author hotcosmos
 */
public class RedisConstant {

    /**
     * 系统设置-科目全量信息(key前缀)
     */
    public static final String SYSTEM_SUBJECT_ALL_KEY_PREFIX = "ifs:system:subjectAllKey:";

    /**
     * 系统设置-专项信息(key前缀)
     * 凭证录入 专项根据一级查询
     */
    public static final String SYSTEM_SPECIAL_INFO_KEY_PREFIX = "ifs:system:specialInfoKey:";


}
