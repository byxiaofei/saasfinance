package com.sinosoft.common;

import com.sinosoft.util.Config;

import java.util.List;

/**
 * 系统常量
 *
 */
public class Constant {

    public static final String MODELPATH= Config.getProperty("MODELPath");

    public static List<String> urlList = null;

    /**
     * 决算月
     */
    public static final String MONTH_14 = "14";

    /**
     * 是
     */
    public static final String YES = "Y";

    /**
     * 否
     */
    public static final String NO = "N";


    /**
     * 会计月度状态 当前
     */
    public static final String ACC_MONTH_STAT_PRESENT = "1";

    /**
     * 会计月度状态 未结转
     */
    public static final String ACC_MONTH_STAT_BEFORE_CARRIED_FORWARD = "2";

    /**
     * 会计月度状态 已结转
     */
    public static final String ACC_MONTH_STAT_AFTER_CARRIED_FORWARD = "3";


    /**
     * 凭证状态 未复核
     */
    public static final String VOUCHER_FLAG_BEFORE_REVIEW = "1";

    /**
     * 凭证状态 已复核
     */
    public static final String VOUCHER_FLAG_AFTER_REVIEW= "2";

    /**
     * 凭证状态 已记账
     */
    public static final String VOUCHER_FLAG_AFTER_POSTING = "3";

    /**
     * 凭证状态 未决算
     */
    public static final String VOUCHER_FLAG_UNBALANCED = "4";

    /**
     * 凭证状态 已决算
     */
    public static final String VOUCHER_FLAG_AFTER_FINAL_ACCOUNTS = "5";


    /**
     * 凭证类型 决算凭证
     */
    public static final String VOUCHER_TYPE_FINAL = "1";
    /**
     * 2-记账凭证 3-固资自转 4-无资自转 5-费控接口凭证 6-基金收缴接口凭证  //TODO
     */

    /**
     * 录入方式（1-自动 2-手工）//TODO
     */

}