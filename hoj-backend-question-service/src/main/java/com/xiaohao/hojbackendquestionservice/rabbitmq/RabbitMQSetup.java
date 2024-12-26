package com.xiaohao.hojbackendquestionservice.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.xiaohao.hojbackendcommon.constant.CodeRabbitmqConstant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RabbitMQSetup {

    private static String host = "192.168.123.81";

    private static int port = 5672;

    private static String username = "root";

    private static String password = "123456";

    // 初始化RabbitMQ连接、交换机和队列
    public static void doInit() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPort(port);
            factory.setUsername(username);
            factory.setPassword(password);
            Connection connection = factory.newConnection();
            // 使用连接创建一个频道（Channel），channel就是用来操作消息的客户端，类似于docker client、jdbc client
            Channel channel = connection.createChannel();

            // 设置交换机的名称
            // 声明一个交换机，类型是direct，持久化为true，表示交换机会在RabbitMQ重启后仍然存在
            channel.exchangeDeclare(CodeRabbitmqConstant.CODE_EXCHANGE, "direct", true);

            // 创建队列名称
            // 声明队列，参数依次表示：队列名、持久化（true表示队列会在RabbitMQ重启后保留）、是否为独占队列、是否自动删除、额外属性
            channel.queueDeclare(CodeRabbitmqConstant.CODE_QUEUE, true, false, false, null);

            // 将队列绑定到交换机，并指定路由键"my_routingKey"
            // 这样，当有消息发送到交换机且路由键匹配时，消息会被路由到这个队列
            channel.queueBind(CodeRabbitmqConstant.CODE_QUEUE, CodeRabbitmqConstant.CODE_EXCHANGE, CodeRabbitmqConstant.CODE_ROUTING_KEY);

            log.info("RabbitMQ初始化成功");
        } catch (Exception e) {
            // 如果发生IOException，抛出RuntimeException
            log.error("RabbitMQ启动失败", e);
        }
    }
}

