# PowerShell script to set environment variables for Spring Boot from .env file
# Usage: .\set-env.ps1

Write-Host "Setting Environment Variables for Codavert Backend..." -ForegroundColor Green

# Check if .env file exists
if (Test-Path ".env") {
    Write-Host "Reading from .env file..." -ForegroundColor Yellow
    
    # Read .env file and set variables
    Get-Content ".env" | ForEach-Object {
        if ($_ -match '^([^#][^=]+)=(.+)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            
            # Remove quotes if present
            $value = $value -replace '^["'']|["'']$'
            
            [Environment]::SetEnvironmentVariable($name, $value, "User")
            Write-Host "Set $name" -ForegroundColor Green
        }
    }
    
    Write-Host ""
    Write-Host "Environment variables set successfully!" -ForegroundColor Green
    Write-Host "Please restart your terminal or IDE for changes to take effect." -ForegroundColor Yellow
} else {
    Write-Host "Error: .env file not found!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Create a .env file with your credentials:" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "MAIL_USERNAME=your-email@gmail.com"
    Write-Host "MAIL_PASSWORD=your-16-char-app-password"
    Write-Host "CONTACT_EMAIL=your-email@gmail.com"
    Write-Host ""
    $url = "https://myaccount.google.com/apppasswords"
    Write-Host "Get Gmail App Password from: $url" -ForegroundColor Cyan
}
