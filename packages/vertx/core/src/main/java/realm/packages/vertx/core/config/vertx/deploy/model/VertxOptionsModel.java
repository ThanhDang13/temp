package realm.packages.vertx.core.config.vertx.deploy.model;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import io.vertx.core.VertxOptions;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.stream.Collectors;

import static java.util.Collections.list;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Data
@Configuration
@ConfigurationProperties("vertx.vertx-options")
public class VertxOptionsModel {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Integer eventLoopPoolSize;
    protected Integer workerPoolSize;
    protected Integer internalBlockingPoolSize;
    protected Long blockedThreadCheckInterval;
    protected Long maxEventLoopExecuteTime;
    protected Long maxWorkerExecuteTime;
    protected Boolean haEnabled;
    protected Integer quorumSize;
    protected String haGroup;
    protected Long warningExceptionTime;
    protected Boolean fileResolverCachingEnabled;
    protected Boolean preferNativeTransport;

    protected String allowedClusterHost;

    protected String hazelcastConfig;

    public VertxOptions toVertxOptions() {
        VertxOptions o = new VertxOptions();
        if (eventLoopPoolSize != null) o.setEventLoopPoolSize(eventLoopPoolSize);
        if (workerPoolSize != null) o.setWorkerPoolSize(workerPoolSize);
        if (internalBlockingPoolSize != null) o.setInternalBlockingPoolSize(internalBlockingPoolSize);
        if (blockedThreadCheckInterval != null) o.setBlockedThreadCheckInterval(blockedThreadCheckInterval);
        if (maxEventLoopExecuteTime != null) o.setMaxEventLoopExecuteTime(maxEventLoopExecuteTime);
        if (maxWorkerExecuteTime != null) o.setMaxWorkerExecuteTime(maxWorkerExecuteTime);
        if (haEnabled != null) o.setHAEnabled(haEnabled);
        if (quorumSize != null) o.setQuorumSize(quorumSize);
        if (haGroup != null) o.setHAGroup(haGroup);
        if (warningExceptionTime != null) o.setWarningExceptionTime(warningExceptionTime);
//        if (fileResolverCachingEnabled != null) o.setFileResolverCachingEnabled(fileResolverCachingEnabled);
        if (preferNativeTransport != null) o.setPreferNativeTransport(preferNativeTransport);
        if (isNotEmpty(allowedClusterHost)) {
            String clusterHost = clusterHost();
            logger.debug("vertx cluster host: " + clusterHost);
//            o.setClusterHost(clusterHost);
        }

        // config hazelcast
        if (isNotEmpty(hazelcastConfig)) {
            Config config = new ClasspathXmlConfig(hazelcastConfig);
            o.setClusterManager(new HazelcastClusterManager(config));
        }

        return o;
    }

    private String clusterHost() {
        // Identify which n/w interface should be used by Vertx.
        try {
            return list(NetworkInterface.getNetworkInterfaces()).stream()
                    .flatMap(ni -> list(ni.getInetAddresses()).stream())
//                    .filter(address -> !address.isAnyLocalAddress())
//                    .filter(address -> !address.isMulticastAddress())
//                    .filter(address -> !address.isLoopbackAddress())
//                    .filter(address -> !(address instanceof Inet6Address))
                    .map(InetAddress::getHostAddress)
                    .filter(host -> host.startsWith(allowedClusterHost))
                    .collect(Collectors.toList())
                    .get(0);
        } catch (SocketException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
