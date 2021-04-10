package org.acme.kafka.interceptor;


import lombok.extern.slf4j.Slf4j;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@KafkaReply
@Interceptor
@Slf4j
public class KafkaReplyInterceptor {
    @AroundInvoke
    public Object manageReply(InvocationContext ctx) throws Exception {
        log.info("in Interceptor");

        Object[] params = ctx.getParameters();
        for (Object param : params) {
            log.info("Parameter: {}", param);
        }
        ctx.getContextData().forEach((k,v) -> log.info("Context Data: {} {}", k, v));
        Object obj = ctx.proceed();
        return obj;
    }
}
