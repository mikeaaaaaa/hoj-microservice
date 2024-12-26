package com.xiaohao.hojbackendjudgeservice.rabbitmq;

import com.rabbitmq.client.Channel;
import com.xiaohao.hojbackendcommon.constant.CodeRabbitmqConstant;
import com.xiaohao.hojbackendjudgeservice.judge.JudgeService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;


/**
 * 消息消费类，监听指定的 RabbitMQ 队列并处理接收到的消息
 */
@Slf4j
@Component
public class MessageConsumer {
    @Resource
    private JudgeService judgeService;


    /**
     * 消费消息的方法，使用 RabbitListener 注解监听指定队列的消息
     *
     * @param message     消息内容
     * @param channel     当前消息的通道，提供消息确认等功能
     * @param deliveryTag 消息的投递标识，用于消息确认
     */
    @SneakyThrows  // 自动处理 checked 异常，避免手动捕获和抛出
    @RabbitListener(queues = {CodeRabbitmqConstant.CODE_QUEUE}, ackMode = "MANUAL") // 监听队列，手动确认消息
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        // 打印接收到的消息
        log.info("Received message: " + message);
        Long questionSubmitId = Long.parseLong(message);
        try {
            judgeService.doJudge(questionSubmitId);
            // 手动确认消息，告知 RabbitMQ 消息已被成功处理
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            // 手动拒绝消息，告知 RabbitMQ 消息处理失败，可以重新投递
            channel.basicNack(deliveryTag, false, true);
        } catch (RuntimeException e) {
            // 捕获并处理 RuntimeException，记录日志，拒绝消息
            log.error("RuntimeException while processing message: " + message, e);
            // todo 第三个参数为true，表示消息会被重新投递到队列，false 表示消息会被丢弃，这里一般需要仔细地权衡才行
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
