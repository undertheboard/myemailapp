<?php
/**
 * Gemini AI Service for PHP API
 * Handles all AI-related functionality using Google Gemini
 */

class GeminiAI {
    private $apiKey;
    private $apiUrl;
    
    public function __construct($apiKey) {
        $this->apiKey = $apiKey;
        $this->apiUrl = GEMINI_API_URL;
    }
    
    /**
     * Make a request to Gemini API
     */
    private function makeRequest($prompt) {
        $data = [
            'contents' => [
                [
                    'parts' => [
                        ['text' => $prompt]
                    ]
                ]
            ]
        ];
        
        $ch = curl_init($this->apiUrl . '?key=' . $this->apiKey);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, [
            'Content-Type: application/json'
        ]);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
        
        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);
        
        if ($httpCode !== 200) {
            throw new Exception('Gemini API error: HTTP ' . $httpCode);
        }
        
        $result = json_decode($response, true);
        return $this->extractText($result);
    }
    
    /**
     * Extract text from Gemini API response
     */
    private function extractText($response) {
        if (isset($response['candidates'][0]['content']['parts'][0]['text'])) {
            return $response['candidates'][0]['content']['parts'][0]['text'];
        }
        return 'Error: Unable to parse AI response';
    }
    
    /**
     * Compose an email using AI
     */
    public function composeEmail($instructions) {
        $prompt = "You are helping an elderly person compose an email. " .
                 "Based on these instructions, write a clear, polite email:\n\n" .
                 $instructions . "\n\nEmail:";
        
        return $this->makeRequest($prompt);
    }
    
    /**
     * Summarize an email
     */
    public function summarizeEmail($emailText) {
        $prompt = "Summarize this email in simple terms for an elderly person:\n\n" .
                 $emailText . "\n\nSimple summary:";
        
        return $this->makeRequest($prompt);
    }
    
    /**
     * Process a voice/text command
     */
    public function processCommand($command) {
        $prompt = "You are helping an elderly person control their email app with voice commands. " .
                 "Analyze this voice command and respond with a JSON object containing:\n" .
                 "- action: one of [READ_EMAIL, COMPOSE_EMAIL, SEND_EMAIL, CHECK_NEW, HELP]\n" .
                 "- parameters: any relevant details (recipient, subject, message, etc.)\n\n" .
                 "Voice command: \"" . $command . "\"\n\n" .
                 "Respond ONLY with valid JSON, no other text.";
        
        $response = $this->makeRequest($prompt);
        
        // Extract JSON from response
        preg_match('/\{.*\}/s', $response, $matches);
        if (!empty($matches)) {
            return json_decode($matches[0], true);
        }
        
        return [
            'action' => 'HELP',
            'parameters' => []
        ];
    }
    
    /**
     * Generate a response for general queries
     */
    public function generateResponse($query) {
        return $this->makeRequest($query);
    }
}
?>
