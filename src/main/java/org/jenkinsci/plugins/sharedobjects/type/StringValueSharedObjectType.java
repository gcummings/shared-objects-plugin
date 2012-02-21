package org.jenkinsci.plugins.sharedobjects.type;

import hudson.Extension;
import hudson.Util;
import org.jenkinsci.plugins.sharedobjects.SharedObjectException;
import org.jenkinsci.plugins.sharedobjects.SharedObjectType;
import org.jenkinsci.plugins.sharedobjects.SharedObjectTypeDescriptor;
import org.jenkinsci.plugins.sharedobjects.service.SharedObjectLogger;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author Gregory Boissinot
 */
public class StringValueSharedObjectType extends SharedObjectType {

    private String value;

    @DataBoundConstructor
    public StringValueSharedObjectType(String name, String profiles, String value) {
        this.name = Util.fixEmptyAndTrim(name);
        this.profiles = Util.fixEmptyAndTrim(profiles);
        this.value = Util.fixEmptyAndTrim(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getEnvVarValue(SharedObjectLogger logger) throws SharedObjectException {
        return value;
    }

    @Extension
    public static class StringValueSharedObjectTypeDescriptor extends SharedObjectTypeDescriptor {

        @Override
        public String getDisplayName() {
            return "Simple content";
        }

        @Override
        public Class<? extends SharedObjectType> getType() {
            return ClearcaseSharedObjectType.class;
        }
    }
}