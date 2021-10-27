# Testing framework
This framework makes beautiful console output for JUnit tests
## How to use:
### academy.kovalevskyi.testing.service.AbstractStdCaptor
> This class can be used for all tests that are going to intercept what will be displayed in the
> method under test
### academy.kovalevskyi.testing.annotation.Container
> Serves for marking test classes. All test classes should be annotated with this annotation
> to work with Testing Framework
### academy.kovalevskyi.testing.util.ContainerManager
> Provides all available test containers
### academy.kovalevskyi.testing.util.ContainerLauncher 
> Launches test containers


## To see test coverage
1. run test scope
2. see the report:
    - or in IDEA: `Ctrl + Alt + 6` —> there chose `target/jacoco.exec`
    - or in your favorite browser: open `target/site/jacoco/index.html`