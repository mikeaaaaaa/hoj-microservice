package com.xiaohao.hojbackendmodel.model.dto.question;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 判题配置
 */
@NoArgsConstructor
@Data
public class JudgeConfig {
    /**
     * 时间限制（ms）
     */
    private Long timeLimit;
    /**
     * 内存限制（kb）
     */
    private Long memoryLimit;
    /**
     * 堆栈限制（kb）
     */
    private Long stackLimit;
}
