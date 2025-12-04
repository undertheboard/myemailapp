<?php
/**
 * Session Manager for persistent email login
 * Stores email credentials securely for "stay logged in" functionality
 */

class SessionManager {
    private $sessionDir;
    
    public function __construct() {
        $this->sessionDir = __DIR__ . '/../sessions';
        
        // Create session directory if it doesn't exist
        if (!is_dir($this->sessionDir)) {
            mkdir($this->sessionDir, 0700, true);
        }
    }
    
    /**
     * Store email session
     */
    public function storeSession($sessionToken, $email, $password) {
        $sessionFile = $this->getSessionFile($sessionToken);
        
        $sessionData = [
            'email' => $email,
            'password' => $this->encrypt($password),
            'created' => time(),
            'last_access' => time()
        ];
        
        file_put_contents($sessionFile, json_encode($sessionData));
        chmod($sessionFile, 0600); // Read/write for owner only
        
        return true;
    }
    
    /**
     * Get email session
     */
    public function getSession($sessionToken) {
        $sessionFile = $this->getSessionFile($sessionToken);
        
        if (!file_exists($sessionFile)) {
            return null;
        }
        
        $sessionData = json_decode(file_get_contents($sessionFile), true);
        
        if (!$sessionData) {
            return null;
        }
        
        // Update last access time
        $sessionData['last_access'] = time();
        file_put_contents($sessionFile, json_encode($sessionData));
        
        // Decrypt password
        $sessionData['password'] = $this->decrypt($sessionData['password']);
        
        return $sessionData;
    }
    
    /**
     * Delete session (logout)
     */
    public function deleteSession($sessionToken) {
        $sessionFile = $this->getSessionFile($sessionToken);
        
        if (file_exists($sessionFile)) {
            unlink($sessionFile);
            return true;
        }
        
        return false;
    }
    
    /**
     * Clean old sessions (older than 30 days)
     */
    public function cleanOldSessions() {
        $files = glob($this->sessionDir . '/*.session');
        $cleaned = 0;
        
        foreach ($files as $file) {
            $sessionData = json_decode(file_get_contents($file), true);
            
            if ($sessionData && isset($sessionData['last_access'])) {
                // Delete if older than 30 days
                if (time() - $sessionData['last_access'] > (30 * 24 * 60 * 60)) {
                    unlink($file);
                    $cleaned++;
                }
            }
        }
        
        return $cleaned;
    }
    
    /**
     * Get session file path
     */
    private function getSessionFile($sessionToken) {
        $hash = hash('sha256', $sessionToken);
        return $this->sessionDir . '/' . $hash . '.session';
    }
    
    /**
     * Simple encryption for stored passwords
     */
    private function encrypt($data) {
        $key = $this->getEncryptionKey();
        $iv = random_bytes(16);
        $encrypted = openssl_encrypt($data, 'AES-256-CBC', $key, 0, $iv);
        return base64_encode($iv . $encrypted);
    }
    
    /**
     * Decrypt stored passwords
     */
    private function decrypt($data) {
        $key = $this->getEncryptionKey();
        $data = base64_decode($data);
        $iv = substr($data, 0, 16);
        $encrypted = substr($data, 16);
        return openssl_decrypt($encrypted, 'AES-256-CBC', $key, 0, $iv);
    }
    
    /**
     * Get encryption key (derived from auth secret)
     */
    private function getEncryptionKey() {
        $secretFile = __DIR__ . '/../config/auth_secret.txt';
        $secret = trim(file_get_contents($secretFile));
        return hash('sha256', $secret . 'encryption_salt', true);
    }
}
?>
