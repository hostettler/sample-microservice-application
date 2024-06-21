package ch.unige.pinfo.sample.utils;

import org.infinispan.Cache;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class CacheManagerProducer {

    private static final Logger LOG = Logger.getLogger(CacheManagerProducer.class);

    
    private DefaultCacheManager cacheManager;
    private ConfigurationBuilder builder;
    
    public CacheManagerProducer() {
        LOG.info("************** Start the cache producer");
        // Set up a clustered Cache Manager.
        LOG.info("************** Start the GlobalConfigurationBuilder");
        GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();
        // Initialize the default Cache Manager.
        LOG.info("Start the cache manager");
        this.cacheManager = new DefaultCacheManager(global.build());
        // Create a distributed cache with synchronous replication.
        LOG.info("Start the Builder");
        this.builder = new ConfigurationBuilder();
                             builder.clustering().cacheMode(CacheMode.DIST_SYNC);  
        
    }
    
    @Produces
    @UserCache
    public Cache<String,String> getUserCache() {
        LOG.info("******************** Produce users cache");
        return cacheManager.administration().withFlags(
                CacheContainerAdmin.AdminFlag.VOLATILE).getOrCreateCache("users", builder.build());
    }
    @Produces
    @OrgsCache
    public Cache<String,String> getOrgsCache() {
        LOG.info("******************** Produce orgs cache");
        return cacheManager.administration().withFlags(
                CacheContainerAdmin.AdminFlag.VOLATILE).getOrCreateCache("orgs", builder.build());
    }

    
    @Produces
    public DefaultCacheManager getCacheManager() {
        LOG.info("******************* Produce cache manager");
        return this.cacheManager;
    }
    
}
