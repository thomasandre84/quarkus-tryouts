package org.acme.kafka.interceptor;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface KafkaReply {
    String outgoing() default "request";
    String incoming() default "reply";
}
