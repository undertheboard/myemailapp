# Installation Guide - AI Email App

This guide will help you set up the AI-powered email application for elderly users.

## Prerequisites

- Java 11 or higher installed
- Optimum email account
- Google Gemini API key (get from https://ai.google.dev)

## Quick Start

### 1. Download the Application

Download the latest `ai-email-app-1.0.0-jar-with-dependencies.jar` file from the releases.

### 2. First Launch

#### Windows
1. Double-click the JAR file, or
2. Open Command Prompt and run:
   ```cmd
   java -jar ai-email-app-1.0.0-jar-with-dependencies.jar
   ```

#### macOS
1. Open Terminal and run:
   ```bash
   java -jar ai-email-app-1.0.0-jar-with-dependencies.jar
   ```
2. If you get a security warning, go to System Preferences > Security & Privacy and allow the app

#### Linux
1. Open Terminal and run:
   ```bash
   java -jar ai-email-app-1.0.0-jar-with-dependencies.jar
   ```

### 3. First-Run Setup

On first launch, the application will:

1. **Create a desktop icon** - Find "AI Email App" on your desktop
2. **Configure auto-startup** - App will start automatically when you boot your computer
3. **Show settings dialog** - Enter your information:
   - **Email Address**: Your Optimum email (e.g., yourname@optimum.net)
   - **Password**: Your Optimum email password
   - **Gemini API Key**: Your Google Gemini API key

### 4. Get Your Gemini API Key

1. Visit https://ai.google.dev
2. Click "Get API key in Google AI Studio"
3. Sign in with your Google account
4. Click "Create API Key"
5. Copy the key and paste it in the app settings

## Using the Application

### Main Features

1. **Check Emails** - Click the "📧 Check New Emails" button
2. **Compose Email** - Fill in To, Subject, and Message fields
3. **AI Help** - Click "🤖 AI Help Compose" to let AI write your email
4. **Voice Control** - Click "🎤 Talk to AI Assistant" to use voice commands
5. **Send Email** - Click "📤 Send Email" when ready to send

### Voice Commands

Say or type:
- "Check my emails"
- "Compose email to John"
- "Help me write a thank you email"
- "Read my new messages"

### Settings

Click the "⚙️ Settings" button to:
- Change your email password
- Update your API key
- Adjust font size
- Configure PHP API (optional)

## PHP API Setup (Optional for Web Integration)

If you want to integrate with a website:

### Server Requirements
- PHP 7.4 or higher
- Apache/Nginx web server
- PHP IMAP extension
- PHP cURL extension

### Installation Steps

1. Copy the `php-api` folder to your web server:
   ```bash
   cp -r php-api /var/www/html/
   ```

2. Create required directories:
   ```bash
   cd /var/www/html/php-api
   mkdir sessions logs
   chmod 700 sessions
   chmod 777 logs
   ```

3. Configure the auth secret:
   ```bash
   nano config/auth_secret.txt
   ```
   Replace the default text with a strong, random secret key (at least 32 characters)

4. Configure Gemini API:
   ```bash
   nano config/config.php
   ```
   Replace `YOUR_GEMINI_API_KEY_HERE` with your actual Gemini API key

5. Test the API:
   ```bash
   curl http://yourserver.com/php-api/health
   ```
   You should see: `{"success":true,"message":"API is running",...}`

### Connect Java App to API

1. In the Java app, click "⚙️ Settings"
2. Scroll down to "API Configuration"
3. Enter:
   - **API Base URL**: `https://yourserver.com/php-api`
   - **API Auth Secret**: (the same secret from `auth_secret.txt`)
4. Click "Save"

## Troubleshooting

### Cannot Connect to Email
- Verify your Optimum email credentials are correct
- Ensure you have internet connection
- Try logging into webmail first to verify credentials

### AI Features Don't Work
- Check that your Gemini API key is valid
- Verify you have API quota remaining at https://ai.google.dev
- Ensure internet connection is active

### Desktop Icon Not Created
- Run the app manually once to create the icon
- On Linux, you may need to mark the .desktop file as trusted
- Check Desktop folder permissions

### Auto-Startup Not Working
**Windows:**
- Check Task Manager > Startup tab
- Verify the registry entry exists

**macOS:**
- Check System Preferences > Users & Groups > Login Items
- Verify the plist file in ~/Library/LaunchAgents/

**Linux:**
- Check ~/.config/autostart/
- Verify the .desktop file exists and is executable

### App Won't Start
- Ensure Java 11+ is installed: `java -version`
- Check console for error messages
- Try running with: `java -jar ai-email-app-1.0.0-jar-with-dependencies.jar`

## Security Considerations

1. **Local Storage**: Credentials are stored in `~/.myemailapp/config.properties`
2. **File Permissions**: Config file is readable only by your user account
3. **API Secret**: Keep your API auth secret secure and never share it
4. **HTTPS**: Always use HTTPS for the PHP API in production
5. **Session Encryption**: PHP sessions use AES-256-CBC encryption

## Uninstallation

### Remove the Application

1. Delete the JAR file
2. Remove the desktop icon
3. Delete configuration: `~/.myemailapp/`

### Remove Auto-Startup

**Windows:**
```cmd
reg delete "HKCU\Software\Microsoft\Windows\CurrentVersion\Run" /v "AIEmailApp" /f
```

**macOS:**
```bash
rm ~/Library/LaunchAgents/com.myemailapp.plist
```

**Linux:**
```bash
rm ~/.config/autostart/ai-email-app.desktop
```

## Support

For issues or questions:
- Check the [README.md](README.md) for detailed documentation
- Review the [PHP API documentation](php-api/README.md) for web integration
- Open an issue on GitHub

## License

MIT License - See LICENSE file for details
