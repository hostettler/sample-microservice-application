package ch.unige.pinfo.sample.service;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ConsistencyCheckService {

    EmbeddedCacheManager emc;
    
    @Inject
    public ConsistencyCheckService(EmbeddedCacheManager emc) {
        this.emc = emc;
    }
    
    public boolean checkUserExists(String userId) {
        return getUserCache().get(userId) != null;
    }

    public boolean checkOrganisationBranchId(String branchId) {
        return getOrgCache().get(branchId) != null;
    }


    
    @Channel("user-command")
    Emitter<String> usersCommandsEmmitter;
    @Channel("org-command")
    Emitter<String> orgsCommandsEmmitter;    

    void onStart(@Observes StartupEvent ev) {
      usersCommandsEmmitter.send("get-all");
      orgsCommandsEmmitter.send("get-all");
    }

    @Incoming("user-update")
    @Transactional
    public void updateUserIds(String userId) {
        getUserCache().put(userId, userId);
    }
    
    
    @Incoming("org-update")
    @Transactional
    public void userCommand(String orgId) {
        getUserCache().put(orgId, orgId);
    }
    
    private Cache<String, String> getUserCache() {
        return emc.getCache("users");
    }
    
    private Cache<String, String> getOrgCache() {
        return emc.getCache("orgs");
    }
}
