# Cloud Email Setup Guide

## Problem with Gmail SMTP on Cloud Platforms

Cloud platforms like Render, Heroku, AWS, etc., often **block outbound SMTP connections** on ports 25, 465, and 587 for security reasons. This causes Gmail SMTP to fail with timeout errors.

## Solution: Use a Transactional Email Service

Instead of Gmail SMTP, use a dedicated transactional email service. These services are designed for cloud deployments and provide better deliverability, tracking, and reliability.

---

## Option 1: Brevo (formerly Sendinblue) - **RECOMMENDED** ✅

Brevo offers a **free tier with 300 emails/day** and works perfectly with cloud platforms.

### Step 1: Create a Brevo Account

1. Go to [https://www.brevo.com/](https://www.brevo.com/)
2. Sign up for a free account
3. Verify your email address

### Step 2: Get SMTP Credentials

1. Log in to your Brevo account
2. Go to **Settings** → **SMTP & API**
3. Click on **SMTP** tab
4. You'll see:
   - **SMTP Server**: `smtp-relay.brevo.com`
   - **Port**: `587`
   - **Login**: Your email address
   - **SMTP Key**: Click "Generate a new SMTP key" to create one

### Step 3: Update Environment Variables on Render

Go to your Render dashboard → Your service → **Environment** tab, and update:

```env
MAIL_HOST=smtp-relay.brevo.com
MAIL_PORT=587
MAIL_USERNAME=your-email@example.com
MAIL_PASSWORD=your-smtp-key-from-brevo
CONTACT_EMAIL=aadityavv9@gmail.com
```

**Important**: Use the **SMTP Key** (not your Brevo account password) for `MAIL_PASSWORD`.

### Step 4: Update application.properties (Already configured)

Your `application.properties` should use environment variables:

```properties
spring.mail.host=${MAIL_HOST:smtp.gmail.com}
spring.mail.port=${MAIL_PORT:587}
spring.mail.username=${MAIL_USERNAME:}
spring.mail.password=${MAIL_PASSWORD:}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Step 5: Verify Sender Email (Optional but Recommended)

1. In Brevo dashboard, go to **Senders & IPs** → **Senders**
2. Add and verify your sender email address (the one you'll send FROM)
3. Follow the verification process

---

## Option 2: SendGrid

SendGrid offers a **free tier with 100 emails/day**.

### Step 1: Create SendGrid Account

1. Go to [https://sendgrid.com/](https://sendgrid.com/)
2. Sign up for a free account
3. Complete the verification process

### Step 2: Create API Key

1. Go to **Settings** → **API Keys**
2. Click **Create API Key**
3. Choose **Full Access** or **Restricted Access** (with Mail Send permission)
4. Copy the API key (you won't see it again!)

### Step 3: Get SMTP Credentials

- **SMTP Server**: `smtp.sendgrid.net`
- **Port**: `587`
- **Username**: `apikey` (literally the word "apikey")
- **Password**: Your API key from Step 2

### Step 4: Update Environment Variables on Render

```env
MAIL_HOST=smtp.sendgrid.net
MAIL_PORT=587
MAIL_USERNAME=apikey
MAIL_PASSWORD=your-sendgrid-api-key
CONTACT_EMAIL=aadityavv9@gmail.com
```

---

## Option 3: Mailgun

Mailgun offers a **free trial with limited emails**.

### SMTP Credentials:

```env
MAIL_HOST=smtp.mailgun.org
MAIL_PORT=587
MAIL_USERNAME=postmaster@your-domain.mailgun.org
MAIL_PASSWORD=your-mailgun-smtp-password
CONTACT_EMAIL=aadityavv9@gmail.com
```

---

## Option 4: Disable Email Notifications (Temporary)

If you want to disable email notifications temporarily:

### On Render:

Remove or leave empty these environment variables:
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `CONTACT_EMAIL`

The backend will now **skip sending emails** but the contact form will still work and save submissions.

---

## Testing Email Configuration

After updating environment variables:

1. **Redeploy** your Render service (or it will redeploy automatically)
2. Wait for deployment to complete
3. Submit a test contact form
4. Check Render logs - you should see:
   - ✅ `Email sent successfully FROM: xxx TO: xxx` (if configured correctly)
   - ⚠️ `Email notification skipped (not configured)` (if not configured)
   - ❌ `Email sending failed` (if credentials are wrong)

---

## Troubleshooting

### "Email notification skipped (not configured)"
- Make sure you've set all three environment variables on Render
- Redeploy your service after adding variables

### "Connection timeout" or "Authentication failed"
- Double-check your SMTP credentials
- Make sure you're using the correct SMTP server and port
- Verify your sender email address in the email service dashboard

### "Sender address not verified"
- Some services require you to verify the sender email address
- Go to your email service dashboard and verify your FROM address

---

## Recommended Setup for Production

**Use Brevo** for:
- ✅ Free tier (300 emails/day)
- ✅ Easy setup
- ✅ Good deliverability
- ✅ Works with all cloud platforms
- ✅ Real-time tracking and analytics

---

## Local Development

For local development, you can continue using Gmail SMTP with app passwords. The Gmail SMTP restrictions only apply to cloud platforms like Render.

Local `.env` file:
```env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-gmail@gmail.com
MAIL_PASSWORD=your-16-char-app-password
CONTACT_EMAIL=your-email@gmail.com
```

---

## Summary

1. **Cloud platforms block Gmail SMTP** → Use transactional email service
2. **Best choice**: Brevo (free, 300 emails/day)
3. **Update environment variables** on Render with new SMTP credentials
4. **Redeploy** and test!

If you need help, check the Render logs after submitting a contact form to see detailed error messages.

