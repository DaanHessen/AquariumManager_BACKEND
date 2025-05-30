# Aquarium Manager Backend

A Java-based REST API for managing aquarium systems, built with Jakarta EE, Jersey, and Hibernate.

## Features

- **Aquarium Management**: Create, read, update, and delete aquariums
- **Inhabitant Tracking**: Manage fish and other aquatic life
- **Accessory Management**: Track filters, heaters, lights, and other equipment
- **Ornament Catalog**: Manage decorative elements
- **User Authentication**: JWT-based authentication system
- **Database Integration**: PostgreSQL with Hibernate ORM
- **Health Monitoring**: Built-in health check endpoints

## Technology Stack

- **Java 17**
- **Jakarta EE 10**
- **Jersey 3.1.3** (JAX-RS implementation)
- **Hibernate 6.2.7** (JPA implementation)
- **PostgreSQL** (Database)
- **HikariCP** (Connection pooling)
- **JWT** (Authentication)
- **Maven** (Build tool)
- **Docker** (Containerization)

## Quick Start

### Prerequisites

- Java 17 or higher
- No Maven installation required (project includes Maven wrapper)
- PostgreSQL database (local or cloud-hosted like Neon)
- Docker (optional)

### Database Setup

#### Option 1: Neon PostgreSQL (Recommended for Production)

