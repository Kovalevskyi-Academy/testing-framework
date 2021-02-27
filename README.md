# Testing framework
This framework makes beautiful console output for JUnit tests.
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