<?php
/**
 * Configuration file for PHP API
 */

// Gemini API Configuration
define('GEMINI_API_KEY', getenv('GEMINI_API_KEY') ?: 'YOUR_GEMINI_API_KEY_HERE');
define('GEMINI_API_URL', 'https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent');

// Email Server Configuration (Optimum)
define('DEFAULT_IMAP_SERVER', 'mail.optimum.net');
define('DEFAULT_IMAP_PORT', 993);
define('DEFAULT_SMTP_SERVER', 'mail.optimum.net');
define('DEFAULT_SMTP_PORT', 465);

// API Configuration
define('API_VERSION', '1.0.0');
define('API_TIMEZONE', 'America/New_York');

// Set timezone
date_default_timezone_set(API_TIMEZONE);

// Error reporting (disable in production)
error_reporting(E_ALL);
ini_set('display_errors', 0);
ini_set('log_errors', 1);
ini_set('error_log', __DIR__ . '/../logs/error.log');
?>
