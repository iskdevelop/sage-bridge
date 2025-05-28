# Sage-Bridge Symbolic Backend

A language-agnostic symbolic computation backend that exposes SageMath through a REST API built with Kotlin/Ktor.

## Features

- **RESTful API** for symbolic mathematics computations
- **Multiple output formats**: raw, LaTeX, and structured JSON
- **Batch processing** support for multiple computations
- **Security controls** with execution timeouts and memory limits
- **Configurable** through properties files and environment variables
- **Language-agnostic** - use from any language that can make HTTP requests
- **Docker support** for easy deployment

## Architecture

```
┌─────────────────┐    HTTP POST    ┌──────────────────┐    Python    ┌─────────────┐
│   Client App    │ ──────────────► │  Kotlin/Ktor     │ ───────────► │  SageMath   │
│  (Any Language) │                 │    Backend       │              │   Engine    │
└─────────────────┘                 └──────────────────┘              └─────────────┘
```

The system consists of:
- **Kotlin/Ktor HTTP server** handling API requests and responses
- **Python/SageMath subprocess** for actual symbolic computation
- **Security layer** preventing dangerous operations
- **Configuration system** for customizable behavior

## Installation & Setup

### Prerequisites

- **SageMath** installed and available in PATH
- **Java 11+** for running the Kotlin application
- **Gradle** for building the project

### Local Setup

1. **Clone and build the project:**
```bash
git clone <repository-url>
cd sagemath-backend

# Build the application
./gradlew build

# Run the application
./gradlew run
```

2. **Using pre-built JAR:**
```bash
# After building
java -jar build/libs/sagemath-backend-1.0.0.jar
```

3. **With configuration:**
```bash
# Create application.properties file (see example below)
java -jar build/libs/sagemath-backend-1.0.0.jar
```

### Docker Setup

1. **Build Docker image:**
```bash
# First build the JAR
./gradlew build

# Build Docker image
docker build -t sagemath-backend .
```

2. **Run with Docker:**
```bash
docker run -p 8080:8080 sagemath-backend
```

3. **Run with custom config:**
```bash
docker run -p 8080:8080 -v $(pwd)/application.properties:/app/application.properties sagemath-backend
```

## Configuration

### Configuration Files

Create `application.properties` in your project root:

```properties
# Server Configuration
server.port=8080
server.host=0.0.0.0

# SageMath Configuration
sagemath.timeout=30
sagemath.maxmemory=512
sagemath.maxbatchsize=10

# Security Configuration
sagemath.security.enable=true
sagemath.security.blockedpatterns=system,shell,popen,subprocess
```

### Environment Variables

You can also configure using environment variables:

```bash
export SERVER_PORT=8080
export SAGEMATH_TIMEOUT=30
export SAGEMATH_SECURITY_ENABLE=true
```

## API Reference

### Base URL
```
http://localhost:8080
```

### Endpoints

#### 1. Single Computation
**POST** `/compute`

**Request Body:**
```json
{
  "code": "integrate(x^2, x)",
  "output": "json"
}
```

**Parameters:**
- `code` (string, required): SageMath expression to evaluate
- `output` (string, optional): Output format - `"raw"`, `"latex"`, or `"json"` (default)

**Response Examples:**

**Raw format** (`"output": "raw"`):
```json
{
  "result": "1/3*x^3",
  "success": true,
  "executionTimeMs": 245
}
```

**LaTeX format** (`"output": "latex"`):
```json
{
  "latex": "\\frac{1}{3} x^{3}",
  "success": true,
  "executionTimeMs": 198
}
```

**JSON format** (`"output": "json"`):
```json
{
  "result": "1/3*x^3",
  "latex": "\\frac{1}{3} x^{3}",
  "type": "symbolic",
  "success": true,
  "executionTimeMs": 187
}
```

**Error Response:**
```json
{
  "success": false,
  "error": "NameError: name 'invalid_function' is not defined"
}
```

#### 2. Batch Processing
**POST** `/batch`

**Request Body:**
```json
{
  "requests": [
    {"code": "integrate(x, x)", "output": "raw"},
    {"code": "diff(x^2, x)", "output": "latex"},
    {"code": "solve(x^2 - 1, x)", "output": "json"}
  ]
}
```

**Response:**
```json
{
  "results": [
    {"result": "1/2*x^2", "success": true, "executionTimeMs": 45},
    {"latex": "2 x", "success": true, "executionTimeMs": 38},
    {"result": "[-1, 1]", "latex": "\\left[-1, 1\\right]", "type": "symbolic", "success": true, "executionTimeMs": 52}
  ]
}
```

#### 3. Health Check
**GET** `/health`

**Response:**
```json
{
  "status": "healthy",
  "service": "sagemath-backend"
}
```

#### 4. Service Info
**GET** `/`

**Response:**
```
SageMath Symbolic Backend is running!
```

## Supported Operations

The backend supports all SageMath operations, including:

### Calculus
```json
{"code": "integrate(x^2, x)"}                    // Integration
{"code": "diff(sin(x), x)"}                     // Differentiation  
{"code": "limit(sin(x)/x, x=0)"}                // Limits
{"code": "taylor(exp(x), x, 0, 5)"}             // Series expansion
```

### Algebra
```json
{"code": "solve(x^2 - 4, x)"}                   // Equation solving
{"code": "factor(x^4 - 1)"}                     // Factorization
{"code": "expand((x+1)^3)"}                     // Expansion
{"code": "simplify(sin(x)^2 + cos(x)^2)"}       // Simplification
```

