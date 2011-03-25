package com.amee.base.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * A service bean to aid in discovery of beans whilst respecting the {@link Since} and {@link Until} annotations.
 */
@Service
public class VersionBeanFinder implements ApplicationContextAware {

    public interface VersionBeanMatcher {
        public boolean matches(Object bean);
    }

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
        return getBeanForVersion(className, version, null);
    }

    /**
     * Finds a bean of type className which is appropriate for the supplied Version. The most
     * appropriate bean is that which has the most recent Since version but is not excluded by an until
     * version. If a VersionBeanMatcher is supplied then this is used to select the correct bean where
     * more than one exists for the Version.
     *
     * @param className for requested bean
     * @param version   to take into account
     * @param matcher   a matcher
     * @return the matching bean
     */
    public Object getBeanForVersion(String className, Version version, VersionBeanMatcher matcher) {
        TreeMap<Version, List<String>> candidates =
                new TreeMap<Version, List<String>>(getBeanNamesForVersion(className, version));
        if (!candidates.isEmpty()) {
            if (matcher == null) {
                // Get the first bean in the list and ignore the rest.
                return applicationContext.getBean(candidates.get(candidates.lastKey()).get(0));
            } else {
                // Check all candidates with the matcher.
                for (Version v : candidates.descendingKeySet()) {
                    for (String beanName : candidates.get(v)) {
                        Object bean = applicationContext.getBean(beanName);
                        if (matcher.matches(bean)) {
                            return bean;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets a Map of bean names keyed by their Since version which are of type className and
     * are appropriate given the supplied Version. Appropriate bean names are those where the
     * supplied version is not less than the Since version and not greater than the Until version.
     *
     * @param className for requested bean
     * @param version   to take into account
     * @return map of bean names keyed by their Since version
     */
    protected Map<Version, List<String>> getBeanNamesForVersion(String className, Version version) {
        Map<Version, List<String>> candidates = new HashMap<Version, List<String>>();
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
                                addCandidate(candidates, since, beanName);
                            }
                        } else {
                            // Until not specified.
                            // We have a candidate.
                            addCandidate(candidates, since, beanName);
                        }
                    }
                } else {
                    // No since, so mark as V0.
                    addCandidate(candidates, new Version("0"), beanName);
                }
            }
        } catch (ClassNotFoundException e) {
            log.warn("getHandler() Class not found: " + className);
        }
        return candidates;
    }

    private void addCandidate(Map<Version, List<String>> candidates, Version version, String beanName) {
        if (candidates.get(version) == null) {
            candidates.put(version, new ArrayList<String>());
        }
        candidates.get(version).add(beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
