package com.xiaohao.hojbackendserviceclient.feignClient;

import com.xiaohao.hojbackendmodel.model.entity.Question;
import com.xiaohao.hojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hoj-backend-question-service", path = "/api/question/inner")
public interface QuestionFeignClient {
    @GetMapping("/question")
    Question getById(@RequestParam("questionId") Long questionId);

    @GetMapping("/questionSubmit")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") Long questionSubmitId);

    @PostMapping("/questionSubmit")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmitUpdate);
}
