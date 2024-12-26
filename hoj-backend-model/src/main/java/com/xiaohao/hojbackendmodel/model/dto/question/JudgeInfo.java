package com.xiaohao.hojbackendmodel.model.dto.question;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 判题信息
 */
@NoArgsConstructor
@Data
public class JudgeInfo {
    /**
     * 判题执行信息
     */
    private String message;
    /**
     * 内存消耗（kb）
     */
    private Long memory;
    /**
     * 时间消耗（ms）
     */
    private Long time;
}
