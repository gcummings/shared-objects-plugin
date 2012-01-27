package org.jenkinsci.plugins.sharedobjects;

import hudson.ExtensionPoint;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.TaskListener;

import java.io.Serializable;

/**
 * @author Gregory Boissinot
 */
public abstract class SharedObjectType implements ExtensionPoint, Describable<SharedObjectType>, Serializable {

    protected String name;

    @Override
    public Descriptor<SharedObjectType> getDescriptor() {
        return (SharedObjectTypeDescriptor) Hudson.getInstance().getDescriptor(getClass());
    }

    @SuppressWarnings("unused")
    public String getName() {
        return name;
    }

    public abstract String getEnvVarValue(TaskListener listener) throws SharedObjectException;
}