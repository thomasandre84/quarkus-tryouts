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
        Object obj = ctx.proceed();
        return obj;
    }
}
