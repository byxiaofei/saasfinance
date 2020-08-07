package com.sinosoft.util;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommonUtil {
    /**
     * //转数字 相加后在转成 字符串
     * @param d2
     * @param d21
     * @return
     */
    public static  String numAdd(String d2, String d21) {
        String str ="";
        if (d2!=null && !d2 .equals("")){
            BigDecimal decimal= new BigDecimal(d2);
            BigDecimal decima2 = new BigDecimal(d21);
            decimal = decimal.add(decima2);
            str =decimal.toString();
        }
        return str;
    }

    /**
     * list 集合的深拷贝
     * @param src
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    /**
     * 对象是否为空
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        }
        if (o instanceof String) {
            if (o.toString().trim().equals("")) {
                return true;
            }
        } else if (o instanceof List) {
            if (((List) o).size() == 0) {
                return true;
            }
        } else if (o instanceof Map) {
            if (((Map) o).size() == 0) {
                return true;
            }
        } else if (o instanceof Set) {
            if (((Set) o).size() == 0) {
                return true;
            }
        } else if (o instanceof Object[]) {
            if (((Object[]) o).length == 0) {
                return true;
            }
        } else if (o instanceof int[]) {
            if (((int[]) o).length == 0) {
                return true;
            }
        } else if (o instanceof long[]) {
            if (((long[]) o).length == 0) {
                return true;
            }
        }
        return false;
    }

}
