package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.DieselTrain;
import edu.umn.cs.csci3081w.project.model.ElectricTrain;
import edu.umn.cs.csci3081w.project.model.Issue;
import edu.umn.cs.csci3081w.project.model.LargeBus;
import edu.umn.cs.csci3081w.project.model.Line;
import edu.umn.cs.csci3081w.project.model.PassengerFactory;
import edu.umn.cs.csci3081w.project.model.PassengerGenerator;
import edu.umn.cs.csci3081w.project.model.Position;
import edu.umn.cs.csci3081w.project.model.RandomPassengerGenerator;
import edu.umn.cs.csci3081w.project.model.Route;
import edu.umn.cs.csci3081w.project.model.SmallBus;
import edu.umn.cs.csci3081w.project.model.Stop;
import edu.umn.cs.csci3081w.project.model.Vehicle;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class GetVehiclesCommandTest {

  private ElectricTrain testTrain1;
  private DieselTrain testTrain2;
  private SmallBus testBus1;
  private LargeBus testBus2;
  private List<Vehicle> vehicleList;

  /**
   * Setup operations before each test runs.
   */
  @BeforeEach
  public void setup() {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;
    List<Stop> stopsIn = new ArrayList<Stop>();
    Stop stop1 = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));
    Stop stop2 = new Stop(1, "test stop 2", new Position(-93.235071, 44.973580));
    stopsIn.add(stop1);
    stopsIn.add(stop2);
    List<Double> distancesIn = new ArrayList<>();
    distancesIn.add(0.843774422231134);
    List<Double> probabilitiesIn = new ArrayList<Double>();
    probabilitiesIn.add(.025);
    probabilitiesIn.add(0.3);
    PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);

    Route testRouteIn = new Route(0, "testRouteIn",
        stopsIn, distancesIn, generatorIn);

    List<Stop> stopsOut = new ArrayList<Stop>();
    stopsOut.add(stop2);
    stopsOut.add(stop1);
    List<Double> distancesOut = new ArrayList<>();
    distancesOut.add(0.843774422231134);
    List<Double> probabilitiesOut = new ArrayList<Double>();
    probabilitiesOut.add(0.3);
    probabilitiesOut.add(.025);
    PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);

    Route testRouteOut = new Route(1, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

    testTrain1 = new ElectricTrain(1,
        new Line(10000, "testLine", "TRAIN", testRouteOut, testRouteIn, new Issue()),
        3, 1.0);
    testTrain2 = new DieselTrain(2, 
        new Line(10000, "testLine", "TRAIN", testRouteOut, testRouteIn, 
        new Issue()), 3, 1.0);
    testBus1 = new SmallBus(3, 
        new Line(10000, "testLine", "BUS", testRouteOut, testRouteIn, 
        new Issue()), 3, 1.0);
    testBus2 = new LargeBus(4, 
        new Line(10000, "testLine", "BUS", testRouteOut, testRouteIn, 
        new Issue()), 3, 1.0);
    vehicleList = new ArrayList<Vehicle>();
    vehicleList.add(testTrain1);
    vehicleList.add(testTrain2);
    vehicleList.add(testBus2);
    vehicleList.add(testBus1);
  }

  /**
   * Test the execute function.
   */
  @Test
  public void testExecute() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    doNothing().when(webServerSessionSpy).sendJson(Mockito.isA(JsonObject.class));
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);

    VisualTransitSimulator simulator = new VisualTransitSimulator("Dummyfile.txt", 
        mock(WebServerSession.class));
    simulator.setActiveVehicles(vehicleList);

    assertTrue(simulator.getActiveVehicles().size() > 0);
    GetVehiclesCommand gvc = new GetVehiclesCommand(simulator);
    assertTrue(gvc.getVts().getActiveVehicles().size() < 8);

    JsonObject command = new JsonObject();
    gvc.execute(webServerSessionSpy, command);

    verify(webServerSessionSpy).sendJson(messageCaptor.capture());
    JsonObject capturedMessage = messageCaptor.getValue();

    assertEquals("updateVehicles", capturedMessage.get("command").getAsString());
    JsonArray vehiclesArray = capturedMessage.getAsJsonArray("vehicles");
    assertEquals(4, vehiclesArray.size());

    JsonObject vehicle1 = vehiclesArray.get(0).getAsJsonObject();
    assertEquals(1, vehicle1.get("id").getAsInt());
    assertEquals(3, vehicle1.get("capacity").getAsInt());
    assertEquals("ELECTRIC_TRAIN_VEHICLE", vehicle1.get("type").getAsString());
    assertEquals(0.0, vehicle1.get("co2").getAsDouble());
    JsonObject position1 = vehicle1.getAsJsonObject("position");
    assertEquals(-93.235071, position1.get("longitude").getAsDouble());
    assertEquals(44.973580, position1.get("latitude").getAsDouble());

    JsonObject vehicle2 = vehiclesArray.get(1).getAsJsonObject();
    assertEquals(2, vehicle2.get("id").getAsInt());
    assertEquals(3, vehicle2.get("capacity").getAsInt());
    assertEquals("DIESEL_TRAIN_VEHICLE", vehicle2.get("type").getAsString());
    assertEquals(6.0, vehicle2.get("co2").getAsDouble());
    JsonObject position2 = vehicle2.getAsJsonObject("position");
    assertEquals(-93.235071, position2.get("longitude").getAsDouble());
    assertEquals(44.973580, position2.get("latitude").getAsDouble());
  }
}