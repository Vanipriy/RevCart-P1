# OAuth Setup for Docker Deployment

## Google Cloud Console Configuration

### 1. Go to Google Cloud Console
https://console.cloud.google.com/apis/credentials

### 2. Select Your OAuth Client (RevCart-OAuth-Final)

### 3. Update Authorized JavaScript Origins
Add these URLs:
- `http://localhost:3000`
- `http://localhost:8080`
- `http://localhost`

### 4. Update Authorized Redirect URIs
Add these URLs:
- `http://localhost:8080/api/login/oauth2/code/google`
- `http://localhost:3000/oauth2/redirect`

### 5. Click SAVE

### 6. Wait 5 minutes for changes to propagate

## Test OAuth Flow

1. Go to: `http://localhost:3000/login`
2. Click "Continue with Google"
3. Select your Google account
4. Should redirect back with token
5. Username should appear in navbar

## If OAuth Still Doesn't Work

Check:
- Browser console for errors (F12)
- Backend logs: `docker logs revcart-backend`
- Verify email is added as test user in OAuth consent screen
- Clear browser cache and cookies
- Try incognito/private window

## Current Configuration

**Frontend URL:** `http://localhost:3000`
**Backend URL:** `http://localhost:8080/api`
**OAuth Redirect:** `http://localhost:8080/api/login/oauth2/code/google`
**Frontend Redirect:** `http://localhost:3000/oauth2/redirect`
