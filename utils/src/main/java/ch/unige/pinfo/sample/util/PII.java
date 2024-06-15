package ch.unige.pinfo.sample.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * This annotation is only there as a mean to mark potential PII
 */
@Target({ElementType.FIELD})
public @interface PII {

}
