# Email Configuration Guide

## Overview
The contact form email feature sends you notifications when someone submits a message through your website's contact form.

## Setup Steps

### 1. Gmail Account Setup (Recommended)

If you want to use Gmail to send emails, follow these steps:

#### **A. Enable 2-Factor Authentication**
1. Go to your Google Account: https://myaccount.google.com/
2. Navigate to **Security**
3. Enable **2-Step Verification**

#### **B. Generate App Password**
1. After enabling 2FA, go to: https://myaccount.google.com/apppasswords
2. Select **Mail** as the app
3. Select **Other (Custom name)** as the device
4. Enter "Codavert Backend" as the name
5. Click **Generate**
6. Copy the 16-character password (e.g., `abcd efgh ijkl mnop`)

### 2. Configure application.properties

Update the following values in `src/main/resources/application.properties`:

```properties
# Your Gmail address
spring.mail.username=your-email@gmail.com

# The app password you generated (remove spaces)
spring.mail.password=abcdefghijklmnop

# Email address where you want to receive contact form submissions
contact.recipient.email=your-email@gmail.com
```

**Example:**
```properties
spring.mail.username=johndoe@gmail.com
spring.mail.password=xyzw abcd efgh ijkl
contact.recipient.email=johndoe@gmail.com
```

### 3. Environment Variables (Production - Recommended)

For production, use environment variables instead of hardcoding:

#### **Windows (PowerShell)**
```powershell
$env:MAIL_USERNAME="your-email@gmail.com"
$env:MAIL_PASSWORD="your-app-password"
$env:CONTACT_EMAIL="your-email@gmail.com"
```

#### **Linux/Mac (Bash)**
```bash
export MAIL_USERNAME="your-email@gmail.com"
export MAIL_PASSWORD="your-app-password"
export CONTACT_EMAIL="your-email@gmail.com"
```

#### **Docker Compose**
Update the `docker-compose.yml`:
```yaml
environment:
  SPRING_MAIL_USERNAME: your-email@gmail.com
  SPRING_MAIL_PASSWORD: your-app-password
  CONTACT_EMAIL: your-email@gmail.com
```

## Email Template Preview

When someone submits a contact form, you'll receive an email like this:

```
Subject: 🔔 New Contact Form Submission - Codavert

From: Codavert Contact Form
To: your-email@gmail.com

NEW CONTACT FORM SUBMISSION
════════════════════════════════════════

👤 Full Name: John Doe
📧 Email: john.doe@example.com
🏢 Company: Acme Corporation

💬 Message:
────────────────────────────────────────
Hi, I'm interested in your services for 
building a mobile application. Can we 
schedule a call to discuss?
────────────────────────────────────────

📅 Submitted on: Oct 27, 2025 at 03:45 PM
```

## Testing

### 1. Test the Backend Endpoint

```bash
curl -X POST https://codavert.onrender.com/api/contact/submit \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "email": "test@example.com",
    "company": "Test Company",
    "message": "This is a test message"
  }'
```

### 2. Test from Frontend

1. Navigate to your website: http://localhost:3000
2. Scroll to the Contact section
3. Fill out the form
4. Click "Send Message"
5. Check your email inbox for the notification

## Troubleshooting

### Issue: Authentication Failed

**Error:** `535 Authentication failed`

**Solution:**
- Make sure you're using an App Password, not your regular Gmail password
- Remove all spaces from the app password
- Verify 2-Factor Authentication is enabled

### Issue: Connection Timeout

**Error:** `Could not connect to SMTP host`

**Solutions:**
- Check your internet connection
- Verify firewall isn't blocking port 587
- Try using port 465 with SSL:
  ```properties
  spring.mail.port=465
  spring.mail.properties.mail.smtp.ssl.enable=true
  ```

### Issue: Email Not Received

**Solutions:**
1. Check your spam/junk folder
2. Verify the recipient email is correct in `application.properties`
3. Check backend logs for errors:
   ```bash
   # Look for email service logs
   grep -i "email" logs/spring.log
   ```

### Issue: "Less Secure Apps" Warning

**Solution:**
- Google has disabled "Less Secure Apps" access
- You MUST use App Passwords (2FA required)
- Follow the App Password generation steps above

## Using Other Email Providers

### Outlook/Hotmail
```properties
spring.mail.host=smtp.office365.com
spring.mail.port=587
spring.mail.username=your-email@outlook.com
spring.mail.password=your-password
```

### Yahoo Mail
```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
spring.mail.username=your-email@yahoo.com
spring.mail.password=your-app-password
```

### Custom SMTP Server
```properties
spring.mail.host=smtp.yourdomain.com
spring.mail.port=587
spring.mail.username=noreply@yourdomain.com
spring.mail.password=your-password
```

## API Endpoints

### Submit Contact Form
```
POST /api/contact/submit
Content-Type: application/json

{
  "fullName": "string",
  "email": "string",
  "company": "string" (optional),
  "message": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Thank you for contacting us! We'll get back to you shortly."
}
```

### Health Check
```
GET /api/contact/health

Response:
{
  "status": "UP",
  "service": "Contact Form Service"
}
```

## Security Best Practices

1. **Never commit credentials** to version control
2. **Use environment variables** in production
3. **Use App Passwords** instead of account passwords
4. **Rotate passwords** regularly
5. **Monitor email logs** for suspicious activity
6. **Set up rate limiting** to prevent spam (future enhancement)

## Features

✅ **Beautiful HTML Email Template** with gradient header
✅ **Automatic Validation** of form fields
✅ **Success/Error Messages** on frontend
✅ **Loading States** while submitting
✅ **Swagger Documentation** for API endpoints
✅ **Error Handling** with detailed logging
✅ **Plain Text Fallback** for email clients

## Next Steps

1. Configure your email credentials
2. Test the contact form
3. Customize the email template (optional)
4. Set up email monitoring/alerts
5. Consider adding rate limiting for production

## Support

If you encounter issues:
1. Check the backend logs
2. Verify email configuration
3. Test with curl first
4. Check spam folder
5. Review this guide again

---

**Important:** Replace all placeholder values (`your-email@gmail.com`, etc.) with your actual email credentials before deploying to production.

