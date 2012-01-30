package org.jenkinsci.plugins.sharedobjects;

import hudson.Extension;
import hudson.model.Hudson;
import hudson.model.TaskListener;
import hudson.remoting.Callable;
import org.jenkinsci.lib.envinject.EnvInjectException;
import org.jenkinsci.plugins.envinject.model.EnvInjectJobPropertyContributor;
import org.jenkinsci.plugins.envinject.model.EnvInjectJobPropertyContributorDescriptor;
import org.jenkinsci.plugins.sharedobjects.service.SharedObjectDataStore;
import org.jenkinsci.plugins.sharedobjects.service.SharedObjectLogger;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class SharedObjectJobProperty extends EnvInjectJobPropertyContributor {

    private boolean populateSharedObjects;

    private String profiles;

    public SharedObjectJobProperty() {
    }

    @DataBoundConstructor
    public SharedObjectJobProperty(boolean populateSharedObjects, String profiles) {
        this.populateSharedObjects = populateSharedObjects;
        this.profiles = profiles;
    }

    @SuppressWarnings("unused")
    public boolean isPopulateSharedObjects() {
        return populateSharedObjects;
    }

    @SuppressWarnings("unused")
    public String getProfiles() {
        return profiles;
    }

    @Override
    public void init() {
        populateSharedObjects = true;
    }

    @Override
    public Map<String, String> getEnvVars(TaskListener listener) throws EnvInjectException {
        SharedObjectLogger logger = new SharedObjectLogger(listener);
        Map<String, String> result = new HashMap<String, String>();
        if (populateSharedObjects) {
            logger.info("Injecting shared objects as environment variables");
            try {
                SharedObjectType[] sharedObjectTypes =
                        Hudson.getInstance().getRootPath().act(new Callable<SharedObjectType[], EnvInjectException>() {
                            public SharedObjectType[] call() throws EnvInjectException {
                                SharedObjectDataStore dataStore = new SharedObjectDataStore();
                                try {
                                    return dataStore.readSharedObjectsFile();
                                } catch (SharedObjectException e) {
                                    throw new EnvInjectException(e);
                                }
                            }
                        });

                if (sharedObjectTypes != null) {
                    for (SharedObjectType type : sharedObjectTypes) {
                        if (type != null && isProfilActivated(profiles, type)) {
                            result.put(type.getName(), type.getEnvVarValue(logger));
                        }
                    }
                }

            } catch (SharedObjectException se) {
                throw new EnvInjectException(se);
            } catch (IOException e) {
                throw new EnvInjectException(e);
            } catch (InterruptedException e) {
                throw new EnvInjectException(e);
            }
        }
        return result;
    }

    private boolean isProfilActivated(String profiles, SharedObjectType type) {
        if (profiles == null) {
            return true;
        }
        if (profiles.trim().length() == 0) {
            return true;
        }
        String typeProfile = type.getProfile();
        if (typeProfile == null) {
            return true;
        }
        if (typeProfile.length() == 0) {
            return true;
        }
        return Arrays.asList(profiles.split(";")).contains(typeProfile);
    }

    @Extension
    public static class SharedObjectJobPropertyDescriptor extends EnvInjectJobPropertyContributorDescriptor {

        @Override
        public String getDisplayName() {
            return "Populate shared objects";
        }


    }
}