### Linear Algebra
```json
{"code": "matrix([[1,2],[3,4]]).determinant()"}              // Determinant
{"code": "matrix([[1,2],[3,4]]).inverse()"}                  // Matrix inverse
{"code": "matrix([[1,2],[3,4]]).eigenvalues()"}              // Eigenvalues
```

### Number Theory
```json
{"code": "prime_range(100)"}                    // Prime numbers
{"code": "factor(12345)"}                       // Prime factorization
{"code": "gcd(48, 18)"}                         // Greatest common divisor
```

## Client Usage Examples

### cURL
```bash
# Basic integration
curl -X POST http://localhost:8080/compute \
  -H "Content-Type: application/json" \
  -d '{"code": "integrate(x^2, x)", "output": "json"}'

# Batch processing
curl -X POST http://localhost:8080/batch \
  -H "Content-Type: application/json" \
  -d '{
    "requests": [
      {"code": "diff(sin(x), x)", "output": "latex"},
      {"code": "solve(x^2 - 4, x)", "output": "json"}
    ]
  }'
```

### Python
```python
import requests

# Initialize client
base_url = "http://localhost:8080"

# Single computation
response = requests.post(f"{base_url}/compute", json={
    "code": "integrate(x^2, x)",
    "output": "json"
})
result = response.json()
print(f"Result: {result['result']}")
print(f"LaTeX: {result['latex']}")
```

### JavaScript/Node.js
```javascript
const axios = require('axios');

async function compute(code, output = 'json') {
    try {
        const response = await axios.post('http://localhost:8080/compute', {
            code: code,
            output: output
        });
        return response.data;
    } catch (error) {
        console.error('Error:', error.response.data);
    }
}

// Usage
compute('integrate(x^3, x)').then(result => {
    console.log('Result:', result);
});
```

### Kotlin
```kotlin
// Using ktor-client
val client = HttpClient(CIO) {
    install(ContentNegotiation) { json() }
}

val response: ComputeResponse = client.post("http://localhost:8080/compute") {
    contentType(ContentType.Application.Json)
    setBody(ComputeRequest("integrate(x^2, x)", "json"))
}.body()

println("Result: ${response.result}")
```

## Security Features

### Execution Limits
- **Timeout**: 30 seconds default (configurable)
- **Memory**: 512MB limit (configurable)
- **Batch size**: Maximum 10 requests per batch

### Code Filtering
The system automatically blocks potentially dangerous operations:
- File system access (`open`, `file`)
- System commands (`system`, `shell`, `subprocess`)
- Import restrictions (`__import__`, `exec`, `eval`)
- Process control (`sys.exit`)

### Configuration
```properties
# Enable/disable security filtering
sagemath.security.enable=true

# Custom blocked patterns (comma-separated)
sagemath.security.blockedpatterns=system,shell,popen,subprocess,import os
```

## Error Handling

The API provides comprehensive error handling:

### Timeout Errors
```json
{
  "success": false,
  "error": "Computation timed out after 30s"
}
```

### Syntax Errors
```json
{
  "success": false,
  "error": "SyntaxError: invalid syntax"
}
```

### Security Violations
```json
{
  "success": false,
  "error": "Potentially unsafe operation detected"
}
```

### Invalid Input
```json
{
  "success": false,
  "error": "Code cannot be empty"
}
```

## Testing

### Run Test Suite
```bash
# Make test script executable
chmod +x test_examples.sh

# Run all tests
./test_examples.sh
```

### Manual Testing
```bash
# Start the server
./gradlew run

# In another terminal, test basic functionality
curl -X POST http://localhost:8080/compute \
  -H "Content-Type: application/json" \
  -d '{"code": "2 + 2", "output": "json"}'
```

## Deployment

### Production Considerations

1. **Resource Limits**: Adjust timeout and memory limits based on your needs
2. **Load Balancing**: The service is stateless and can be horizontally scaled
3. **Monitoring**: Use the `/health` endpoint for health checks
4. **Logging**: Configure logging levels in `application.properties`

### Docker Compose Example
```yaml
version: '3.8'
services:
  sagemath-backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SERVER_PORT=8080
      - SAGEMATH_TIMEOUT=30
    volumes:
      - ./application.properties:/app/application.properties
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

### Kubernetes Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: sagemath-backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: sagemath-backend
  template:
    metadata:
      labels:
        app: sagemath-backend
    spec:
      containers:
      - name: sagemath-backend
        image: sagemath-backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: SERVER_PORT
          value: "8080"
        - name: SAGEMATH_TIMEOUT
          value: "30"
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

## Performance

### Benchmarks
- **Simple operations** (2+2): ~50ms
- **Integration** (polynomial): ~200ms
- **Complex solving**: ~500ms-2s
- **Matrix operations**: ~100-500ms

### Optimization Tips
1. Use batch processing for multiple computations
2. Adjust timeout values based on expected computation complexity
3. Consider caching results for repeated computations
4. Monitor memory usage and adjust limits accordingly

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the GPL-3.0 License - see the LICENSE file for details.

## Support

- **Issues**: Report bugs and request features via GitHub issues
- **Documentation**: This README and inline code comments
- **Examples**: See `test_examples.sh` and client examples

## 🔮 Roadmap

- [ ] WebSocket support for real-time computation
- [ ] Session-based persistent variables
- [ ] Result caching mechanism
- [ ] GraphQL API endpoint
- [ ] Built-in plotting support (via SVG export)
- [ ] Authentication and rate limiting
- [ ] Computation history and replay
