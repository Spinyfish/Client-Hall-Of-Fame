package club.pulsive.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientInfo {
    private final String clientName, clientVersion;
    private final boolean isAlphaBuild;



    public String getClientTitle(){
        return clientName + " " + clientVersion + (isAlphaBuild ? "[ALPHA]" : "");
    }
}
