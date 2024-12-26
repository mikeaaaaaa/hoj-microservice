package com.xiaohao.hojbackendmodel.model.dto.questionsubmit;

import com.xiaohao.hojbackendcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 帖子点赞请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 提交状态
     */
    private Integer status;


    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 题目 id
     */
    private Long questionId;


    private static final long serialVersionUID = 1L;
}