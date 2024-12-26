package com.xiaohao.hojbackendgateway.filter;

import cn.hutool.core.text.AntPathMatcher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class GlobalFIlter implements GlobalFilter, Ordered {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String path = serverHttpRequest.getURI().getPath();
        // 判断路径中是否含有inner
        if(antPathMatcher.match("/**/inner/**", path)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            DataBuffer dataBuffer = response.bufferFactory().wrap("Forbidden".getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(dataBuffer));
        }

        // todo 在这里可以做全局的权限校验，但是由于我们项目的实现方式通过会话来校验权限，在gateway中很难拿到会话信息，所以这里不做权限校验，要是想要作统一权限校验
        // todo 建议使用jwt方式的权限校验，在这里解析jwt token，然后校验权限，非常简单
        return chain.filter(exchange);
    }

    /**
     * 过滤器的优先级，数字越小，优先级越高，这里将优先级设置为最高，最先执行
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
