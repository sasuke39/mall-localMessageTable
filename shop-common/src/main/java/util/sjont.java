package util;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.jar.JarEntry;

/**
 * @author xianhong
 * @date 2021/12/30
 */
public class sjont {
    public static void main(String[] args) throws Exception {
        File file = new File("/Users/haochidebingqilinkaobulei/Library/Containers/com.tencent.WeWorkMac/Data/Documents/Profiles/1148AB9A23353FBF8E7D3B4009B714E3/Caches/Files/2021-12/39548f9102d9c596e3116a300b6e594b/新建文本文档.txt");
        InputStreamReader inputStream = new InputStreamReader(new FileInputStream(file));
        BufferedReader reader = new BufferedReader(inputStream);
        String s;
        StringBuilder sb =new StringBuilder();
        while ((s=reader.readLine())!=null){
            sb.append(s);
        }
//        System.out.println(sb.toString());
//        List<saa> saaList =new ArrayList<>();
//        saaList.add(saa.builder().age(10).dasda("zzzx").build());
//        saaList.add(saa.builder().age(10).dasda("zzzx").build());
//        saaList.add(saa.builder().age(10).dasda("zzzx").build());saaList.add(saa.builder().age(10).dasda("zzzx").build());
//
//        saaList.add(saa.builder().age(10).dasda("zzzx").build());
//        saaList.add(saa.builder().age(10).dasda("zzzx").build());
//        saaList.add(saa.builder().age(10).dasda("zzzx").build());
//        System.out.println(JSON.toJSONString(saaList));

        StringBuilder sbs = new StringBuilder();
        sbs.append("[");

        String toString = sb.toString();
        Stack<Integer> stack = new Stack<>();
        int length = toString.length();
        for (int i = 0; i < length; i++) {
            if (toString.charAt(i) =='{') {
                stack.push(i);
            }else if (toString.charAt(i)=='}'){
                Integer pop = stack.pop();
                    String substring = toString.substring(pop, i+1);
                    sbs.append(substring);
                    if (i!=length-1){
                        sbs.append(",");
                    }
            }
        }
        sbs.append("]");
        System.out.println(sbs.toString());


    }
    @Data
    @Builder
    @AllArgsConstructor
    public static class saa{
        Integer age;
        String dasda;
    }
}
