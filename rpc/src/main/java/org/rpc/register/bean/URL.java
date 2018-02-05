package org.rpc.register.bean;

/**
 * The type Url.
 *
 * @author yefei
 * @date 2017 -06-28 14:20
 */
public class URL {
    /**
     * 类型 服务提供者or消费者
     */
    private URL.Type type;

    /**
     * 组
     */
    private String group;

    /**
     * 接口全限定名
     */
    private String interfaceName;

    /**
     * ip
     */
    private String Ip;

    /**
     * 版本号
     */
    private String version;

    /**
     * 时间戳
     */
    private Long timestamp;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("type=").append(type);
        sb.append(", group='").append(group).append('\'');
        sb.append(", interfaceName='").append(interfaceName).append('\'');
        sb.append(", Ip='").append(Ip).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", timestamp=").append(timestamp);
        return sb.toString();
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(Type type) {
        this.type = type;
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
     * @param interfaceName the interface name
     */
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    /**
     * Gets ip.
     *
     * @return the ip
     */
    public String getIp() {
        return Ip;
    }

    /**
     * Sets ip.
     *
     * @param ip the ip
     */
    public void setIp(String ip) {
        Ip = ip;
    }

    /**
     * Gets version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets version.
     *
     * @param version the version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets timestamp.
     *
     * @return the timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets timestamp.
     *
     * @param timestamp the timestamp
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets group.
     *
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets group.
     *
     * @param group the group
     */
    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        URL url = (URL) o;

        if (type != url.type)
            return false;
        if (group != null ? !group.equals(url.group) : url.group != null)
            return false;
        if (interfaceName != null ? !interfaceName.equals(url.interfaceName) : url.interfaceName != null)
            return false;
        if (Ip != null ? !Ip.equals(url.Ip) : url.Ip != null)
            return false;
        if (version != null ? !version.equals(url.version) : url.version != null)
            return false;
        return timestamp != null ? timestamp.equals(url.timestamp) : url.timestamp == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (interfaceName != null ? interfaceName.hashCode() : 0);
        result = 31 * result + (Ip != null ? Ip.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    /**
     * @author yefei
     * @date 2017 -6-28 11:21:03 The enum Type.
     */
    enum Type {

        /**
         * Provider type.
         */
        PROVIDER,

        /**
         * Consumer type.
         */
        CONSUMER;
    }
}
