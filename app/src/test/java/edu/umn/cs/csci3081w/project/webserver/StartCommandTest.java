

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.webserver.StartCommand;
import edu.umn.cs.csci3081w.project.webserver.VisualTransitSimulator;
import edu.umn.cs.csci3081w.project.webserver.WebServerSession;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class StartCommandTest {
  
  /**
   * Test the execute function.
   */
  @Test
  public void testExecute() {
    VisualTransitSimulator mockSimulator = mock(VisualTransitSimulator.class);
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    StartCommand startCommand = spy(new StartCommand(mockSimulator));
    doReturn(10).when(startCommand).getCurrentSimulationTime();
    JsonObject command = new JsonObject();
    command.addProperty("numTimeSteps", 50);
    JsonArray timeBetweenVehicles = new JsonArray();
    timeBetweenVehicles.add(5);
    timeBetweenVehicles.add(10);
    timeBetweenVehicles.add(15);
    command.add("timeBetweenVehicles", timeBetweenVehicles);
    startCommand.execute(webServerSessionSpy, command);
    ArgumentCaptor<List<Integer>> timeBetweenCaptor = ArgumentCaptor.forClass(ArrayList.class);
    ArgumentCaptor<Integer> numTimeStepsCaptor = ArgumentCaptor.forClass(Integer.class);
    verify(mockSimulator).setVehicleFactories(10);
    verify(mockSimulator).start(timeBetweenCaptor.capture(), numTimeStepsCaptor.capture());
    List<Integer> capturedTimeBetweenVehicles = timeBetweenCaptor.getValue();
    assertEquals(3, capturedTimeBetweenVehicles.size());
    assertEquals(5, capturedTimeBetweenVehicles.get(0));
    assertEquals(10, capturedTimeBetweenVehicles.get(1));
    assertEquals(15, capturedTimeBetweenVehicles.get(2));
    assertEquals(50, numTimeStepsCaptor.getValue());
  }

}
