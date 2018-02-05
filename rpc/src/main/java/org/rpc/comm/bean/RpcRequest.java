package org.rpc.comm.bean;

/**
 * The type Rpc request.
 *
 * @author yefei
 * @date 2017 -6-20 13:12:57
 */
public class RpcRequest {

    private String requestId;
    private String interfaceName;
    private String serviceVersion = "1.0.0";
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

    /**
     * Gets request id.
     *
     * @return the request id
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets request id.
     *
     * @param requestId the request id
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets interface name.
     *
     * @return the interface name
     */
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     * Sets interface name.
     *
     * @param className the class name
     */
    public void setInterfaceName(String className) {
        this.interfaceName = className;
    }

    /**
     * Gets service version.
     *
     * @return the service version
     */
    public String getServiceVersion() {
        return serviceVersion;
    }

    /**
     * Sets service version.
     *
     * @param serviceVersion the service version
     */
    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    /**
     * Gets method name.
     *
     * @return the method name
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets method name.
     *
     * @param methodName the method name
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Get parameter types class [ ].
     *
     * @return the class [ ]
     */
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Sets parameter types.
     *
     * @param parameterTypes the parameter types
     */
    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    /**
     * Get parameters object [ ].
     *
     * @return the object [ ]
     */
    public Object[] getParameters() {
        return parameters;
    }

    /**
     * Sets parameters.
     *
     * @param parameters the parameters
     */
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
