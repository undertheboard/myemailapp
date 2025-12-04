<?php
/**
 * Authentication Manager for API Authorization
 * Validates client requests using shared secret
 */

class AuthManager {
    private $secretKey;
    
    public function __construct() {
        $this->loadSecret();
    }
    
    /**
     * Load secret from text file
     */
    private function loadSecret() {
        $secretFile = __DIR__ . '/../config/auth_secret.txt';
        
        if (!file_exists($secretFile)) {
            throw new Exception('Auth secret file not found');
        }
        
        $this->secretKey = trim(file_get_contents($secretFile));
        
        if (empty($this->secretKey)) {
            throw new Exception('Auth secret is empty');
        }
    }
    
    /**
     * Validate incoming request authorization
     */
    public function validateRequest() {
        $headers = getallheaders();
        $authHeader = $headers['Authorization'] ?? $headers['authorization'] ?? '';
        
        if (empty($authHeader)) {
            http_response_code(401);
            throw new Exception('Authorization header missing');
        }
        
        // Expected format: Bearer <secret>
        if (strpos($authHeader, 'Bearer ') !== 0) {
            http_response_code(401);
            throw new Exception('Invalid authorization format');
        }
        
        $providedSecret = substr($authHeader, 7); // Remove "Bearer "
        
        if (!hash_equals($this->secretKey, $providedSecret)) {
            http_response_code(401);
            throw new Exception('Invalid authorization token');
        }
        
        return true;
    }
    
    /**
     * Generate a session token for authenticated user
     */
    public function generateSessionToken($email) {
        $data = [
            'email' => $email,
            'timestamp' => time(),
            'random' => bin2hex(random_bytes(16))
        ];
        
        $token = base64_encode(json_encode($data));
        $signature = hash_hmac('sha256', $token, $this->secretKey);
        
        return $token . '.' . $signature;
    }
    
    /**
     * Verify session token
     */
    public function verifySessionToken($sessionToken) {
        $parts = explode('.', $sessionToken);
        
        if (count($parts) !== 2) {
            return false;
        }
        
        list($token, $signature) = $parts;
        
        // Verify signature
        $expectedSignature = hash_hmac('sha256', $token, $this->secretKey);
        
        if (!hash_equals($expectedSignature, $signature)) {
            return false;
        }
        
        // Decode and verify token data
        $data = json_decode(base64_decode($token), true);
        
        if (!$data || !isset($data['email'], $data['timestamp'])) {
            return false;
        }
        
        // Check if token is not too old (30 days)
        if (time() - $data['timestamp'] > (30 * 24 * 60 * 60)) {
            return false;
        }
        
        return $data['email'];
    }
}
?>
