package com.xjh.controller;

import com.xjh.DAG.DAG;
import com.xjh.lexer.LexAnalyse;
import com.xjh.lexer.pojo.ErrorWord;
import com.xjh.lexer.pojo.LexerResult;
import com.xjh.parser.Parser;
import com.xjh.parser.pojo.IntermediateCode;
import com.xjh.targetCode.Assembler;
import com.xjh.targetCode.GetTargetCode;
import com.xjh.targetCode.pojo.TargetCode;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xjhxjhxjh
 * @date 2019/12/17 9:52
 */
@RestController
public class Controller {

    @RequestMapping("/run")
    public Map run(ModelAndView modelAndView, String code) {
        // 完成词法分析
        Map<String, List> map = LexAnalyse.doLexer(code);
        List<ErrorWord> errorList = map.get("errorList");
        HashMap<String, Object> returnMap = new HashMap<>();
        for (ErrorWord errorWord : errorList) {
            returnMap.put("result", errorWord);
            returnMap.put("msg", 0);
            return returnMap;
        }
        List<LexerResult> resultList = map.get("resultList");
        // 语法分析，中间代码生成
        Map parserMap = Parser.doParser(new ArrayList<>(resultList));
        List<IntermediateCode> intermediateCodesList = (List) parserMap.get("intermediateCodesList");
        intermediateCodesList.forEach(System.out::println);
        List parserErrorList = (List) parserMap.get("errorList");
        String info = (String) parserMap.get("info");
        // DAG优化
        List<IntermediateCode> intermediateCodes = DAG.doDAG(intermediateCodesList);
        List<TargetCode> targetCodes = GetTargetCode.doTargetCode(intermediateCodesList);
        List<TargetCode> assembler = Assembler.doAssembler(intermediateCodesList);
        returnMap.put("code", 0);
        returnMap.put("resultList", resultList);
        returnMap.put("targetCodes", targetCodes);
        returnMap.put("errorList", errorList);
        returnMap.put("intermediateCodesList", intermediateCodesList);
        returnMap.put("info", info);
        returnMap.put("parserErrorList", parserErrorList);
        returnMap.put("assembler", assembler);
        assembler.forEach(System.out::println);
        return returnMap;
    }


    @RequestMapping("upload")
    public Map<String, Object> upload(ModelAndView modelAndView, MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);
        try {
            String code = IOUtils.toString(file.getInputStream());
            map.put("cCode", code);
        } catch (IOException e) {
            map.put("code", 1);
        }
        return map;
    }
}
