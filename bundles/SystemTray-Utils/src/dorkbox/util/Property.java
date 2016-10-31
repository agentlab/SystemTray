package dorkbox.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to track system properties across the entire code-base. System properties are values that can be changed programmatically,
 * via a "System.getProperty()", or via arguments.
 * <br>
 *  Loading arguments is left for the end-user application. Using an annotation detector to load/save properties is recommended.
 *  <br>
 *  For example (if implemented): -Ddorkbox.Args.Debug=true
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD})
public
@interface Property {

    /**
     * This is used to mark a unique value for the property, in case the object name is used elsewhere, is generic, or is repeated.
     */
    String alias() default "";
}
