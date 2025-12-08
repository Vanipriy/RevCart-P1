# RevCart Deployment Guide

## Pre-Deployment Checklist

### 1. Update Google OAuth Settings
Go to Google Cloud Console > APIs & Services > Credentials

**Add Production URLs:**
- Authorized JavaScript origins:
  - `https://YOUR_DOMAIN`
  - `https://YOUR_DOMAIN:443`
  
- Authorized redirect URIs:
  - `https://YOUR_DOMAIN/api/login/oauth2/code/google`

### 2. Environment Variables to Set

**Backend:**
```bash
# Database
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password
SPRING_DATASOURCE_URL=jdbc:mysql://your-db-host:3306/revcart_db

# MongoDB
MONGODB_URI=mongodb://your-mongo-host:27017/revcart_logs

# CORS (Add all your domains)
CORS_ORIGINS=https://yourdomain.com,https://www.yourdomain.com

# Frontend URL
FRONTEND_URL=https://yourdomain.com

# OAuth Redirect
OAUTH_REDIRECT_URI=https://yourdomain.com/api/login/oauth2/code/google

# Spring Profile
SPRING_PROFILES_ACTIVE=prod
```

**Frontend:**
```bash
# API URL
API_URL=https://yourdomain.com/api
```

### 3. Docker Deployment

**Update docker-compose.yml:**
```yaml
services:
  backend:
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://your-db:3306/revcart_db
      - DB_USERNAME=root
      - DB_PASSWORD=yourpassword
      - MONGODB_URI=mongodb://your-mongo:27017/revcart_logs
      - CORS_ORIGINS=https://yourdomain.com
      - FRONTEND_URL=https://yourdomain.com
      - OAUTH_REDIRECT_URI=https://yourdomain.com/api/login/oauth2/code/google
```

### 4. Build and Deploy

```bash
# Build backend
cd backend
mvn clean package -DskipTests

# Build and run with Docker
cd ..
docker-compose -f docker-compose.yml up --build -d
```

### 5. Verify Deployment

✅ Check backend health: `https://yourdomain.com/api/actuator/health`
✅ Check frontend loads: `https://yourdomain.com`
✅ Test regular login
✅ Test OAuth login
✅ Check browser console for CORS errors (should be none)

## CORS Configuration Summary

**Current Setup:**
- ✅ Backend accepts all origins with `*` pattern
- ✅ All HTTP methods allowed (GET, POST, PUT, DELETE, OPTIONS, PATCH)
- ✅ All headers allowed
- ✅ Credentials enabled
- ✅ Dynamic frontend URL from environment variable
- ✅ Dynamic OAuth redirect URL

**No CORS errors will occur if:**
1. Environment variables are set correctly
2. Google OAuth redirect URIs are updated
3. Frontend API URL points to correct backend

## Security Notes

⚠️ **For Production:**
1. Change `cors.allowed-origins` from `*` to specific domains
2. Use HTTPS for all URLs
3. Update JWT secret to a strong random value
4. Enable SSL for database connections
5. Use environment variables for all secrets
6. Never commit credentials to Git

## Troubleshooting

**CORS Error:**
- Check `CORS_ORIGINS` environment variable
- Verify frontend is using correct API URL
- Check browser console for exact error

**OAuth Error:**
- Verify redirect URI in Google Cloud Console
- Check `OAUTH_REDIRECT_URI` and `FRONTEND_URL` environment variables
- Ensure OAuth client is enabled

**Database Connection Error:**
- Verify database host and credentials
- Check if database allows remote connections
- Verify firewall rules
