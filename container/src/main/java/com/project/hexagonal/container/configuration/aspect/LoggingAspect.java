package com.project.hexagonal.container.configuration.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @AfterThrowing(pointcut = "execution(* com.project.hexagonal.application.adapter.*(..))", throwing = "exc")
    public void logServiceExceptions(JoinPoint joinPoint, Throwable exc) {
        log.error("An exception occured in method: {} \n ExceptionMessage: {} \n Method arguments: {} \n Exception Type: {}",
                joinPoint.getSignature().getName(),
                exc.getMessage(),
                (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0 ? joinPoint.getArgs() : "No method arguments!"),
                exc.getClass().getName()
        );
    }
}
