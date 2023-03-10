package gay.sukumi.irc.listener;

import de.datasecs.hydra.shared.handler.Session;
import gay.sukumi.irc.profile.UserProfile;
import gay.sukumi.irc.utils.Errors;

public interface LoginListener {

    void onSuccess(UserProfile userProfile, Session session);

    void onFail(Errors reason, Session session);

}
