package cs428.project.gather.utilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import net.kencochrane.raven.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Raven.class)
public class SentryTests {
    @Test
    public void testSendIsCalled() {
        PowerMockito.spy(Raven.class);
        PowerMockito.verifyStatic();
        try {
            throw new Exception("Failed here");
        } catch (Throwable e) {
            Sentry.sendError(SentryTests.class, e, "Test Error Message!");
        }
    }
}
