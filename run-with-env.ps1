# Run Spring Boot with environment variables from .env file
# Usage: .\run-with-env.ps1

Write-Host "Starting Codavert Backend with environment variables..." -ForegroundColor Green
Write-Host ""

if (Test-Path ".env") {
    # Load environment variables from .env file
    Get-Content ".env" | ForEach-Object {
        if ($_ -match '^([^#][^=]+)=(.+)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            $value = $value -replace '^["'']|["'']$'
            
            Set-Item -Path "env:$name" -Value $value
            Write-Host "Loaded: $name" -ForegroundColor Yellow
        }
    }
    
    Write-Host ""
    Write-Host "Starting Spring Boot application..." -ForegroundColor Green
    Write-Host ""
    
    # Run Maven with the loaded environment variables
    mvn spring-boot:run
} else {
    Write-Host "Error: .env file not found!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Create a .env file in this directory with:" -ForegroundColor Yellow
    Write-Host "MAIL_USERNAME=your-email@gmail.com"
    Write-Host "MAIL_PASSWORD=your-app-password"
    Write-Host "CONTACT_EMAIL=your-email@gmail.com"
}

