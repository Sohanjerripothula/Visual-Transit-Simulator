package edu.umn.cs.csci3081w.project.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.webserver.WebServerSession;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class VehicleTest {

  private Vehicle testVehicle;
  private Route testRouteIn;
  private Route testRouteOut;
  private Vehicle vehicle;
  private Line mockLine;
  private Route mockOutboundRoute;
  private Route mockInboundRoute;
  private Stop mockPrevStop;
  private Stop mockNextStop;


  /**
   * Setup operations before each test runs.
   */
  @BeforeEach
  public void setUp() {
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
    distancesIn.add(1.0);
    distancesIn.add(0.8443333);
    List<Double> probabilitiesIn = new ArrayList<Double>();
    probabilitiesIn.add(.025);
    probabilitiesIn.add(0.3);
    PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);

    testRouteIn = new Route(0, "testRouteIn",
        stopsIn, distancesIn, generatorIn);

    List<Stop> stopsOut = new ArrayList<Stop>();
    stopsOut.add(stop2);
    stopsOut.add(stop1);
    List<Double> distancesOut = new ArrayList<>();
    distancesOut.add(1.0);
    distancesIn.add(0.8443333);
    List<Double> probabilitiesOut = new ArrayList<Double>();
    probabilitiesOut.add(0.3);
    probabilitiesOut.add(.025);
    PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);

    testRouteOut = new Route(1, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

    testVehicle = new VehicleTestImpl(1, new Line(10000, "testLine",
        "VEHICLE_LINE", testRouteOut, testRouteIn,
        new Issue()), 3, 1.0, new PassengerLoader(), new PassengerUnloader());
  }

  /**
   * Tests constructor.
   */
  @Test
  public void testConstructor() {
    assertEquals(1, testVehicle.getId());
    assertEquals("testRouteOut1", testVehicle.getName());
    assertEquals(3, testVehicle.getCapacity());
    assertEquals(1, testVehicle.getSpeed());
    assertEquals(testRouteOut, testVehicle.getLine().getOutboundRoute());
    assertEquals(testRouteIn, testVehicle.getLine().getInboundRoute());
  }

  /**
   * Tests if testIsTripComplete function works properly.
   */
  @Test
  public void testIsTripComplete() {
    assertEquals(false, testVehicle.isTripComplete());
    testVehicle.move();
    testVehicle.move();
    testVehicle.move();
    testVehicle.move();
    assertEquals(true, testVehicle.isTripComplete());

  }


  /**
   * Tests if loadPassenger function works properly.
   */
  @Test
  public void testLoadPassenger() {

    Passenger testPassenger1 = new Passenger(3, "testPassenger1");
    Passenger testPassenger2 = new Passenger(2, "testPassenger2");
    Passenger testPassenger3 = new Passenger(1, "testPassenger3");
    Passenger testPassenger4 = new Passenger(1, "testPassenger4");

    assertEquals(1, testVehicle.loadPassenger(testPassenger1));
    assertEquals(1, testVehicle.loadPassenger(testPassenger2));
    assertEquals(1, testVehicle.loadPassenger(testPassenger3));
    assertEquals(0, testVehicle.loadPassenger(testPassenger4));
  }


  /**
   * Tests if move function works properly.
   */
  @Test
  public void testMove() {

    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());
    testVehicle.move();

    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.move();
    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.move();
    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());

    testVehicle.move();
    assertEquals(null, testVehicle.getNextStop());

  }

  /**
   * Test move with negative ratio.
   */
  @Test
  public void testMoveRatioNegative() {

    testVehicle.move();

    Route currentRoute = testVehicle.getLine().getOutboundRoute();
    double distanceBetween = currentRoute.getNextStopDistance();
    assertTrue(distanceBetween - 0.00001 >= 0);

    testVehicle.setDistanceRemaining(-5.0);
    double distanceRemaining = testVehicle.getDistanceRemaining();
    double ratio = distanceRemaining / distanceBetween;
    assertTrue(ratio < 0);

    testVehicle.move();
    assertEquals(0.0, testVehicle.getDistanceRemaining());

    Stop prevStop = testVehicle.getLine().getOutboundRoute().prevStop();
    assertEquals(prevStop.getPosition().getLongitude(), 
        testVehicle.getPosition().getLongitude(), 0.01);
    assertEquals(prevStop.getPosition().getLatitude(), 
        testVehicle.getPosition().getLatitude(), 0.01);
  }


  /**
   * Tests if update function works properly.
   */
  @Test
  public void testUpdate() {
    Passenger testPassenger1 = new Passenger(3, "testPassenger1");
    testVehicle.loadPassenger(testPassenger1);
    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());
    testVehicle.update();

    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.update();
    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.update();
    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());

    testVehicle.update();
    assertEquals(null, testVehicle.getNextStop());

  }

  /**
   * Test update with line issue.
   */
  @Test
  public void testUpdateWithIssue() {
    Line line = testVehicle.getLine();
    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    line.createIssue();
    testVehicle.update();
    assertEquals("test stop 2", testVehicle.getNextStop().getName());
  }

  /**
   * Test for update distance. 
   */
  @Test
  public void testUpdateDistance() {
    Vehicle testVehicleSpeed = new VehicleTestImpl(1, new Line(10000, "testLine",
        "VEHICLE_LINE", testRouteOut, testRouteIn,
        new Issue()), 3, -1, new PassengerLoader(), new PassengerUnloader());

    testVehicleSpeed.move();
    assertEquals(-1, testVehicleSpeed.getSpeed());
  }

  /**
   * Test to see if observer got attached.
   */
  @Test
  public void testProvideInfo() {
    WebServerSession spyWebServerSession = spy(WebServerSession.class);
    doNothing().when(spyWebServerSession).sendJson(Mockito.isA(JsonObject.class));
    
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(spyWebServerSession);


    testVehicle.setVehicleSubject(vehicleConcreteSubject);

    testVehicle.update();
    testVehicle.provideInfo();

    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(spyWebServerSession).sendJson(messageCaptor.capture());

    JsonObject capturedJson = messageCaptor.getValue();

    //JsonObject testOutput = testVehicle.getTestOutput();
    String command = capturedJson.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    String observedText = capturedJson.get("text").getAsString();
    String expectedText = "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: " + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: 0" + System.lineSeparator();
    assertEquals(expectedText, observedText);
  }

  /**
   * Test the provideInfo function with small bus.
   */
  @Test
  public void testProvideInfoSmallBus() {
    WebServerSession spyWebServerSession = spy(WebServerSession.class);
    doNothing().when(spyWebServerSession).sendJson(Mockito.isA(JsonObject.class));
    
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(spyWebServerSession);

    Vehicle testSmallBus = new SmallBus(1, new Line(10000, "testLine", "VEHICLE_LINE", 
        testRouteOut, testRouteIn, new Issue()), 20, 0.5);

    testSmallBus.setVehicleSubject(vehicleConcreteSubject);

    testSmallBus.update();
    testSmallBus.provideInfo();

    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(spyWebServerSession).sendJson(messageCaptor.capture());

    JsonObject capturedJson = messageCaptor.getValue();

    //JsonObject testOutput = testVehicle.getTestOutput();
    String command = capturedJson.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    String observedText = capturedJson.get("text").getAsString();
    String expectedText = "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: SMALL_BUS_VEHICLE" + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: 3" + System.lineSeparator();
    assertEquals(expectedText, observedText);
  }

  /**
   * Test the provideInfo function with large bus.
   */
  @Test
  public void testProvideInfoLargeBus() {
    WebServerSession spyWebServerSession = spy(WebServerSession.class);
    doNothing().when(spyWebServerSession).sendJson(Mockito.isA(JsonObject.class));
    
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(spyWebServerSession);

    Vehicle testLargeBus = new LargeBus(1, new Line(10000, "testLine", "VEHICLE_LINE", 
        testRouteOut, testRouteIn, new Issue()), 80, 0.5);

    testLargeBus.setVehicleSubject(vehicleConcreteSubject);

    testLargeBus.update();
    testLargeBus.provideInfo();

    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(spyWebServerSession).sendJson(messageCaptor.capture());

    JsonObject capturedJson = messageCaptor.getValue();

    //JsonObject testOutput = testVehicle.getTestOutput();
    String command = capturedJson.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    String observedText = capturedJson.get("text").getAsString();
    String expectedText = "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: LARGE_BUS_VEHICLE" + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: 5" + System.lineSeparator();
    assertEquals(expectedText, observedText);
  }

  /**
   * Test the provideInfo function with electric train.
   */
  @Test
  public void testProvideInfoElectricTrain() {
    WebServerSession spyWebServerSession = spy(WebServerSession.class);
    doNothing().when(spyWebServerSession).sendJson(Mockito.isA(JsonObject.class));
    
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(spyWebServerSession);

    Vehicle testElectricTrain = new ElectricTrain(1, new Line(10000, "testLine", 
        "VEHICLE_LINE", testRouteOut, testRouteIn, new Issue()), 80, 0.5);

    testElectricTrain.setVehicleSubject(vehicleConcreteSubject);

    testElectricTrain.update();
    testElectricTrain.provideInfo();

    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(spyWebServerSession).sendJson(messageCaptor.capture());

    JsonObject capturedJson = messageCaptor.getValue();

    //JsonObject testOutput = testVehicle.getTestOutput();
    String command = capturedJson.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    String observedText = capturedJson.get("text").getAsString();
    String expectedText = "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: ELECTRIC_TRAIN_VEHICLE" + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: 0" + System.lineSeparator();
    assertEquals(expectedText, observedText);
  }

  /**
   * Test the provideInfo function with diesel train.
   */
  @Test
  public void testProvideInfoDieselTrain() {
    WebServerSession spyWebServerSession = spy(WebServerSession.class);
    doNothing().when(spyWebServerSession).sendJson(Mockito.isA(JsonObject.class));
    
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(spyWebServerSession);

    Vehicle testDieselTrain = new DieselTrain(1, new Line(10000, "testLine", 
        "VEHICLE_LINE", testRouteOut, testRouteIn, new Issue()), 80, 0.5);

    testDieselTrain.setVehicleSubject(vehicleConcreteSubject);

    testDieselTrain.update();
    testDieselTrain.provideInfo();

    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(spyWebServerSession).sendJson(messageCaptor.capture());

    JsonObject capturedJson = messageCaptor.getValue();

    //JsonObject testOutput = testVehicle.getTestOutput();
    String command = capturedJson.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    String observedText = capturedJson.get("text").getAsString();
    String expectedText = "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: DIESEL_TRAIN_VEHICLE" + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: 6" + System.lineSeparator();
    assertEquals(expectedText, observedText);
  }

  /**
   * Test the provideInfo function wil multiple vehicles.
   */
  @Test
  public void testProvideInfoMultVehicle() {
    WebServerSession spyWebServerSession = spy(WebServerSession.class);
    doNothing().when(spyWebServerSession).sendJson(Mockito.isA(JsonObject.class));
    
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(spyWebServerSession);

    
    Vehicle testSmallBus = new SmallBus(1, new Line(10000, "testLine", 
        "VEHICLE_LINE", testRouteOut, testRouteIn, new Issue()), 80, 0.5);

    
    testSmallBus.setVehicleSubject(vehicleConcreteSubject);

   
    testSmallBus.update();
    Passenger testPassenger1 = new Passenger(3, "testPassenger1");
    testVehicle.loadPassenger(testPassenger1);
    testSmallBus.update();
    testSmallBus.provideInfo();


    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(spyWebServerSession).sendJson(messageCaptor.capture());

    JsonObject capturedJson = messageCaptor.getValue();

    //JsonObject testOutput = testVehicle.getTestOutput();
    String command = capturedJson.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    String observedText = capturedJson.get("text").getAsString();
    String expectedText = "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: SMALL_BUS_VEHICLE" + System.lineSeparator()
        + "* Position: (-93.239423,44.972986)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: 3, 3" + System.lineSeparator();
    assertEquals(expectedText, observedText);
  }


  /**
   * Test provide info without updating.
   */
  @Test
  public void testProvideInfoNoUpdate() {
    WebServerSession spyWebServerSession = spy(WebServerSession.class);
    doNothing().when(spyWebServerSession).sendJson(Mockito.isA(JsonObject.class));
    
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(spyWebServerSession);


    testVehicle.setVehicleSubject(vehicleConcreteSubject);

    testVehicle.provideInfo();

    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(spyWebServerSession).sendJson(messageCaptor.capture());

    JsonObject capturedJson = messageCaptor.getValue();

    //JsonObject testOutput = testVehicle.getTestOutput();
    String command = capturedJson.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    String observedText = capturedJson.get("text").getAsString();
    String expectedText = "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: " + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: " + System.lineSeparator();
    assertEquals(expectedText, observedText);
  }

  /**
   * Test getters for rgba.
   */
  @Test
  public void testRgba() {
    assertEquals(255, testVehicle.getR());
    assertEquals(255, testVehicle.getG());
    assertEquals(255, testVehicle.getB());
    assertEquals(255, testVehicle.getAlpha());
  }

  /**
   * Clean up our variables after each test.
   */
  @AfterEach
  public void cleanUpEach() {
    testVehicle = null;
  }
}
