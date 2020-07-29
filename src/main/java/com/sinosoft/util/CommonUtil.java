package com.sinosoft.util;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;

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

}
