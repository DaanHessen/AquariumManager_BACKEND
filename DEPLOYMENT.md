# Deployment Guide

## Neon PostgreSQL + Railway Deployment

This guide walks you through deploying the Aquarium Manager Backend using Neon PostgreSQL as the database and Railway for hosting.

### Step 1: Set up Neon PostgreSQL Database

1. **Create a Neon account**
   - Go to [neon.tech](https://neon.tech)
   - Sign up for a free account

2. **Create a new project**
   - Click "Create Project"
   - Choose a region close to your users
   - Note down the connection details

3. **Get your connection string**
   Your connection string will look like:
   ```
   postgres://username:password@hostname/database?sslmode=require
   ```

### Step 2: Deploy to Railway

1. **Connect to Railway**
   - Go to [railway.app](https://railway.app)
   - Sign up and connect your GitHub account
   - Create a new project from your repository

2. **Configure Environment Variables**
   In Railway, go to your project settings and add:
   ```
   DATABASE_URL=postgres://neondb_owner:npg_POpns15rGmed@ep-restless-tooth-a4ch55l0-pooler.us-east-1.aws.neon.tech/neondb?sslmode=require
   JWT_SECRET=your_secure_jwt_secret_key_here
   JWT_EXPIRATION=86400000
   HIBERNATE_HBM2DDL=update
   ```

3. **Deploy**
   - Railway will automatically detect the Dockerfile
   - The build will use the Maven wrapper to compile the application
   - The application will start on the port specified by Railway

### Step 3: Verify Deployment

1. **Check health endpoints**
   - Basic health: `https://your-app.railway.app/health`
   - Database health: `https://your-app.railway.app/api/health`

2. **Test API endpoints**
   - Root: `https://your-app.railway.app/api`
   - Documentation: See API_DOCUMENTATION.md

### Environment Variables Reference

| Variable | Description | Example |
|----------|-------------|---------|
| `DATABASE_URL` | Neon PostgreSQL connection URL | `postgres://user:pass@host/db?sslmode=require` |
| `JWT_SECRET` | Secret key for JWT token signing | `your_super_secret_key_here` |
| `JWT_EXPIRATION` | JWT token expiration time in milliseconds | `86400000` (24 hours) |
| `HIBERNATE_HBM2DDL` | Database schema management | `update` |

### Troubleshooting

#### Health Check Fails
- Check that `DATABASE_URL` is correctly set
- Verify the Neon database is accessible
- Check Railway logs for connection errors

#### Build Fails
- Ensure the Maven wrapper has execute permissions
- Check that all dependencies are available
- Verify Java 17 compatibility

#### Database Connection Issues
- Verify the Neon connection string includes `?sslmode=require`
- Check that the database credentials are correct
- Ensure the Neon database is not paused (free tier limitation)

### Production Considerations

1. **Security**
   - Use a strong, random JWT secret
   - Enable HTTPS in production
   - Consider using environment-specific secrets

2. **Database**
   - Consider upgrading to Neon Pro for better performance
   - Set up database backups
   - Monitor connection pool usage

3. **Monitoring**
   - Set up Railway monitoring
   - Configure error logging
   - Monitor API response times

### Local Development with Neon

You can also use your Neon database for local development:

```bash
export DATABASE_URL="your_neon_connection_string"
export JWT_SECRET="your_jwt_secret"
./mvnw clean package
java -jar target/dependency/webapp-runner.jar --port 8080 target/aquarium-api.war
```

This allows you to test against the same database that will be used in production. 