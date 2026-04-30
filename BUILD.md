# Building ScriptsLab

This guide explains how to build ScriptsLab from source.

## Prerequisites

### Required
- **Java Development Kit (JDK)**: 17 or higher
  - Download: [Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/)
- **Maven**: 3.6.0 or higher
  - Download: [Apache Maven](https://maven.apache.org/download.cgi)
- **Git**: For cloning the repository
  - Download: [Git](https://git-scm.com/downloads)

### Recommended
- **Java 21**: For best GraalVM JavaScript performance
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions

## Quick Build

```bash
# Clone the repository
git clone https://github.com/scriptslab/scriptslab.git
cd scriptslab

# Build with Maven
mvn clean package

# The JAR will be in target/ScriptsLab-1.0.0.jar
```

## Detailed Build Steps

### 1. Clone the Repository

```bash
git clone https://github.com/scriptslab/scriptslab.git
cd scriptslab
```

### 2. Verify Java Version

```bash
java -version
```

You should see Java 17 or higher. If not, install JDK 17+ and set `JAVA_HOME`:

**Windows:**
```powershell
$env:JAVA_HOME = "C:\Path\To\JDK"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

**Linux/Mac:**
```bash
export JAVA_HOME=/path/to/jdk
export PATH=$JAVA_HOME/bin:$PATH
```

### 3. Build with Maven

**Standard build:**
```bash
mvn clean package
```

**Skip tests:**
```bash
mvn clean package -DskipTests
```

**Build with specific Java version:**
```bash
mvn clean package -Djava.version=17
```

### 4. Locate the JAR

The compiled JAR will be at:
```
target/ScriptsLab-1.0.0.jar
```

## Build Profiles

### Development Build
```bash
mvn clean package -Pdev
```
- Includes debug symbols
- Verbose logging
- No optimization

### Production Build
```bash
mvn clean package -Pprod
```
- Optimized bytecode
- Minimal logging
- Shaded dependencies

### Testing Build
```bash
mvn clean package -Ptest
```
- Runs all tests
- Generates coverage reports
- Includes test utilities

## Maven Goals

### Clean
```bash
mvn clean
```
Removes the `target/` directory.

### Compile
```bash
mvn compile
```
Compiles the source code.

### Test
```bash
mvn test
```
Runs unit tests.

### Package
```bash
mvn package
```
Creates the JAR file.

### Install
```bash
mvn install
```
Installs the JAR to your local Maven repository.

### Deploy
```bash
mvn deploy
```
Deploys the JAR to a remote repository (requires configuration).

## IDE Setup

### IntelliJ IDEA

1. **Import Project**
   - File → Open → Select `pom.xml`
   - Click "Open as Project"

2. **Set JDK**
   - File → Project Structure → Project
   - Set Project SDK to JDK 17+

3. **Build**
   - View → Tool Windows → Maven
   - Double-click `package` under Lifecycle

### Eclipse

1. **Import Project**
   - File → Import → Maven → Existing Maven Projects
   - Select the project directory

2. **Set JDK**
   - Right-click project → Properties → Java Build Path
   - Set JRE System Library to JDK 17+

3. **Build**
   - Right-click project → Run As → Maven build
   - Goals: `clean package`

### VS Code

1. **Open Project**
   - File → Open Folder → Select project directory

2. **Install Extensions**
   - Extension Pack for Java
   - Maven for Java

3. **Build**
   - Open Command Palette (Ctrl+Shift+P)
   - Run: "Maven: Execute commands"
   - Select: `clean package`

## Troubleshooting

### "Invalid target release: 17"

**Problem**: Maven is using an older Java version.

**Solution**:
```bash
# Check Maven's Java version
mvn -version

# Set JAVA_HOME to JDK 17+
export JAVA_HOME=/path/to/jdk17
```

### "Package org.graalvm.polyglot does not exist"

**Problem**: GraalVM dependencies not downloaded.

**Solution**:
```bash
# Force dependency download
mvn clean install -U
```

### "Cannot access com.scriptslab.api.module.Module"

**Problem**: Package declarations don't match file locations.

**Solution**:
```bash
# Clean and rebuild
mvn clean compile
```

### Build is Slow

**Problem**: Maven is downloading dependencies.

**Solution**:
```bash
# Use offline mode after first build
mvn clean package -o
```

### Out of Memory

**Problem**: Maven runs out of heap space.

**Solution**:
```bash
# Increase Maven memory
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"
mvn clean package
```

## Dependencies

ScriptsLab uses the following dependencies:

### Provided (by server)
- **Paper API** 1.20.4 - Minecraft server API

### Included (shaded)
- **GraalVM Polyglot** 24.0.0 - JavaScript engine
- **GraalVM JS** 24.0.0 - JavaScript language implementation

All dependencies are automatically downloaded by Maven.

## Customizing the Build

### Change Java Version

Edit `pom.xml`:
```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

### Change Paper Version

Edit `pom.xml`:
```xml
<dependency>
    <groupId>io.papermc.paper</groupId>
    <artifactId>paper-api</artifactId>
    <version>1.20.4-R0.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

### Add Dependencies

Edit `pom.xml`:
```xml
<dependencies>
    <!-- Your dependency here -->
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>example-lib</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

## Continuous Integration

### GitHub Actions

Create `.github/workflows/build.yml`:
```yaml
name: Build

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Build with Maven
        run: mvn clean package
      - name: Upload artifact
        uses: actions/upload-artifact@v3
        with:
          name: ScriptsLab
          path: target/ScriptsLab-*.jar
```

### Jenkins

Create `Jenkinsfile`:
```groovy
pipeline {
    agent any
    tools {
        maven 'Maven 3.8'
        jdk 'JDK 17'
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar'
            }
        }
    }
}
```

## Release Process

1. **Update Version**
   ```bash
   mvn versions:set -DnewVersion=1.1.0
   ```

2. **Build Release**
   ```bash
   mvn clean package -Pprod
   ```

3. **Create Tag**
   ```bash
   git tag -a v1.1.0 -m "Release 1.1.0"
   git push origin v1.1.0
   ```

4. **Upload to GitHub**
   - Go to Releases
   - Create new release
   - Upload `target/ScriptsLab-1.1.0.jar`

## Development Tips

### Fast Rebuild
```bash
# Only compile changed files
mvn compile
```

### Watch Mode
```bash
# Auto-rebuild on file changes (requires plugin)
mvn fizzed-watcher:run
```

### Debug Build
```bash
# Build with debug info
mvn clean package -Dmaven.compiler.debug=true
```

### Dependency Tree
```bash
# View all dependencies
mvn dependency:tree
```

### Update Dependencies
```bash
# Check for updates
mvn versions:display-dependency-updates
```

## Getting Help

- **Maven Issues**: [Maven Documentation](https://maven.apache.org/guides/)
- **Java Issues**: [Java Documentation](https://docs.oracle.com/en/java/)
- **Build Issues**: [GitHub Issues](https://github.com/scriptslab/scriptslab/issues)

---

**Happy Building! 🔨**
