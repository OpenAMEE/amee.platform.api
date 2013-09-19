package com.amee.platform.resource;

import com.amee.domain.APIVersion;
import com.amee.service.data.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ApiVersionSetEditor extends PropertyEditorSupport {

    @Autowired
    private DataService dataService;

    @Override
    public void setAsText(String text) {
        List<String> submittedVersions = new ArrayList<String>();
        if (!text.isEmpty()) {
            for (String version : text.split(",")) {
                submittedVersions.add(version);
            }
        }

        Set<APIVersion> apiVersions = new HashSet<APIVersion>();
        for (APIVersion apiVersion : dataService.getAPIVersions()) {
            if (submittedVersions.contains(apiVersion.getVersion())) {
                apiVersions.add(apiVersion);
            }
        }

        setValue(apiVersions);
    }
}
