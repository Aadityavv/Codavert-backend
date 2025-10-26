# 📧 Email Configuration Guide

The contact form **works without email configuration**, but to receive email notifications, follow one of these options:

---

## ✅ Option 1: Direct Configuration (Simplest - 2 minutes)

Just replace the values in `application.properties`:

```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-char-app-password
contact.recipient.email=your-email@gmail.com
```

**Get Gmail App Password:**
1. Go to https://myaccount.google.com/apppasswords
2. Enable 2-Step Verification if not already enabled
3. Create new App Password for "Mail"
4. Copy the 16-character password (remove spaces)

Then **restart the backend** and you're done! ✅

---

## ✅ Option 2: Using .env File (Recommended for Development)

### Step 1: Create `.env` file in `codavert-backend/` folder:

```env
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-16-char-app-password
CONTACT_EMAIL=your-email@gmail.com
```

### Step 2: Set environment variables

**On Windows (PowerShell):**
```powershell
cd codavert-backend
.\set-env.ps1
```

**On Mac/Linux (Terminal):**
```bash
cd codavert-backend
export $(cat .env | xargs)
```

### Step 3: Restart your terminal/IDE

Close and reopen your terminal or IDE so it picks up the new environment variables.

### Step 4: Start the backend

```bash
mvn spring-boot:run
```

---

## ✅ Option 3: Using IntelliJ IDEA / VS Code

### IntelliJ IDEA:
1. Right-click on `CodavertBackendApplication.java`
2. Select **Run 'CodavertBackendApplication'**
3. Click **Edit Configurations...**
4. Add to **Environment variables**:
   ```
   MAIL_USERNAME=your-email@gmail.com;MAIL_PASSWORD=your-app-password;CONTACT_EMAIL=your-email@gmail.com
   ```
5. Click **Apply** and **Run**

### VS Code:
1. Open `.vscode/launch.json`
2. Add to the configuration:
   ```json
   "env": {
     "MAIL_USERNAME": "your-email@gmail.com",
     "MAIL_PASSWORD": "your-app-password",
     "CONTACT_EMAIL": "your-email@gmail.com"
   }
   ```
3. Run the application

---

## 🎯 Testing Email Configuration

After configuring, submit a test through the contact form. Check the console:

✅ **Working:**
```
INFO: Email notification sent successfully for: [Name]
```

⚠️ **Not Configured (Still works, just no email):**
```
WARN: Failed to send email notification (this is okay if email is not configured)
```

---

## 🔒 Security Best Practices

1. **Never commit `.env` file** - Add to `.gitignore`
2. **Use App Passwords** - Never use your actual Gmail password
3. **For production** - Use environment variables or secrets management
4. **Rotate passwords** - Change app password if compromised

---

## 🆘 Troubleshooting

### "Username and Password not accepted"
- ✅ Use **App Password**, not regular password
- ✅ Remove all spaces from app password
- ✅ Enable 2-Step Verification on Gmail

### "Authentication failed"
- ✅ Generate a new App Password
- ✅ Make sure email address is correct
- ✅ Check if environment variables are set: `echo $env:MAIL_USERNAME` (Windows PowerShell)

### Environment variables not working?
- ✅ Restart your terminal/IDE after setting them
- ✅ On Windows, you may need to restart your computer
- ✅ Check if they're set: `Get-ChildItem Env:` (PowerShell) or `printenv` (Mac/Linux)

---

## 🎉 Quick Start (Copy-Paste Ready)

Create `codavert-backend/.env`:

```env
MAIL_USERNAME=replace-with-your@gmail.com
MAIL_PASSWORD=replace-with-app-password
CONTACT_EMAIL=replace-with-your@gmail.com
```

Get App Password: https://myaccount.google.com/apppasswords

Run:
```powershell
cd codavert-backend
.\set-env.ps1
# Restart terminal
mvn spring-boot:run
```

Done! 🚀

---

## 📝 Note

The contact form **works perfectly without email configuration**. Submissions are logged to the console, so you won't miss any inquiries even if email isn't set up yet!

