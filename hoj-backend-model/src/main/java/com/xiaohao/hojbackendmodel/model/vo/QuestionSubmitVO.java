package com.xiaohao.hojbackendmodel.model.vo;

import cn.hutool.json.JSONUtil;

import com.xiaohao.hojbackendmodel.model.dto.question.JudgeInfo;
import com.xiaohao.hojbackendmodel.model.entity.QuestionSubmit;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 已登录用户视图（脱敏）
 **/
@Data
public class QuestionSubmitVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 判题信息（json 对象）
     */
    private JudgeInfo judgeInfo;

    /**
     * 判题状态 (0-待判题、1-判题中、2-成功、3-失败)
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 提交用户信息
     */
    private UserVO userVO;


    private static final long serialVersionUID = 1L;

    public static QuestionSubmit voToObj(QuestionSubmitVO questionSubmitVO) {
        if (questionSubmitVO == null) {
            return null;
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitVO, questionSubmit);
        String judgeInfoVO = questionSubmit.getJudgeInfo();
        if(!Objects.nonNull(judgeInfoVO)){
            questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoVO));
        }
        return questionSubmit;
    }

    /**
     * 对象转包装类
     *
     * @param questionSubmit
     * @return
     */
    public static QuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, questionSubmitVO);
        String judgeInfo = questionSubmit.getJudgeInfo();
        if(StringUtils.isNotBlank(judgeInfo)){
            questionSubmitVO.setJudgeInfo(JSONUtil.toBean(judgeInfo, JudgeInfo.class));
        }
        return questionSubmitVO;
    }
}