package com.jt28.a6735.jmreceipt;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by a6735 on 2017/7/20.
 */

public class IOUtil {
    public static String readLine(InputStream source) throws IOException {
        StringBuffer sb = new StringBuffer();
        int ch;
        while((ch = source.read()) != -1) {
            if (ch == '\n')
                return sb.toString();
            sb.append((char) ch);
        }
        if (ch == -1 && sb.toString().length() < 1)
            return null;
        return sb.toString();
    }
}
