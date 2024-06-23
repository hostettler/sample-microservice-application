package ch.unige.pinfo.sample.service;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.infinispan.Cache;
import org.jboss.logging.Logger;

import ch.unige.pinfo.sample.utils.OrgsCache;
import ch.unige.pinfo.sample.utils.UserCache;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ConsistencyCheckService {

    private static final Logger LOG = Logger.getLogger(ConsistencyCheckService.class);
  
    Cache<String, String> users;   
    Cache<String, String> orgs;

    @Inject
    public ConsistencyCheckService(@UserCache Cache<String, String> users, @OrgsCache Cache<String, String> orgs) {
        this.users = users;
        this.orgs = orgs;
    }

    public boolean checkUserExists(String userId) {
        LOG.debug(String.format("Check user %s status:%s", userId, users.get(userId)));
        return users.get(userId) != null;
    }

    public boolean checkOrganisationBranchId(String branchId) {
        LOG.debug(String.format("Check branch %s status:%s", branchId, orgs.get(branchId)));
        return orgs.get(branchId) != null;
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
        LOG.debug("Update user cache with " + userId);
        users.put(userId, userId);
    }

    @Incoming("org-update")
    @Transactional
    public void updateOrgIds(String orgId) {
        LOG.debug("Update org cache with " + orgId);
        orgs.put(orgId, orgId);
    }

}
