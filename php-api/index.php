<?php
/**
 * AI Email App REST API
 * PHP API for web integration with the Java email application
 * Uses Google Gemini for AI functionality
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once 'config/config.php';
require_once 'includes/GeminiAI.php';
require_once 'includes/EmailClient.php';
require_once 'includes/AuthManager.php';
require_once 'includes/SessionManager.php';

// Initialize managers
$authManager = new AuthManager();
$sessionManager = new SessionManager();

// Get request details
$request_method = $_SERVER['REQUEST_METHOD'];
$request_uri = $_SERVER['REQUEST_URI'];
$path = parse_url($request_uri, PHP_URL_PATH);
$path_parts = explode('/', trim($path, '/'));

// Get the endpoint (last part of path)
$endpoint = end($path_parts);

// Get request body for POST/PUT
$input = json_decode(file_get_contents('php://input'), true);

// Initialize response
$response = [
    'success' => false,
    'message' => '',
    'data' => null
];

try {
    // Skip auth check for health endpoint
    if ($endpoint !== 'health') {
        $authManager->validateRequest();
    }
    
    switch ($endpoint) {
        case 'login':
            // Login and create session
            if ($request_method === 'POST') {
                $email = $input['email'] ?? '';
                $password = $input['password'] ?? '';
                
                if (empty($email) || empty($password)) {
                    throw new Exception('Email and password are required');
                }
                
                // Test connection
                $credentials = [
                    'email' => $email,
                    'password' => $password
                ];
                
                $emailClient = new EmailClient($credentials);
                // Try to connect to verify credentials
                try {
                    $emailClient->fetchEmails(1);
                } catch (Exception $e) {
                    throw new Exception('Invalid email credentials');
                }
                
                // Generate session token
                $sessionToken = $authManager->generateSessionToken($email);
                $sessionManager->storeSession($sessionToken, $email, $password);
                
                $response['success'] = true;
                $response['message'] = 'Login successful';
                $response['data'] = ['session_token' => $sessionToken];
            }
            break;
            
        case 'logout':
            // Logout and destroy session
            if ($request_method === 'POST') {
                $sessionToken = $input['session_token'] ?? '';
                
                if (!empty($sessionToken)) {
                    $sessionManager->deleteSession($sessionToken);
                }
                
                $response['success'] = true;
                $response['message'] = 'Logout successful';
            }
            break;
            
        case 'ai-compose':
            // AI email composition
            if ($request_method === 'POST') {
                $instructions = $input['instructions'] ?? '';
                if (empty($instructions)) {
                    throw new Exception('Instructions are required');
                }
                
                $ai = new GeminiAI(GEMINI_API_KEY);
                $emailContent = $ai->composeEmail($instructions);
                
                $response['success'] = true;
                $response['message'] = 'Email composed successfully';
                $response['data'] = ['email_content' => $emailContent];
            }
            break;
            
        case 'ai-summarize':
            // AI email summarization
            if ($request_method === 'POST') {
                $emailText = $input['email_text'] ?? '';
                if (empty($emailText)) {
                    throw new Exception('Email text is required');
                }
                
                $ai = new GeminiAI(GEMINI_API_KEY);
                $summary = $ai->summarizeEmail($emailText);
                
                $response['success'] = true;
                $response['message'] = 'Email summarized successfully';
                $response['data'] = ['summary' => $summary];
            }
            break;
            
        case 'ai-command':
            // Process voice/text command
            if ($request_method === 'POST') {
                $command = $input['command'] ?? '';
                if (empty($command)) {
                    throw new Exception('Command is required');
                }
                
                $ai = new GeminiAI(GEMINI_API_KEY);
                $result = $ai->processCommand($command);
                
                $response['success'] = true;
                $response['message'] = 'Command processed successfully';
                $response['data'] = $result;
            }
            break;
            
        case 'send-email':
            // Send email via API (with session support)
            if ($request_method === 'POST') {
                $to = $input['to'] ?? '';
                $subject = $input['subject'] ?? '';
                $body = $input['body'] ?? '';
                $sessionToken = $input['session_token'] ?? '';
                $credentials = $input['credentials'] ?? [];
                
                if (empty($to) || empty($subject) || empty($body)) {
                    throw new Exception('To, subject, and body are required');
                }
                
                // Use session if provided, otherwise use credentials
                if (!empty($sessionToken)) {
                    $session = $sessionManager->getSession($sessionToken);
                    if ($session) {
                        $credentials = [
                            'email' => $session['email'],
                            'password' => $session['password']
                        ];
                    } else {
                        throw new Exception('Invalid or expired session');
                    }
                } elseif (empty($credentials)) {
                    throw new Exception('Session token or credentials required');
                }
                
                $emailClient = new EmailClient($credentials);
                $emailClient->sendEmail($to, $subject, $body);
                
                $response['success'] = true;
                $response['message'] = 'Email sent successfully';
            }
            break;
            
        case 'fetch-emails':
            // Fetch emails via API (with session support)
            if ($request_method === 'POST') {
                $sessionToken = $input['session_token'] ?? '';
                $credentials = $input['credentials'] ?? [];
                $count = $input['count'] ?? 10;
                
                // Use session if provided, otherwise use credentials
                if (!empty($sessionToken)) {
                    $session = $sessionManager->getSession($sessionToken);
                    if ($session) {
                        $credentials = [
                            'email' => $session['email'],
                            'password' => $session['password']
                        ];
                    } else {
                        throw new Exception('Invalid or expired session');
                    }
                } elseif (empty($credentials)) {
                    throw new Exception('Session token or credentials required');
                }
                
                $emailClient = new EmailClient($credentials);
                $emails = $emailClient->fetchEmails($count);
                
                $response['success'] = true;
                $response['message'] = 'Emails fetched successfully';
                $response['data'] = ['emails' => $emails];
            }
            break;
            
        case 'health':
            // Health check endpoint
            if ($request_method === 'GET') {
                $response['success'] = true;
                $response['message'] = 'API is running';
                $response['data'] = [
                    'version' => '1.0.0',
                    'timestamp' => date('Y-m-d H:i:s')
                ];
            }
            break;
            
        default:
            throw new Exception('Invalid endpoint');
    }
} catch (Exception $e) {
    $response['success'] = false;
    $response['message'] = $e->getMessage();
    http_response_code(400);
}

// Output response
echo json_encode($response, JSON_PRETTY_PRINT);
?>
