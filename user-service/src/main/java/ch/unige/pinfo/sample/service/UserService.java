package ch.unige.pinfo.sample.service;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import ch.unige.pinfo.sample.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class UserService {

    @Channel("user-update")
    Emitter<User> usersUpdateEmmitter;
        
    @Incoming("user-command")
    @Transactional
    public void userCommand(String command) {
        if ("get-all".equals(command)) {
            User.findAll().stream().spliterator().forEachRemaining(u -> usersUpdateEmmitter.send((User) u));
        }
    }
    
    @Transactional
    public User create(User user) {
        user.setStatus(User.UserStatus.ACTIVE);
        User.persist(user);
        // Propagate creation of the user to the messaging system
        usersUpdateEmmitter.send(user);
        return user;
    }

    @Transactional
    public void disableUser(Long id) {
        User entity = User.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }
        entity.setStatus(User.UserStatus.INACTIVE);
        usersUpdateEmmitter.send(entity);
    }

}
