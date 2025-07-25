import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.security.KeyPair;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class VotingApp {
    private static final Map<String, Voter> voters = new HashMap<>();
    private static final List<Vote> voteBox = new ArrayList<>();
    private static final Map<String, Integer> voteCounts = new HashMap<>();
    private static JTextArea candidateViewArea;
    private static JLabel voterStatus;
    private static JTextField voterIdField;
    private static JComboBox<String> candidateCombo;

    public static void main(String[] args) {
        voteCounts.put("CandidateA", 0);
        voteCounts.put("CandidateB", 0);

        SwingUtilities.invokeLater(() -> {
            createVoterWindow();
            createCandidateWindow();
        });
    }

    private static void createVoterWindow() {
        JFrame voterFrame = new JFrame("Voter Panel");
        voterFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        voterFrame.setSize(400, 200);

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Vote Here"));
        panel.add(new JLabel("Voter ID:"));
        voterIdField = new JTextField();
        panel.add(voterIdField);

        panel.add(new JLabel("Select Candidate:"));
        candidateCombo = new JComboBox<>(new String[]{"CandidateA", "CandidateB"});
        panel.add(candidateCombo);

        JButton voteButton = new JButton("Register & Vote");
        voteButton.addActionListener(VotingApp::castVote);
        panel.add(voteButton);

        voterStatus = new JLabel();
        panel.add(voterStatus);

        voterFrame.add(panel);
        voterFrame.setLocation(100, 100);
        voterFrame.setVisible(true);
    }

    private static void createCandidateWindow() {
        JFrame candidateFrame = new JFrame("Candidate Vote Count");
        candidateFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        candidateFrame.setSize(300, 200);
        candidateViewArea = new JTextArea();
        candidateViewArea.setEditable(false);
        candidateFrame.add(new JScrollPane(candidateViewArea));
        candidateFrame.setLocation(550, 100);
        candidateFrame.setVisible(true);
        updateCandidateWindow();
    }

    private static void castVote(ActionEvent e) {
        String id = voterIdField.getText().trim();
        String candidate = (String) candidateCombo.getSelectedItem();

        if (id.isEmpty()) {
            voterStatus.setText("Please enter Voter ID.");
            return;
        }

        Voter voter = voters.get(id);
        if (voter == null) {
            try {
                KeyPair keyPair = RSAUtil.generateKeyPair();
                voter = new Voter(id, keyPair);
                voters.put(id, voter);
            } catch (Exception ex) {
                ex.printStackTrace();
                voterStatus.setText("Registration failed.");
                return;
            }
        }

        if (voter.hasVoted()) {
            voterStatus.setText("You have already voted.");
            return;
        }

        try {
            SecretKey aesKey = AESUtil.generateKey();
            IvParameterSpec iv = AESUtil.generateIV();
            String encryptedVote = AESUtil.encrypt(candidate, aesKey, iv);
            String encryptedAESKey = RSAUtil.encrypt(AESUtil.encodeKey(aesKey), voter.getPublicKey());
            String signature = RSAUtil.sign(encryptedVote, voter.getPrivateKey());
            Vote vote = new Vote(encryptedVote, signature, encryptedAESKey, Base64.getEncoder().encodeToString(iv.getIV()));
            voteBox.add(vote);
            voter.setVoted(true);
            voteCounts.put(candidate, voteCounts.get(candidate) + 1);
            voterStatus.setText("Vote successfully cast!");
            updateCandidateWindow();
        } catch (Exception ex) {
            ex.printStackTrace();
            voterStatus.setText("Error occurred while voting.");
        }
    }

    private static void updateCandidateWindow() {
        StringBuilder sb = new StringBuilder("Live Voting Results:\n");
        for (Map.Entry<String, Integer> entry : voteCounts.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" votes\n");
        }
        candidateViewArea.setText(sb.toString());
    }
}
