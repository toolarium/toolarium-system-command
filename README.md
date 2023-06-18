[![License](https://img.shields.io/github/license/toolarium/toolarium-system-command)](https://github.com/toolarium/toolarium-system-command/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.toolarium/toolarium-system-command/0.9.0)](https://search.maven.org/artifact/com.github.toolarium/toolarium-system-command/0.9.0/jar)
[![javadoc](https://javadoc.io/badge2/com.github.toolarium/toolarium-system-command/javadoc.svg)](https://javadoc.io/doc/com.github.toolarium/toolarium-system-command)

# toolarium-jwebserver

Implements a simple java library which abstract system calls.


## Built With

* [cb](https://github.com/toolarium/common-build) - The toolarium common build

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/toolarium/toolarium-system-command/tags). 


### Gradle:

```groovy
dependencies {
    implementation "com.github.toolarium:toolarium-system-command:0.9.0"
}
```

### Maven:

```xml
<dependency>
    <groupId>com.github.toolarium</groupId>
    <artifactId>toolarium-system-command</artifactId>
    <version>0.9.0</version>
</dependency>
```

Start a process synchronous:

```java
ISynchronousProcess mySyncProcess = SystemCommandExecuterFactory.builder()
      .system().command("dir")
      .build()
      .runSynchronous();
```

Start a process asynchronous and get output streams and inherit input stream which reads from the parent standard input stream if it is needed:


```java
IProcessInputStream processInputStream = ProcessStreamFactory.getInstance().getStandardIn();
ProcessBufferOutputStream output = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
ProcessBufferOutputStream errOutput = ProcessStreamFactory.getInstance().getProcessBufferOutputStream();
IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
    .system().command("dir")
    .build()
    .runAsynchronous(processInputStream, output, errOutput);
myAsyncProcess.waitFor();
```

Start a process asynchronous silently:


```java
IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
    .system().command("dir")
    .build()
    .runAsynchronous(ProcessStreamFactory.getInstance().getStandardIn(), null, null);
myAsyncProcess.waitFor();
```

Start a java process asynchronous:

```java
ProcessBufferOutputStream output = new ProcessBufferOutputStream();
ProcessBufferOutputStream errOutput = new ProcessBufferOutputStream();
IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
    .java("com.github.toolarium.system.command.TestMain")
        .inheritJre()
        .inheritClassPath()
        .environmentVariable("ENV_KEY", "env value")
        .javaMemory("256M", "1024M")
        .systemProperty("mySystemProperty", "system property value")
        .parameter("-param1").parameter("-param2=true")
    .build()
    .runAsynchronous(output, errOutput);
myAsyncProcess.waitFor();
```

Start a java process and set the classpath:

```java
ProcessBufferOutputStream output = new ProcessBufferOutputStream();
ProcessBufferOutputStream errOutput = new ProcessBufferOutputStream();
IAsynchronousProcess myAsyncProcess = SystemCommandExecuterFactory.builder()
    .java("com.github.toolarium.system.command.TestMain")
        .inheritJre()
        .classPath("build/classes/java/test")
        .classPath("build/classes/java/main") 
        .environmentVariable("ENV_KEY", "env value")
        .javaMemory("256M", "1024M")
        .systemProperty("mySystemProperty", "system property value")
        .parameter("-param1").parameter("-param2=true")
    .build()
    .runAsynchronous(output, errOutput);
myAsyncProcess.waitFor();
```
