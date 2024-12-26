package com.xiaohao.hojbackendserviceclient.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hoj-backend-judge-service",path="/api/judge/inner")
public interface JudgeFeignClient {
    @PostMapping("/doJudge")
    void doJudge(@RequestParam("questionSubmitId") Long questionSubmitId);
}
