public class Vote {
    private String encryptedVote;
    private String signature;
    private String encryptedAesKey;
    private String iv;

    public Vote(String encryptedVote, String signature, String encryptedAesKey, String iv) {
        this.encryptedVote = encryptedVote;
        this.signature = signature;
        this.encryptedAesKey = encryptedAesKey;
        this.iv = iv;
    }

    public String getEncryptedVote() {
        return encryptedVote;
    }

    public String getSignature() {
        return signature;
    }

    public String getEncryptedAesKey() {
        return encryptedAesKey;
    }

    public String getIv() {
        return iv;
    }
}
