package edu.umn.cs.csci3081w.project.webserver;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.Line;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class LineIssueCommandTest {

  /**
   * Test the execute function.
   */
  @Test 
  public void testExecute() {
    VisualTransitSimulator mockSimulator = mock(VisualTransitSimulator.class);
    Line mockLine = mock(Line.class);
    when(mockLine.getId()).thenReturn(1);
    List<Line> lines = new ArrayList<>();
    lines.add(mockLine);
    when(mockSimulator.getLines()).thenReturn(lines);
    LineIssueCommand lic = new LineIssueCommand(mockSimulator);
    JsonObject inputCommand = new JsonObject();
    inputCommand.addProperty("id", 1);
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    lic.execute(webServerSessionSpy, inputCommand);
    verify(mockLine).createIssue();
    verify(mockSimulator).setVehicleIssues(1);
  }
}
