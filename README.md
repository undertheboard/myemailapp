# AI-Powered Email App for Optimum

A simple, AI-powered email application designed specifically for elderly users. Features large fonts, voice control, and AI assistance powered by Google Gemini.

## Features

- **Simple Interface**: Large fonts, big buttons, easy-to-use design
- **AI-Powered**: Google Gemini integration for email composition and summarization
- **Voice Control**: Talk to the AI assistant to control the app
- **Optimum Email Support**: Direct login to Optimum email accounts
- **Auto-Startup**: Launches automatically when computer starts
- **Web API**: PHP REST API for web integration

## Requirements

### Java Application
- Java 11 or higher
- Maven 3.6+
- Optimum email account
- Google Gemini API key

### PHP API (Optional)
- PHP 7.4+
- Apache/Nginx web server
- PHP IMAP and cURL extensions

## Installation

### Step 1: Get API Key
1. Visit [Google AI Studio](https://ai.google.dev)
2. Create a new API key for Gemini
3. Save the key for later configuration

### Step 2: Build the Java Application
```bash
# Clone the repository
git clone https://github.com/undertheboard/myemailapp.git
cd myemailapp

# Build with Maven
mvn clean package

# The application will be in target/ai-email-app-1.0.0-jar-with-dependencies.jar
```

### Step 3: Run the Application
```bash
java -jar target/ai-email-app-1.0.0-jar-with-dependencies.jar
```

### Step 4: Configure on First Run
When you first run the application:
- A desktop icon will be created automatically
- You'll be prompted to enter:
  - Your Optimum email address
  - Your email password
  - Your Gemini API key
  - (Optional) PHP API URL and auth secret for web integration
- Your login session will be saved for future use

## Usage

### Main Features

#### Check Emails
1. Click the **"📧 Check New Emails"** button
2. Your recent emails will appear in the left panel
3. Easy to read with large fonts

#### Compose Email
1. Fill in the **To**, **Subject**, and message fields
2. Click **"📤 Send Email"** to send
3. Or use **"🤖 AI Help Compose"** for AI assistance

#### Voice Commands
1. Click **"🎤 Talk to AI Assistant"**
2. Say or type commands like:
   - "Check my emails"
   - "Compose email to John"
   - "Help me write a thank you email"

### Voice Command Examples
- **"Check my emails"** - Views recent messages
- **"Compose email to [name]"** - Starts new email
- **"Help me write..."** - AI assists with writing
- **"Read my emails"** - Displays inbox

## Configuration

Settings are stored in: `~/.myemailapp/config.properties`

You can change:
- Email address and password
- Gemini API key
- Font size (default: 18pt)
- Voice control settings

## Auto-Startup & Desktop Icon

On first launch, the app automatically:

### Creates Desktop Icon
- **Windows**: Creates `.lnk` shortcut on Desktop
- **macOS**: Creates `.command` script on Desktop  
- **Linux**: Creates `.desktop` file on Desktop

### Configures Auto-Startup
- **Windows**: Creates registry entry in `HKCU\Software\Microsoft\Windows\CurrentVersion\Run`
- **macOS**: Creates LaunchAgent plist in `~/Library/LaunchAgents/`
- **Linux**: Creates autostart .desktop file in `~/.config/autostart/`

### Stay Logged In
- Email credentials are securely stored locally
- Automatic reconnection on app restart
- Sessions persist for 30 days

## PHP API Setup (Optional)

For web integration, deploy the PHP API:

1. Copy `php-api/` to your web server
2. Edit `php-api/config/config.php` with your Gemini API key
3. Edit `php-api/config/auth_secret.txt` with a strong secret key
4. Create `sessions/` directory with restrictive permissions (chmod 700)
5. Ensure PHP IMAP extension is enabled
6. In Java app settings, configure:
   - API Base URL: `https://yoursite.com/php-api`
   - API Auth Secret: (match the secret in `auth_secret.txt`)

See [php-api/README.md](php-api/README.md) for detailed API documentation.

### Client-Server Authentication
- Java app authenticates using shared secret from `auth_secret.txt`
- All API requests include `Authorization: Bearer SECRET` header
- Sessions persist login state for 30 days
- Credentials encrypted in session storage

## Troubleshooting

### Cannot Connect to Email
- Verify your Optimum email credentials
- Check internet connection
- Ensure IMAP access is enabled for your account

### AI Not Working
- Verify your Gemini API key is correct
- Check API quota limits at [Google AI Studio](https://ai.google.dev)
- Ensure internet connection is active

### App Doesn't Start on Boot
- **Windows**: Check Task Manager > Startup tab
- **macOS**: Check System Preferences > Users & Groups > Login Items
- **Linux**: Check ~/.config/autostart/

### Large Text Issues
- Adjust font size in Settings (⚙️ button)
- Default is 18pt for readability

## Architecture

### Java Application
```
src/main/java/com/myemailapp/
├── Main.java              - Entry point
├── ai/                    - Gemini AI integration
│   ├── GeminiAIService.java
│   └── AICommand.java
├── email/                 - Email service (IMAP/SMTP)
│   ├── EmailService.java
│   └── EmailMessage.java
├── gui/                   - User interface
│   └── EmailAppGUI.java
├── voice/                 - Voice control
│   └── VoiceService.java
├── startup/               - Auto-startup manager
│   └── StartupManager.java
└── config/                - Configuration management
    └── AppConfig.java
```

### PHP API
```
php-api/
├── index.php              - Main API router
├── config/
│   └── config.php         - Configuration
└── includes/
    ├── GeminiAI.php       - AI service
    └── EmailClient.php    - Email operations
```

## Security Considerations

1. **Credentials**: Stored locally in user's home directory
2. **API Keys**: Never commit to version control
3. **HTTPS**: Use HTTPS for PHP API in production
4. **Encryption**: Consider encrypting stored passwords
5. **Updates**: Keep dependencies updated

## Development

### Building
```bash
mvn clean package
```

### Testing
```bash
mvn test
```

### Running in Development
```bash
mvn exec:java -Dexec.mainClass="com.myemailapp.Main"
```

## Contributing

Contributions welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

MIT License - See LICENSE file for details

## Support

For issues or questions:
- Open an issue on GitHub
- Check troubleshooting section above
- Review API documentation

## Credits

- Built with Java Swing
- AI powered by Google Gemini
- Email via JavaMail API
- PHP API for web integration

---

**Note**: This application is designed specifically for elderly users with:
- Large, readable fonts (18pt+)
- Simple, intuitive interface
- Big buttons for easy clicking
- Voice control for hands-free operation
- AI assistance for composing emails
- Clear instructions and help text