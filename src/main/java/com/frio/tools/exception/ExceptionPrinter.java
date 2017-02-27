package com.frio.tools.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by frio on 17/2/22.
 */
public class ExceptionPrinter {

    /**
     * get exception stack format string
     * @param t
     * @return
     */
    public static String getExceptionMessage(Throwable t){
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        t.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
