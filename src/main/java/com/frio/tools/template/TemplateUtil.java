package com.frio.tools.template;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Map;

/**
 * Created by frio on 17/5/31.
 */
public class TemplateUtil {
    /**
     * replace template variables to get str
     */
    public String replaceTemplateVar(String template, Map<String, String> map, String left, String right){
        StrSubstitutor sub = new StrSubstitutor(map, left, right);
        return sub.replace(template);
    }

    /**
     * replace template with default '${}'
     */
    public String replaceTemplateVarDefault(String template, Map<String, String> map){
        StrSubstitutor sub = new StrSubstitutor(map, "${", "}");
        return sub.replace(template);
    }
}
