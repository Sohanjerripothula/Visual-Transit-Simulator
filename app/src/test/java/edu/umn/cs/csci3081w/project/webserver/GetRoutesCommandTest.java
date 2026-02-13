package edu.umn.cs.csci3081w.project.webserver;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.Line;
import edu.umn.cs.csci3081w.project.model.Route;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;



public class GetRoutesCommandTest {
  
  /**
   * Test the execute funtion.
   */
  @Test
  public void testExecute() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    doNothing().when(webServerSessionSpy).sendJson(Mockito.isA(JsonObject.class));
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);

    VisualTransitSimulator mockVts = mock(VisualTransitSimulator.class);
    GetRoutesCommand grc = new GetRoutesCommand(mockVts);

    Route outboundRouteMock = mock(Route.class);
    Route inboundRouteMock = mock(Route.class);

    when(outboundRouteMock.getId()).thenReturn(1);
    when(inboundRouteMock.getId()).thenReturn(2);

    Line lineMock1 = mock(Line.class);
    when(lineMock1.getOutboundRoute()).thenReturn(outboundRouteMock);
    when(lineMock1.getInboundRoute()).thenReturn(inboundRouteMock);

    Line lineMock2 = mock(Line.class);
    when(lineMock2.getOutboundRoute()).thenReturn(outboundRouteMock);
    when(lineMock2.getInboundRoute()).thenReturn(inboundRouteMock);

    List<Line> lineList = new ArrayList<>();
    lineList.add(lineMock1);
    lineList.add(lineMock2);
    when(mockVts.getLines()).thenReturn(lineList);

    JsonObject command = new JsonObject();
    grc.execute(webServerSessionSpy, command);

    verify(webServerSessionSpy).sendJson(messageCaptor.capture());
    JsonObject capturedMessage = messageCaptor.getValue();

    assertEquals("updateRoutes", capturedMessage.get("command").getAsString());
    JsonArray routesArray = capturedMessage.getAsJsonArray("routes");
    assertEquals(4, routesArray.size());
    assertEquals(1, routesArray.get(0).getAsJsonObject().get("id").getAsInt());
    assertEquals(2, routesArray.get(1).getAsJsonObject().get("id").getAsInt());
  }

}
