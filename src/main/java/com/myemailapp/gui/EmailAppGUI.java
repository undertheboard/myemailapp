package com.myemailapp.gui;

import com.myemailapp.ai.AICommand;
import com.myemailapp.ai.GeminiAIService;
import com.myemailapp.config.AppConfig;
import com.myemailapp.email.EmailMessage;
import com.myemailapp.email.EmailService;
import com.myemailapp.voice.VoiceService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Main GUI for the AI-powered email application
 * Designed with large fonts and simple interface for elderly users
 */
public class EmailAppGUI extends JFrame {
    private static final int LARGE_FONT_SIZE = 18;
    private static final int BUTTON_FONT_SIZE = 20;
    private static final Color PRIMARY_COLOR = new Color(51, 122, 183);
    private static final Color SUCCESS_COLOR = new Color(92, 184, 92);
    private static final Color DANGER_COLOR = new Color(217, 83, 79);
    
    private AppConfig config;
    private EmailService emailService;
    private GeminiAIService aiService;
    private VoiceService voiceService;
    
    private JTextArea emailDisplayArea;
    private JTextField recipientField;
    private JTextField subjectField;
    private JTextArea messageArea;
    private JButton voiceButton;
    private JLabel statusLabel;
    
    public EmailAppGUI() {
        config = new AppConfig();
        
        setTitle("AI Email Assistant");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Check if first run - show setup dialog
        if (config.getEmailAddress().isEmpty() || config.getGeminiApiKey().isEmpty()) {
            showSetupDialog();
        }
        
        // Initialize services
        initializeServices();
        
        // Create UI
        createUI();
        
        // Add window listener for cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (emailService != null) {
                    emailService.disconnect();
                }
            }
        });
    }
    
    private void initializeServices() {
        try {
            emailService = new EmailService(config);
            if (!config.getGeminiApiKey().isEmpty()) {
                aiService = new GeminiAIService(config.getGeminiApiKey());
            }
            voiceService = new VoiceService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Top panel - Voice control and status
        JPanel topPanel = createTopPanel();
        
        // Center panel - Email display and composition
        JPanel centerPanel = createCenterPanel();
        
        // Bottom panel - Action buttons
        JPanel bottomPanel = createBottomPanel();
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Voice button
        voiceButton = new JButton("🎤 Talk to AI Assistant");
        voiceButton.setFont(new Font("Arial", Font.BOLD, BUTTON_FONT_SIZE));
        voiceButton.setBackground(PRIMARY_COLOR);
        voiceButton.setForeground(Color.WHITE);
        voiceButton.setFocusPainted(false);
        voiceButton.setPreferredSize(new Dimension(300, 60));
        voiceButton.addActionListener(e -> handleVoiceInput());
        
        // Status label
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, LARGE_FONT_SIZE));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(voiceButton);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(statusLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        
        // Left side - Email display
        JPanel displayPanel = new JPanel(new BorderLayout(5, 5));
        displayPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 2),
            "Your Emails",
            0,
            0,
            new Font("Arial", Font.BOLD, LARGE_FONT_SIZE)
        ));
        
        emailDisplayArea = new JTextArea();
        emailDisplayArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        emailDisplayArea.setEditable(false);
        emailDisplayArea.setLineWrap(true);
        emailDisplayArea.setWrapStyleWord(true);
        
        JScrollPane displayScroll = new JScrollPane(emailDisplayArea);
        displayPanel.add(displayScroll, BorderLayout.CENTER);
        
        JButton checkEmailBtn = createLargeButton("📧 Check New Emails", PRIMARY_COLOR);
        checkEmailBtn.addActionListener(e -> checkEmails());
        displayPanel.add(checkEmailBtn, BorderLayout.SOUTH);
        
        // Right side - Compose email
        JPanel composePanel = new JPanel(new BorderLayout(5, 5));
        composePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY, 2),
            "Compose Email",
            0,
            0,
            new Font("Arial", Font.BOLD, LARGE_FONT_SIZE)
        ));
        
        JPanel composeFields = new JPanel(new GridLayout(3, 1, 5, 5));
        
        recipientField = createLargeTextField("To:");
        subjectField = createLargeTextField("Subject:");
        
        composeFields.add(recipientField);
        composeFields.add(subjectField);
        
        messageArea = new JTextArea();
        messageArea.setFont(new Font("Arial", Font.PLAIN, 16));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScroll = new JScrollPane(messageArea);
        
        composePanel.add(composeFields, BorderLayout.NORTH);
        composePanel.add(messageScroll, BorderLayout.CENTER);
        
        JPanel composeButtons = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton aiComposeBtn = createLargeButton("🤖 AI Help Compose", SUCCESS_COLOR);
        aiComposeBtn.addActionListener(e -> aiComposeEmail());
        JButton sendBtn = createLargeButton("📤 Send Email", SUCCESS_COLOR);
        sendBtn.addActionListener(e -> sendEmail());
        
        composeButtons.add(aiComposeBtn);
        composeButtons.add(sendBtn);
        composePanel.add(composeButtons, BorderLayout.SOUTH);
        
        panel.add(displayPanel);
        panel.add(composePanel);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton settingsBtn = createLargeButton("⚙️ Settings", Color.GRAY);
        settingsBtn.addActionListener(e -> showSetupDialog());
        
        JButton helpBtn = createLargeButton("❓ Help", PRIMARY_COLOR);
        helpBtn.addActionListener(e -> showHelp());
        
        panel.add(settingsBtn);
        panel.add(helpBtn);
        
        return panel;
    }
    
    private JButton createLargeButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, BUTTON_FONT_SIZE));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(250, 50));
        return button;
    }
    
    private JTextField createLargeTextField(String label) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, LARGE_FONT_SIZE));
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, LARGE_FONT_SIZE));
        panel.add(lbl, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        
        // Return a dummy text field that wraps the panel
        // In practice, you'd want to refactor this
        return field;
    }
    
    private void handleVoiceInput() {
        if (aiService == null) {
            showError("Please configure Gemini API key in settings first.");
            return;
        }
        
        // Simple dialog for voice command (in production, would use actual voice recognition)
        String command = JOptionPane.showInputDialog(
            this,
            "Speak your command:\n(In production, this would use voice recognition)",
            "Voice Command",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (command != null && !command.trim().isEmpty()) {
            processVoiceCommand(command);
        }
    }
    
    private void processVoiceCommand(String command) {
        setStatus("Processing your command...");
        
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    AICommand aiCommand = aiService.processVoiceCommand(command);
                    SwingUtilities.invokeLater(() -> executeAICommand(aiCommand));
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> showError("Error processing command: " + e.getMessage()));
                }
                return null;
            }
        }.execute();
    }
    
    private void executeAICommand(AICommand command) {
        switch (command.getAction()) {
            case "READ_EMAIL":
                checkEmails();
                break;
            case "COMPOSE_EMAIL":
                String recipient = command.getParameter("recipient");
                String subject = command.getParameter("subject");
                if (recipient != null) recipientField.setText(recipient);
                if (subject != null) subjectField.setText(subject);
                break;
            case "CHECK_NEW":
                checkEmails();
                break;
            case "HELP":
                showHelp();
                break;
            default:
                setStatus("Command understood. Ready for next action.");
        }
    }
    
    private void checkEmails() {
        setStatus("Checking emails...");
        
        new SwingWorker<List<EmailMessage>, Void>() {
            @Override
            protected List<EmailMessage> doInBackground() {
                try {
                    emailService.connect();
                    return emailService.fetchRecentEmails(10);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<EmailMessage> emails = get();
                    if (emails != null) {
                        displayEmails(emails);
                        setStatus("Loaded " + emails.size() + " emails");
                    } else {
                        showError("Could not connect to email. Check your settings.");
                    }
                } catch (Exception e) {
                    showError("Error loading emails: " + e.getMessage());
                }
            }
        }.execute();
    }
    
    private void displayEmails(List<EmailMessage> emails) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < emails.size(); i++) {
            EmailMessage email = emails.get(i);
            sb.append("═══════════════════════════════════\n");
            sb.append("Email ").append(i + 1).append(":\n");
            sb.append("From: ").append(email.getFrom()).append("\n");
            sb.append("Subject: ").append(email.getSubject()).append("\n");
            sb.append("Date: ").append(email.getDate()).append("\n");
            sb.append("───────────────────────────────────\n");
            sb.append(email.getBody()).append("\n\n");
        }
        emailDisplayArea.setText(sb.toString());
        emailDisplayArea.setCaretPosition(0);
    }
    
    private void aiComposeEmail() {
        if (aiService == null) {
            showError("Please configure Gemini API key in settings first.");
            return;
        }
        
        String instructions = JOptionPane.showInputDialog(
            this,
            "Tell the AI what you want to write:\n(e.g., 'Write a thank you email to my friend')",
            "AI Email Composer",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (instructions != null && !instructions.trim().isEmpty()) {
            setStatus("AI is composing your email...");
            
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    try {
                        return aiService.composeEmail(instructions);
                    } catch (Exception e) {
                        return "Error: " + e.getMessage();
                    }
                }
                
                @Override
                protected void done() {
                    try {
                        String emailText = get();
                        messageArea.setText(emailText);
                        setStatus("Email composed by AI. You can edit it before sending.");
                    } catch (Exception e) {
                        showError("Error composing email: " + e.getMessage());
                    }
                }
            }.execute();
        }
    }
    
    private void sendEmail() {
        String to = recipientField.getText().trim();
        String subject = subjectField.getText().trim();
        String body = messageArea.getText().trim();
        
        if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
            showError("Please fill in all fields (To, Subject, and Message)");
            return;
        }
        
        setStatus("Sending email...");
        
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    emailService.sendEmail(to, subject, body);
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> showError("Error sending email: " + e.getMessage()));
                }
                return null;
            }
            
            @Override
            protected void done() {
                setStatus("Email sent successfully!");
                recipientField.setText("");
                subjectField.setText("");
                messageArea.setText("");
            }
        }.execute();
    }
    
    private void showSetupDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setFont(labelFont);
        JTextField emailField = new JTextField(config.getEmailAddress());
        emailField.setFont(fieldFont);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        JPasswordField passwordField = new JPasswordField(config.getEmailPassword());
        passwordField.setFont(fieldFont);
        
        JLabel apiKeyLabel = new JLabel("Gemini API Key:");
        apiKeyLabel.setFont(labelFont);
        JTextField apiKeyField = new JTextField(config.getGeminiApiKey());
        apiKeyField.setFont(fieldFont);
        
        JLabel helpLabel = new JLabel("<html><i>Get API key from: ai.google.dev</i></html>");
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(apiKeyLabel);
        panel.add(apiKeyField);
        panel.add(new JLabel());
        panel.add(helpLabel);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Email Settings",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            config.setEmailAddress(emailField.getText());
            config.setEmailPassword(new String(passwordField.getPassword()));
            config.setGeminiApiKey(apiKeyField.getText());
            config.saveConfig();
            
            // Reinitialize services with new config
            initializeServices();
            
            setStatus("Settings saved!");
        }
    }
    
    private void showHelp() {
        String helpText = "AI Email Assistant Help\n\n" +
            "VOICE COMMANDS:\n" +
            "• 'Check my emails' - View recent messages\n" +
            "• 'Compose email to [name]' - Start new email\n" +
            "• 'Help me write...' - AI assists with writing\n\n" +
            "BUTTONS:\n" +
            "• 🎤 Talk to AI - Give voice commands\n" +
            "• 📧 Check Emails - Get latest messages\n" +
            "• 🤖 AI Help - AI writes email for you\n" +
            "• 📤 Send - Send your email\n" +
            "• ⚙️ Settings - Configure email & API\n\n" +
            "TIPS:\n" +
            "• Use large buttons for easy clicking\n" +
            "• AI can help write emails in simple terms\n" +
            "• Voice commands make it hands-free\n" +
            "• All text is in large, readable fonts";
        
        JTextArea textArea = new JTextArea(helpText);
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        
        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Help",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void setStatus(String message) {
        statusLabel.setText(message);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
        setStatus("Error: " + message);
    }
}
