package gay.sukumi.irc.listener;

import gay.sukumi.irc.profile.UserProfile;

public interface UserProfileListener {

    void onConnected(UserProfile profile);
    void onDisconnected(UserProfile profile);

    void onNameChange(UserProfile profile);
    void onServerChange(UserProfile profile);

}
