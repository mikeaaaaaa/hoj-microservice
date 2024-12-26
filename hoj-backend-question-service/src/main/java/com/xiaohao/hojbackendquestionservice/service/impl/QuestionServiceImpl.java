package com.xiaohao.hojbackendquestionservice.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaohao.hojbackendcommon.common.ErrorCode;
import com.xiaohao.hojbackendcommon.constant.CommonConstant;
import com.xiaohao.hojbackendcommon.exception.BusinessException;
import com.xiaohao.hojbackendcommon.exception.ThrowUtils;
import com.xiaohao.hojbackendcommon.utils.SqlUtils;
import com.xiaohao.hojbackendmodel.model.dto.question.QuestionQueryRequest;
import com.xiaohao.hojbackendmodel.model.entity.Question;
import com.xiaohao.hojbackendmodel.model.entity.User;
import com.xiaohao.hojbackendmodel.model.enums.QuestionDifficultyEnum;
import com.xiaohao.hojbackendmodel.model.vo.QuestionVO;
import com.xiaohao.hojbackendmodel.model.vo.UserVO;
import com.xiaohao.hojbackendquestionservice.mapper.QuestionMapper;
import com.xiaohao.hojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.xiaohao.hojbackendquestionservice.service.QuestionService;
import com.xiaohao.hojbackendserviceclient.feignClient.UserFeignClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author pc
 * @description 针对表【question(题目)】的数据库操作Service实现
 * @createDate 2024-12-04 22:16:26
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {


    @Resource
    private UserFeignClient userFeignClient;



    /**
     * 校验题目是否合法（这个需要我们自己重新编写）
     * @param question
     * @param add
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();
        Integer difficulty = question.getDifficulty();


        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(difficulty), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }

        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }

        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }

        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }

        if (ObjectUtils.isNotEmpty(difficulty) && !QuestionDifficultyEnum.getValues().contains(difficulty)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "难度设置错误");
        }
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前段传来的请求对象，得到mybatis支持的查询QueryWrapper类）
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        Integer difficulty = questionQueryRequest.getDifficulty();
        // 拼接查询条件,这里存在如果title、content相同的情况，采用或查询
        if(StringUtils.isNotBlank(title) && StringUtils.isNotBlank(content) && title.equals(content)){
            // 添加 title 或 content 任意一个 LIKE 条件
            queryWrapper.and(wrapper -> wrapper.like("title", title)
                    .or().like("content", content));
        }else{
            queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
            queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        }
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.eq(ObjectUtils.isNotEmpty(difficulty), "difficulty", difficulty);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    /**
     * 获取题目VO对象（这个需要我们自己重新编写）
     * @param question
     * @param request
     * @return
     */
    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userFeignClient.getById(userId);
        }
        UserVO userVO = userFeignClient.getUserVO(user);
        questionVO.setUserVO(userVO);
        return questionVO;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUserVO(userFeignClient.getUserVO(user));
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    public Page<QuestionVO> listQuestionVOByPage(QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long pageSize = questionQueryRequest.getPageSize();
        Page<Question> questionPage = this.page(new Page<>(current, pageSize),
                this.getQueryWrapper(questionQueryRequest));
        return this.getQuestionVOPage(questionPage, request);
    }
}




