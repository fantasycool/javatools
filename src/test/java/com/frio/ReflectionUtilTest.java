package com.frio;

import com.alibaba.fastjson.JSON;
import com.frio.tools.reflect.ReflectionUtil;
import org.junit.Test;

import java.util.*;

/**
 * Created by frio on 17/2/27.
 */
public class ReflectionUtilTest {
    @Test
    public void testMapToBean(){
        Map<String, Object> parent = new HashMap<>();
        parent.put("name", "parent_Name");
        parent.put("gmt_create", System.currentTimeMillis());
        parent.put("longInteger", 11111l);
        parent.put("doubleType", 32.2);
        List<Map<String, Object>> list = new ArrayList<>();
        parent.put("childsList", list);
        Map<String, Object> child = new HashMap<>();
        child.put("name", "childName1");
        child.put("count_action", "count_action1");
        list.add(child);
        child = new HashMap<>();
        child.put("name", "childName1");
        child.put("count_action", "count_action1");
        list.add(child);
        Parent parentBean = new Parent();
        Map<String, Class> description = new HashMap<>();
        description.put("childsList", Child.class);
        ReflectionUtil.cloneMapValueToBean(parent, parentBean, description);

        System.out.println(JSON.toJSONString(parentBean));
    }

    public static class Parent {
        private String name;
        private Date gmtCreate;
        private Long longInteger;
        private Double doubleType;
        private List<Child> childsList;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getGmtCreate() {
            return gmtCreate;
        }

        public void setGmtCreate(Date gmtCreate) {
            this.gmtCreate = gmtCreate;
        }

        public Long getLongInteger() {
            return longInteger;
        }

        public void setLongInteger(Long longInteger) {
            this.longInteger = longInteger;
        }

        public Double getDoubleType() {
            return doubleType;
        }

        public void setDoubleType(Double doubleType) {
            this.doubleType = doubleType;
        }

        public List<Child> getChildsList() {
            return childsList;
        }

        public void setChildsList(List<Child> childsList) {
            this.childsList = childsList;
        }
    }
    public static class Child {
        private String countAction;
        private String name;

        public String getCountAction() {
            return countAction;
        }

        public void setCountAction(String countAction) {
            this.countAction = countAction;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
