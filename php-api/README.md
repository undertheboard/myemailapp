# AI Email App - PHP REST API

This is the PHP REST API component of the AI Email App that enables web integration with the Java desktop application.

## Features

- AI-powered email composition using Google Gemini
- Email summarization for easier reading
- Voice command processing
- Email sending and fetching via REST API
- CORS enabled for web integration

## Requirements

- PHP 7.4 or higher
- IMAP extension enabled
- cURL extension enabled
- Apache or Nginx web server
- Google Gemini API key

## Installation

1. Copy the `php-api` directory to your web server
2. Ensure PHP has IMAP and cURL extensions enabled:
   ```bash
   # On Ubuntu/Debian
   sudo apt-get install php-imap php-curl
   sudo systemctl restart apache2
   ```

3. Configure your API key:
   - Edit `config/config.php`
   - Replace `YOUR_GEMINI_API_KEY_HERE` with your actual Gemini API key
   - Or set environment variable: `export GEMINI_API_KEY="your-key"`

4. Configure authentication secret:
   - Edit `config/auth_secret.txt`
   - Replace the default secret with a strong, unique secret key
   - **IMPORTANT**: Keep this secret secure and match it in the Java app configuration

5. Create required directories:
   ```bash
   mkdir logs sessions
   chmod 700 sessions
   chmod 777 logs
   ```

## API Endpoints

All endpoints (except `/health`) require authentication via the `Authorization` header:
```
Authorization: Bearer YOUR_SECRET_KEY
```

### Health Check
```
GET /health
```
Returns API status and version. No authentication required.

### Login (Create Session)
```
POST /login
Authorization: Bearer YOUR_SECRET_KEY
Content-Type: application/json

{
  "email": "your@optimum.net",
  "password": "your_password"
}

Response:
{
  "success": true,
  "message": "Login successful",
  "data": {
    "session_token": "eyJlbWFpbCI6InlvdXJAb3B0aW11bS5uZXQi..."
  }
}
```

### Logout (Destroy Session)
```
POST /logout
Authorization: Bearer YOUR_SECRET_KEY
Content-Type: application/json

{
  "session_token": "your_session_token"
}
```

### AI Email Composition
```
POST /ai-compose
Content-Type: application/json

{
  "instructions": "Write a thank you email to my doctor"
}
```

### AI Email Summarization
```
POST /ai-summarize
Content-Type: application/json

{
  "email_text": "Long email content here..."
}
```

### Process AI Command
```
POST /ai-command
Content-Type: application/json

{
  "command": "Check my new emails"
}
```

### Send Email
```
POST /send-email
Authorization: Bearer YOUR_SECRET_KEY
Content-Type: application/json

{
  "to": "recipient@example.com",
  "subject": "Hello",
  "body": "Email body text",
  "session_token": "your_session_token"
}

// Or without session (less secure):
{
  "to": "recipient@example.com",
  "subject": "Hello",
  "body": "Email body text",
  "credentials": {
    "email": "your@optimum.net",
    "password": "your_password"
  }
}
```

### Fetch Emails
```
POST /fetch-emails
Authorization: Bearer YOUR_SECRET_KEY
Content-Type: application/json

{
  "count": 10,
  "session_token": "your_session_token"
}

// Or without session (less secure):
{
  "count": 10,
  "credentials": {
    "email": "your@optimum.net",
    "password": "your_password"
  }
}
```

## Usage Example

### JavaScript/AJAX
```javascript
// Login first
fetch('https://yoursite.com/php-api/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer YOUR_SECRET_KEY'
  },
  body: JSON.stringify({
    email: 'your@optimum.net',
    password: 'your_password'
  })
})
.then(response => response.json())
.then(data => {
  if (data.success) {
    const sessionToken = data.data.session_token;
    // Store sessionToken for future requests
    
    // Now compose email with AI
    return fetch('https://yoursite.com/php-api/ai-compose', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer YOUR_SECRET_KEY'
      },
      body: JSON.stringify({
        instructions: 'Write a friendly email to invite someone to lunch'
      })
    });
  }
})
.then(response => response.json())
.then(data => {
  if (data.success) {
    console.log('AI composed:', data.data.email_content);
  }
});
```

### cURL
```bash
# Health check (no auth required)
curl https://yoursite.com/php-api/health

# Login
curl -X POST https://yoursite.com/php-api/login \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_SECRET_KEY" \
  -d '{"email": "your@optimum.net", "password": "your_password"}'

# Compose email (with auth)
curl -X POST https://yoursite.com/php-api/ai-compose \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_SECRET_KEY" \
  -d '{"instructions": "Write a birthday email to my friend"}'
```

## Security Notes

1. **Auth Secret**: Keep `config/auth_secret.txt` secure and never commit to version control
2. **API Key**: Never commit your Gemini API key to version control
3. **Sessions**: Session tokens expire after 30 days of inactivity
4. **Encryption**: Passwords in sessions are encrypted using AES-256-CBC
5. **HTTPS**: Always use HTTPS in production environments
6. **Rate Limiting**: Consider implementing rate limiting in production
7. **CORS**: Adjust CORS settings in production to restrict origins
8. **File Permissions**: Ensure sessions directory is only readable by web server (chmod 700)

## Integration with Java App

The PHP API can communicate with the Java desktop application to:
- Provide web-based email composition
- Enable remote AI assistance
- Allow web dashboard for email management
- Facilitate mobile app integration

## Troubleshooting

### IMAP Connection Issues
- Verify IMAP extension is enabled: `php -m | grep imap`
- Check email server settings in `config/config.php`
- Ensure credentials are correct

### Gemini API Errors
- Verify API key is valid
- Check API quota limits
- Ensure network connectivity to Google APIs

### Permission Errors
- Ensure logs directory is writable
- Check file permissions on API files

## License

Part of the AI Email App project.
