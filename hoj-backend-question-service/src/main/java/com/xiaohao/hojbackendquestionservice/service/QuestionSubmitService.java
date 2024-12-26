package com.xiaohao.hojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaohao.hojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.xiaohao.hojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.xiaohao.hojbackendmodel.model.entity.QuestionSubmit;
import com.xiaohao.hojbackendmodel.model.entity.User;
import com.xiaohao.hojbackendmodel.model.vo.QuestionSubmitVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author pc
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-12-04 22:17:37
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 点赞
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);


    /**
     * 分页获取问题封装
     *
     * @param questionSubmitPage
     * @param request
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request);

}
