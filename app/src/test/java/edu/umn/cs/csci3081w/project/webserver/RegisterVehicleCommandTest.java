package edu.umn.cs.csci3081w.project.webserver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.Vehicle;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RegisterVehicleCommandTest {
  
  /**
   * Test the execute function.
   */
  @Test
  public void testExecute() {
    VisualTransitSimulator mockSimulator = mock(VisualTransitSimulator.class);
    Vehicle mockVehicle = mock(Vehicle.class);
    when(mockVehicle.getId()).thenReturn(1);
    Vehicle mockDV = mock(Vehicle.class);
    when(mockVehicle.getDV()).thenReturn(mockDV);
    List<Vehicle> activeVehicles = new ArrayList<>();
    activeVehicles.add(mockVehicle);
    when(mockSimulator.getActiveVehicles()).thenReturn(activeVehicles);
    RegisterVehicleCommand command = new RegisterVehicleCommand(mockSimulator);
    JsonObject inputCommand = new JsonObject();
    inputCommand.addProperty("id", 1);
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    command.execute(webServerSessionSpy, inputCommand);
    verify(mockSimulator).addObserver(mockDV);
  }
}
