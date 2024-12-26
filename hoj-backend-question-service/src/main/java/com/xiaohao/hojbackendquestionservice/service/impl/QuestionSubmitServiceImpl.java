package com.xiaohao.hojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaohao.hojbackendcommon.common.ErrorCode;
import com.xiaohao.hojbackendcommon.constant.CodeRabbitmqConstant;
import com.xiaohao.hojbackendcommon.constant.CommonConstant;
import com.xiaohao.hojbackendcommon.exception.BusinessException;
import com.xiaohao.hojbackendcommon.utils.SqlUtils;
import com.xiaohao.hojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.xiaohao.hojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.xiaohao.hojbackendmodel.model.entity.Question;
import com.xiaohao.hojbackendmodel.model.entity.QuestionSubmit;
import com.xiaohao.hojbackendmodel.model.entity.User;
import com.xiaohao.hojbackendmodel.model.enums.QuestionSubmitLanguageEnum;
import com.xiaohao.hojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.xiaohao.hojbackendmodel.model.vo.QuestionSubmitVO;
import com.xiaohao.hojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.xiaohao.hojbackendquestionservice.rabbitmq.MessageProducer;
import com.xiaohao.hojbackendquestionservice.service.QuestionService;
import com.xiaohao.hojbackendquestionservice.service.QuestionSubmitService;
import com.xiaohao.hojbackendserviceclient.feignClient.JudgeFeignClient;
import com.xiaohao.hojbackendserviceclient.feignClient.UserFeignClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author pc
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2024-12-04 22:17:37
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private MessageProducer messageProducer;

    /**
     * 点赞
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        //  校验编程语言是否合理
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum enumByValue = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (enumByValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言不合法");
        }
        // 判断实体是否存在，根据类别获取实体
        long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setUserId(userId);
        questionSubmit.setLanguage(questionSubmitAddRequest.getLanguage());
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        //  设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");

        boolean save = this.save(questionSubmit);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        Long questionSubmitId = questionSubmit.getId();
        // 执行判题服务
        // CompletableFuture.runAsync(() -> {
        //     judgeFeignClient.doJudge(questionSubmitId);
        // });
        // todo 这里转变为向rabbitmq中发送消息,并且不再需要异步执行
        messageProducer.sendMessage(CodeRabbitmqConstant.CODE_EXCHANGE, CodeRabbitmqConstant.CODE_ROUTING_KEY,
                String.valueOf(questionSubmitId));
        return questionSubmitId;
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前段传来的请求对象，得到mybatis支持的查询QueryWrapper类）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long userId = questionSubmitQueryRequest.getUserId();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);


        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionSubmitList.stream().map(QuestionSubmit::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit -> {
            QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
            Long userId = questionSubmit.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionSubmitVO.setUserVO(userFeignClient.getUserVO(user));
            return questionSubmitVO;
        }).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }



}




