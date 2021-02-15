package academy.kovalevskyi.testing.model;

import academy.kovalevskyi.testing.service.ContainerHandler;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * This class is a parent of all test containers. All test containers directly or indirectly need to
 * inherit from this class. It allows you to use Testing Framework.
 */
@ExtendWith(ContainerHandler.class)
public abstract class AbstractContainer {

}
