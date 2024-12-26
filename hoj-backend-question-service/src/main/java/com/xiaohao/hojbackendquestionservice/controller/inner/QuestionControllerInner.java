package com.xiaohao.hojbackendquestionservice.controller.inner;

import com.xiaohao.hojbackendmodel.model.entity.Question;
import com.xiaohao.hojbackendmodel.model.entity.QuestionSubmit;
import com.xiaohao.hojbackendquestionservice.service.QuestionService;
import com.xiaohao.hojbackendquestionservice.service.QuestionSubmitService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RequestMapping("inner")
@RestController
public class QuestionControllerInner {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @GetMapping("/question")
    public Question getById(Long questionId) {
        return questionService.getById(questionId);
    }

    @GetMapping("/questionSubmit")
    public QuestionSubmit getQuestionSubmitById(Long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    @PostMapping("/questionSubmit")
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmitUpdate) {
        return questionSubmitService.updateById(questionSubmitUpdate);
    }

}
