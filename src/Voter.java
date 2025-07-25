import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;

public class Voter {
    private String voterId;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private boolean hasVoted;

    public Voter(String voterId, KeyPair keyPair) {
        this.voterId = voterId;
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
        this.hasVoted = false;
    }

    public String getVoterId() {
        return voterId;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public boolean hasVoted() {
        return hasVoted;
    }

    public void setVoted(boolean voted) {
        this.hasVoted = voted;
    }
}