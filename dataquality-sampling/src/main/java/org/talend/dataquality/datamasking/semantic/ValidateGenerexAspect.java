package org.talend.dataquality.datamasking.semantic;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class ValidateGenerexAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateGenerexAspect.class);

    @Pointcut("execution(final void dk.brics.automaton.State.resetTransitions())")
    public void toAutomation() {

    }

    @Before("toAutomation()")
    public void toAutomationExec() {
        if (Thread.currentThread().isInterrupted()) {
            LOGGER.warn("thread interrupted due to automaton lib");
            throw new RuntimeException();
        }
    }
}
