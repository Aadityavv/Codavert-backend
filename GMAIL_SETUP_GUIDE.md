# Gmail Email Configuration Guide

The contact form now works **without email configuration**, but if you want to receive email notifications when users submit the contact form, follow these steps:

## Quick Setup (5 minutes)

### Step 1: Enable 2-Step Verification
1. Go to [Google Account Security](https://myaccount.google.com/security)
2. Under "Signing in to Google", click on **2-Step Verification**
3. Follow the prompts to enable it

### Step 2: Generate App Password
1. Go to [App Passwords](https://myaccount.google.com/apppasswords)
2. You might need to sign in again
3. In the "Select app" dropdown, choose **Mail**
4. In the "Select device" dropdown, choose **Other (Custom name)**
5. Type "Codavert Backend" and click **Generate**
6. **Copy the 16-character password** (it looks like: `abcd efgh ijkl mnop`)

### Step 3: Update application.properties
Open `codavert-backend/src/main/resources/application.properties` and update:

```properties
# Replace with your actual Gmail address
spring.mail.username=your-email@gmail.com

# Replace with the 16-character app password (no spaces)
spring.mail.password=abcdefghijklmnop

# Email recipient for contact form submissions (can be same or different)
contact.recipient.email=your-email@gmail.com
```

### Step 4: Restart the Backend
Stop and restart your Spring Boot application. The email notifications will now work!

---

## Testing Email Configuration

After setup, test by submitting the contact form. You should see in the console:

```
✅ Email notification sent successfully for: [Name]
```

Instead of:

```
⚠️  Failed to send email notification (this is okay if email is not configured)
```

---

## Alternative Email Services

### Using SendGrid, Mailgun, or other SMTP services:

```properties
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=your-sendgrid-username
spring.mail.password=your-sendgrid-api-key
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## Troubleshooting

### "Username and Password not accepted"
- Make sure you're using an **App Password**, not your regular Gmail password
- Remove any spaces from the app password
- Verify 2-Step Verification is enabled

### "Authentication failed"
- Double-check the email and app password are correct
- Try generating a new app password
- Make sure your Gmail account is active

### Still not working?
The contact form will still work and log submissions to the console. You can retrieve them from the application logs.

---

## Current Behavior (Email Not Configured)

✅ Contact form **works perfectly**  
✅ Submissions are **logged to console**  
✅ Users receive **success confirmation**  
⚠️  Email notifications are **disabled**

You can check the console logs to see all submissions:

```
INFO c.codavert.controller.ContactController : Contact form data - Name: John Doe, Email: john@example.com, Company: Acme Corp, Message: Hello...
```

---

## Need Help?

- [Gmail App Passwords Guide](https://support.google.com/accounts/answer/185833)
- [Spring Boot Email Guide](https://www.baeldung.com/spring-email)

