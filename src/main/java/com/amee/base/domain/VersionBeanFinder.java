package com.amee.base.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * A service bean to aid in discovery of beans whilst respecting the Since and Until annotations.
 */
@Service
public class VersionBeanFinder implements ApplicationContextAware {

    private final Log log = LogFactory.getLog(getClass());

    private ApplicationContext applicationContext;

    /**
     * Finds a bean of type className which is appropriate for the supplied Version. The most
     * appropriate bean is that which has the most recent Since version but is not excluded by an until
     * version.
     *
     * @param className for requested bean
     * @param version   to take into account
     * @return the matching bean
     */
    public Object getBeanForVersion(String className, Version version) {
        Object bean = null;
        TreeMap<Version, String> candidates =
                new TreeMap<Version, String>(getBeanNamesForVersion(className, version));
        if (!candidates.isEmpty()) {
            bean = applicationContext.getBean(candidates.get(candidates.lastKey()));
        }
        return bean;
    }

    /**
     * Gets a Map of bean names keyed by their Since version which are of type className and
     * are appropriate given the supplied Version. Appropriate bean names are those where the
     * supplied version is not less than the Since version and not greater than the Until version.
     * <p/>
     * TODO: This implementation causes an exhaustive search of beans. There is an opportunity for improvement.
     * TODO: In development testing this took 38ms on the first run and then 1ms on subsequent runs.
     *
     * @param className for requested bean
     * @param version   to take into account
     * @return map of bean names keyed by their Since version
     */
    public Map<Version, String> getBeanNamesForVersion(String className, Version version) {
        Map<Version, String> candidates = new HashMap<Version, String>();
        try {
            // Iterate over all bean names matching the target type.
            Class clazz = Class.forName(className);
            for (String beanName : applicationContext.getBeanNamesForType(clazz)) {
                // Requested version must not be before since.
                Since sinceAnn = applicationContext.findAnnotationOnBean(beanName, Since.class);
                if (sinceAnn != null) {
                    // Since is specified.
                    Version since = new Version(sinceAnn.value());
                    if (!version.before(since)) {
                        // Requested version must not be after until.
                        Until untilAnn = applicationContext.findAnnotationOnBean(beanName, Until.class);
                        if (untilAnn != null) {
                            Version until = new Version(sinceAnn.value());
                            if (!version.after(until)) {
                                // We have a candidate.
                                candidates.put(since, beanName);
                            }
                        } else {
                            // Until not specified.
                            // We have a candidate.
                            candidates.put(since, beanName);
                        }
                    }
                } else {
                    // No since, so mark as V0.
                    candidates.put(new Version("0"), beanName);
                }
            }
        } catch (ClassNotFoundException e) {
            log.warn("getHandler() Class not found: " + className);
        }
        return candidates;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
