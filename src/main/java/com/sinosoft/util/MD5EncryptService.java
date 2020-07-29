package com.sinosoft.util;

import org.apache.shiro.crypto.hash.Md5Hash;

/**
 * Shiro 默认是16进制加密
 *
 * @author zh
 */
public class MD5EncryptService implements EncryptService {

    private int hashIterations = 1;

    private boolean saltDisabled = false;

    @Override
    public String encryptPassword(String password, String salt) throws IllegalArgumentException {
        Assert.notBlank(password, "密码不能为空");
        if (saltDisabled) {
            salt = null;
        }
        return new Md5Hash(password, salt, hashIterations).toHex();
    }

    @Override
    public String getCredentialsStrategy() {
        return Md5Hash.ALGORITHM_NAME;
    }

    @Override
    public int getHashIterations() {
        return hashIterations;
    }

    @Override
    public boolean saltDisabled() {
        return saltDisabled;
    }

    /*-------------- provide setter methods  ------------------*/

    public void setSaltDisabled(boolean disabled) {
        this.saltDisabled = disabled;
    }

    public void setHashIterations(int hashIterations) {
        this.hashIterations = hashIterations;
    }
}