This project is pre-configured to work with [Neon PostgreSQL](https://neon.tech), a serverless PostgreSQL service that's perfect for modern applications.

1. **Create a Neon database**
   - Sign up at [neon.tech](https://neon.tech)
   - Create a new project and database
   - Copy the connection string from the dashboard

2. **Set the DATABASE_URL environment variable**
   ```bash
   export DATABASE_URL="postgres://username:password@hostname/database?sslmode=require"
   ```

   The application automatically detects and parses this URL, including SSL configuration.

#### Option 2: Local PostgreSQL

1. **Install PostgreSQL locally**
2. **Create a database**
   ```sql
   CREATE DATABASE aquariumdb;
   ```
3. **Set environment variables**
   ```bash
   export DB_HOST=localhost
   export DB_PORT=5432
   export DB_NAME=aquariumdb
   export DB_USER=postgres
   export DB_PASSWORD=your_password
   ```

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd aquarium-manager-backend
   ```

2. **Set up environment variables**
   
   **With Neon PostgreSQL:**
   ```bash
   export DATABASE_URL="your_neon_connection_string"
   export JWT_SECRET=your_jwt_secret_key
   export HIBERNATE_HBM2DDL=update
   ```
   
   **With Local PostgreSQL:**
   ```bash
   export DB_HOST=localhost
   export DB_PORT=5432
   export DB_NAME=aquariumdb
   export DB_USER=postgres
   export DB_PASSWORD=your_password
   export JWT_SECRET=your_jwt_secret_key
   export HIBERNATE_HBM2DDL=update
   ```

3. **Build and run**
   ```bash
   ./mvnw clean package
   java -jar target/dependency/webapp-runner.jar --port 8080 target/aquarium-api.war
   ```

4. **Access the API**
   - API Base URL: `http://localhost:8080/api`
   - Health Check: `http://localhost:8080/api/status`
   - API Documentation: See `API_DOCUMENTATION.md`

## Railway Deployment

### Automatic Deployment

1. **Connect your GitHub repository to Railway**
   - Go to [Railway](https://railway.app)
   - Create a new project
   - Connect your GitHub repository

2. **Add PostgreSQL Database**
   - In your Railway project, click "New Service"
   - Select "Database" → "PostgreSQL"
   - Railway will automatically set the `DATABASE_URL` environment variable

3. **Configure Environment Variables**
   Set the following variables in Railway:
   ```
   JWT_SECRET=your_secure_jwt_secret_key_here
   JWT_EXPIRATION=86400000
   HIBERNATE_HBM2DDL=update
   ```

4. **Deploy**
   - Railway will automatically detect the Dockerfile
   - The application will build and deploy automatically
   - Access your API at the generated Railway URL

### ✅ Health Check Optimizations

This application has been optimized for Railway's health check requirements:

- **Fast Health Check Endpoint**: `/health` responds immediately without requiring database connectivity
- **Optimized Startup**: Database initialization is non-blocking to ensure quick startup
- **Railway Compatibility**: Port configuration and health checks are optimized for Railway's deployment process
- **Resilient Architecture**: Application continues startup even if database connectivity is delayed

#### Health Check Endpoints

- `/health` - **Railway Health Check**: Fast response, no database dependency (recommended for Railway)
- `/api/status` - **Full Health Check**: Includes database connectivity verification

#### Health Check Configuration

The `railway.json` file configures:
- Health check path: `/health`
- Health check timeout: 300 seconds (5 minutes)
- Restart policy: `ON_FAILURE` with max 10 retries

### Manual Deployment with Railway CLI

1. **Install Railway CLI**
   ```bash
   npm install -g @railway/cli
   ```

2. **Login and initialize**
   ```bash
   railway login
   railway init
   ```

3. **Add PostgreSQL service**
   ```bash
   railway add postgresql
   ```

4. **Set environment variables**
   ```bash
   railway variables set JWT_SECRET=your_secure_jwt_secret_key_here
   railway variables set JWT_EXPIRATION=86400000
   railway variables set HIBERNATE_HBM2DDL=update
   ```

5. **Deploy**
   ```bash
   railway up
   ```

### Testing Railway Deployment Locally

Use the provided verification script to test Railway-like deployment locally:

```bash
./verify-railway-startup.sh
```

This script will:
- Build the Docker image
- Start a container with Railway-like environment variables
- Test all health check endpoints
- Verify startup sequence and timing
- Provide a deployment readiness report

## Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DATABASE_URL` | PostgreSQL connection URL | - | Yes (auto-set by Railway) |
| `DB_HOST` | Database host | localhost | No (if DATABASE_URL is set) |
| `DB_PORT` | Database port | 5432 | No |
| `DB_NAME` | Database name | aquariumdb | No |
| `DB_USER` | Database username | postgres | No |
| `DB_PASSWORD` | Database password | postgres | No |
| `JWT_SECRET` | JWT signing secret | - | Yes |
| `JWT_EXPIRATION` | JWT expiration time (ms) | 86400000 | No |
| `HIBERNATE_HBM2DDL` | Hibernate DDL mode | update | No |
| `PORT` | Application port | 8080 | No (auto-set by Railway) |

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login user

### Aquariums
- `GET /api/aquariums` - Get all aquariums
- `POST /api/aquariums` - Create aquarium
- `GET /api/aquariums/{id}` - Get aquarium by ID
- `PUT /api/aquariums/{id}` - Update aquarium
- `DELETE /api/aquariums/{id}` - Delete aquarium

### Health Check
- `GET /api/status` - Application health status

For complete API documentation, see [API_DOCUMENTATION.md](API_DOCUMENTATION.md).

## Docker

### Build and run with Docker

```bash
# Build the image
docker build -t aquarium-api .

# Run the container
docker run -p 8080:8080 \
  -e DATABASE_URL=postgresql://user:password@host:5432/database \
  -e JWT_SECRET=your_secret \
  aquarium-api
```

### Docker Compose

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DATABASE_URL=postgresql://postgres:password@db:5432/aquariumdb
      - JWT_SECRET=your_secret_key
    depends_on:
      - db
  
  db:
    image: postgres:15
    environment:
      - POSTGRES_DB=aquariumdb
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=password
    ports:
      - "5432:5432"
```

## Development

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── nl/hu/bep/
│   │       ├── application/     # Service layer
│   │       ├── config/          # Configuration classes
│   │       ├── data/            # Repository layer
│   │       ├── domain/          # Domain entities
│   │       ├── presentation/    # REST controllers and DTOs
│   │       └── security/        # Authentication and authorization
│   ├── resources/
│   │   └── META-INF/
│   │       └── persistence.xml  # JPA configuration
│   └── webapp/
│       └── WEB-INF/
│           └── web.xml          # Web application configuration
└── test/                        # Test classes
```

### Building

```bash
# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Package WAR file
./mvnw package

# Skip tests during packaging
./mvnw package -DskipTests
```

### Database Migrations

The application uses Hibernate's automatic schema generation. Set `HIBERNATE_HBM2DDL` to:
- `create` - Drop and recreate schema
- `create-drop` - Create schema, drop on shutdown
- `update` - Update schema (recommended for development)
- `validate` - Validate schema without changes
- `none` - No automatic schema management

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify PostgreSQL is running
   - Check DATABASE_URL or individual DB_* environment variables
   - Ensure database exists and user has proper permissions

2. **Application Won't Start**
   - Check Java version (requires Java 17+)
   - Verify all required environment variables are set
   - Check application logs for specific error messages

3. **Authentication Issues**
   - Ensure JWT_SECRET is set and consistent
   - Check token expiration settings
   - Verify user registration/login endpoints

### Railway-Specific Issues

1. **Build Failures**
   - Check that Dockerfile is in the root directory
   - Verify Maven dependencies are correctly specified
   - Check Railway build logs for specific errors

2. **Database Connection Issues**
   - **CRITICAL**: Ensure PostgreSQL service is added to your Railway project
   - Verify DATABASE_URL environment variable is automatically set
   - Check that the database service is running

3. **Health Check Failures**
   - If health checks fail, first check `/health` endpoint
   - If basic health passes but `/api/status` fails, it's a database issue
   - Review application startup logs for database connection errors

#### Adding PostgreSQL to Railway

**The most common cause of health check failures is missing database service:**

1. **Via Railway Dashboard:**
   - Go to your Railway project
   - Click "New Service" or "Add Service"
   - Select "Database" → "PostgreSQL"
   - Railway will automatically set the `DATABASE_URL` environment variable

2. **Via Railway CLI:**
   ```bash
   railway add postgresql
   ```

3. **Verify Database Connection:**
   - Check your Railway project dashboard
   - Ensure PostgreSQL service shows as "Active"
   - Verify `DATABASE_URL` is listed in environment variables

#### Debugging Database Issues

1. **Check Application Logs:**
   ```bash
   railway logs
   ```
   Look for database connection errors or initialization failures.

2. **Test Basic Health Check:**
   ```
   GET https://your-app.railway.app/health
   ```
   This endpoint doesn't require database connectivity.

3. **Test Full Health Check:**
   ```
   GET https://your-app.railway.app/api/status
   ```
   This will show detailed database connection status and environment info.

4. **Common Error Messages:**
   - `"DATABASE_URL_present": false` → PostgreSQL service not added
   - `Connection refused` → Database service not running
   - `Authentication failed` → Database credentials issue
   - `Database not initialized` → Application startup failed

#### Environment Variables for Railway

Required variables (set these manually):
```
JWT_SECRET=your_secure_jwt_secret_key_here
JWT_EXPIRATION=86400000
HIBERNATE_HBM2DDL=update
```

Auto-set by Railway (when PostgreSQL service is added):
```
DATABASE_URL=postgresql://username:password@hostname:port/database
PORT=8080
```

#### Health Check Endpoints

- `/health` - **Railway Health Check**: Fast response, no database dependency (recommended for Railway)
- `/api/status` - **Full Health Check**: Includes database connectivity verification

Use `/health` for initial deployment verification, then switch to `/api/status` once database is configured.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.