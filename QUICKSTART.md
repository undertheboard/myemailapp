# Quick Start Guide

Get up and running with AI Email App in 5 minutes!

## 1. Download

```bash
git clone https://github.com/undertheboard/myemailapp.git
cd myemailapp
```

The pre-built JAR is ready in the `releases/` directory - no build needed!

## 2. Get Your API Key

1. Visit https://ai.google.dev
2. Click "Get API key in Google AI Studio"
3. Sign in and create an API key
4. Copy the key

## 3. Run the App

**Windows:**
```cmd
java -jar releases\ai-email-app-1.0.0-jar-with-dependencies.jar
```

**Mac/Linux:**
```bash
java -jar releases/ai-email-app-1.0.0-jar-with-dependencies.jar
```

## 4. First-Time Setup

When the app launches:

1. **Enter Email**: Your Optimum email address (e.g., yourname@optimum.net)
2. **Enter Password**: Your Optimum email password
3. **Enter API Key**: Paste the Gemini API key from step 2
4. **Click Save**

That's it! The app will:
- ✅ Create a desktop icon
- ✅ Configure auto-startup
- ✅ Save your credentials securely

## 5. Use the App

### Check Your Emails
Click **"📧 Check New Emails"** to see your latest messages

### Send an Email
1. Fill in **To**, **Subject**, and **Message**
2. Click **"📤 Send Email"**

### Use AI to Write Emails
1. Click **"🤖 AI Help Compose"**
2. Tell the AI what you want to write
3. Review and send!

### Voice Commands
Click **"🎤 Talk to AI Assistant"** and say:
- "Check my emails"
- "Help me write a thank you email"
- "Compose email to John"

## Common Issues

### "Java not found"
Install Java 11 or higher from https://adoptium.net

### "Cannot connect to email"
- Verify your Optimum email credentials
- Check internet connection
- Try logging into webmail first

### "AI not working"
- Verify your Gemini API key is correct
- Check quota at https://ai.google.dev

## Next Steps

- Read [INSTALLATION.md](INSTALLATION.md) for detailed setup
- Review [README.md](README.md) for all features
- Check [SUMMARY.md](SUMMARY.md) for technical details
- See [php-api/README.md](php-api/README.md) for web integration

## Need Help?

1. Check the built-in help (click **"❓ Help"** in the app)
2. Review the documentation files
3. Open an issue on GitHub

---

**That's it! You're ready to use AI Email App!** 🎉
