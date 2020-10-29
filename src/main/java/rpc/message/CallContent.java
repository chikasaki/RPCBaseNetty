package rpc.message;

import java.io.Serializable;

public class CallContent implements Serializable, Content {

    private static final long serialVersionUID = -4785903185399611375L;

    private String className;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] parameterTypes;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
