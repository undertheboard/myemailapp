<?php
/**
 * Email Client for PHP API
 * Handles IMAP and SMTP operations for Optimum email
 */

class EmailClient {
    private $credentials;
    private $imapServer;
    private $imapPort;
    private $smtpServer;
    private $smtpPort;
    
    public function __construct($credentials) {
        $this->credentials = $credentials;
        $this->imapServer = $credentials['imap_server'] ?? DEFAULT_IMAP_SERVER;
        $this->imapPort = $credentials['imap_port'] ?? DEFAULT_IMAP_PORT;
        $this->smtpServer = $credentials['smtp_server'] ?? DEFAULT_SMTP_SERVER;
        $this->smtpPort = $credentials['smtp_port'] ?? DEFAULT_SMTP_PORT;
    }
    
    /**
     * Connect to IMAP server
     */
    private function connectIMAP() {
        $email = $this->credentials['email'] ?? '';
        $password = $this->credentials['password'] ?? '';
        
        if (empty($email) || empty($password)) {
            throw new Exception('Email credentials are required');
        }
        
        $mailbox = '{' . $this->imapServer . ':' . $this->imapPort . '/imap/ssl}INBOX';
        $connection = imap_open($mailbox, $email, $password);
        
        if (!$connection) {
            throw new Exception('Failed to connect to email server: ' . imap_last_error());
        }
        
        return $connection;
    }
    
    /**
     * Fetch recent emails
     */
    public function fetchEmails($count = 10) {
        $connection = $this->connectIMAP();
        $emails = [];
        
        $messageCount = imap_num_msg($connection);
        $start = max(1, $messageCount - $count + 1);
        
        for ($i = $messageCount; $i >= $start && $i > 0; $i--) {
            $header = imap_headerinfo($connection, $i);
            $body = imap_fetchbody($connection, $i, 1);
            
            $emails[] = [
                'from' => $header->fromaddress ?? 'Unknown',
                'subject' => $header->subject ?? 'No Subject',
                'date' => date('Y-m-d H:i:s', strtotime($header->date ?? 'now')),
                'body' => $this->decodeBody($body)
            ];
        }
        
        imap_close($connection);
        return $emails;
    }
    
    /**
     * Decode email body
     */
    private function decodeBody($body) {
        // Handle different encodings
        $decoded = quoted_printable_decode($body);
        if ($decoded === false) {
            $decoded = $body;
        }
        return substr($decoded, 0, 500); // Limit to 500 chars for preview
    }
    
    /**
     * Send email via SMTP
     */
    public function sendEmail($to, $subject, $body) {
        $email = $this->credentials['email'] ?? '';
        $password = $this->credentials['password'] ?? '';
        
        if (empty($email) || empty($password)) {
            throw new Exception('Email credentials are required');
        }
        
        // Create email headers
        $headers = [
            'From' => $email,
            'Reply-To' => $email,
            'X-Mailer' => 'PHP/' . phpversion(),
            'MIME-Version' => '1.0',
            'Content-Type' => 'text/plain; charset=UTF-8'
        ];
        
        $headerString = '';
        foreach ($headers as $key => $value) {
            $headerString .= $key . ': ' . $value . "\r\n";
        }
        
        // Use PHP's mail function with SMTP configuration
        // Note: This requires proper SMTP configuration in php.ini
        // or use a library like PHPMailer for more reliable SMTP
        
        // For demonstration, we'll use basic mail()
        // In production, use PHPMailer or similar for SMTP authentication
        $success = mail($to, $subject, $body, $headerString);
        
        if (!$success) {
            throw new Exception('Failed to send email');
        }
        
        return true;
    }
}
?>
