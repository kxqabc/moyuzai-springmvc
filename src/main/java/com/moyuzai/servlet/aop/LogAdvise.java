package com.moyuzai.servlet.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Aspect风格的Aop类，负责打印controller每个方法的输入参数、方法返回值以及抛出的异常
 */
@Component
@Aspect
public class LogAdvise {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    //within(com.moyuzai.servlet.controller.*)表示controller包下所有的连接点joinpoint都是pointcut，这里的joinpoint可以认为是所有的方法，
    // 而pointcut只是对符合我们想要增强（advise）的joinpoint的一种描述，描述什么样的joinpoint才是我们想要增强对象。
    @Pointcut("within(com.moyuzai.servlet.controller.*)")
    public void logPointCut(){

    }

    //增强（advise）
    @Before("logPointCut()")
    public void logMethodInvokeParam(JoinPoint joinPoint){
        logger.info("执行方法：{}, 参数：{}",joinPoint.getSignature().toShortString(),joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "logPointCut()",returning = "retVal")
    public void logMethodInvokeResult(JoinPoint joinPoint,Object retVal){
        logger.info("方法：{} 的返回值：{}",joinPoint.getSignature().toShortString(),joinPoint.getArgs());
    }

    @AfterThrowing(pointcut = "logPointCut()",throwing = "exception")
    public void logMethodInvokeException(JoinPoint joinPoint,Exception exception){
        logger.info("方法：{}出现异常：{}",joinPoint.getSignature().toShortString(),exception.getMessage());
    }
}
