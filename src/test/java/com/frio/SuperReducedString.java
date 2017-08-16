package com.frio;

import java.util.Scanner;
import java.util.Stack;

/**
 * Created by frio on 17/6/22.
 */
public class SuperReducedString {
    public static String super_reduced_string(String s){
        Stack<Character> stack = new Stack<>();
        char[] array = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < array.length; i ++){
            switch (stack.size()){
                case 0:
                    stack.add(array[i]);
                    if(i == array.length - 1){
                        sb.append(array[i]);
                    }
                    break;
                case 1:
                    if(stack.peek().charValue() == array[i]){
                        stack.pop();
                    }else {
                        sb.append(stack.pop());
                        stack.add(array[i]);
                        if (i == array.length - 1) {
                            sb.append(array[i]);
                        }
                    }
                    break;
                default:
                    throw new RuntimeException();
            }
        }
        if(isHaveBrother(sb.toString())){
            return super_reduced_string(sb.toString());
        }else{
            return sb.length() == 0 ? "Empty String" : sb.toString();
        }
    }

    public static boolean isHaveBrother(String s){
        char[] array = s.toCharArray();
        for(int i = 0; i < array.length; i++){
            if(i > 0){
                if(array[i-1] == array[i]){
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String s = in.next();
        String result = super_reduced_string(s);
        System.out.println(result);
    }
}
