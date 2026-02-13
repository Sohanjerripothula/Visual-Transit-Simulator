package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

import edu.umn.cs.csci3081w.project.model.ElectricTrain;
import edu.umn.cs.csci3081w.project.model.Issue;
import edu.umn.cs.csci3081w.project.model.IssueDecorator;
import edu.umn.cs.csci3081w.project.model.Line;
import edu.umn.cs.csci3081w.project.model.PassengerFactory;
import edu.umn.cs.csci3081w.project.model.PassengerGenerator;
import edu.umn.cs.csci3081w.project.model.Position;
import edu.umn.cs.csci3081w.project.model.RandomPassengerGenerator;
import edu.umn.cs.csci3081w.project.model.Route;
import edu.umn.cs.csci3081w.project.model.SmallBus;
import edu.umn.cs.csci3081w.project.model.Stop;
import edu.umn.cs.csci3081w.project.model.Vehicle;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class VisualTransitSimulatorTest {
  private VisualTransitSimulator simulator;
  private WebServerSession mockSession;
  private Line testLine;
  private Stop testStop;

  /**
   * Setup operations before each test runs.
   */
  @BeforeEach
  public void setup() {
    mockSession = Mockito.mock(WebServerSession.class);
    simulator = new VisualTransitSimulator("testConfig.json", mockSession);
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;
    testStop = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));

    List<Stop> stopsOut = new ArrayList<Stop>();
    stopsOut.add(testStop);
    List<Double> distancesOut = new ArrayList<Double>();
    distancesOut.add(0.9712663713083954);
    List<Double> probabilitiesOut = new ArrayList<Double>();
    probabilitiesOut.add(.15);
    PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);
    Route testRouteOut = new Route(10, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

    List<Stop> stopsIn = new ArrayList<>();
    stopsIn.add(testStop);
    List<Double> distancesIn;
    distancesIn = new ArrayList<>();
    distancesIn.add(0.961379387775189);
    List<Double> probabilitiesIn = new ArrayList<>();
    probabilitiesIn.add(.4);
    PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);
    Route testRouteIn = new Route(11, "testRouteIn",
        stopsIn, distancesIn, generatorIn);


    testLine = new Line(0, "testLine", Line.BUS_LINE,
        testRouteOut, testRouteIn,
        new Issue());  
  }

  /**
   * Test the initalization.
   */
  @Test
  public void testInitialization() {
    assertNotNull(simulator.getLines(), "Lines should be initialized.");
    assertNotNull(simulator.getActiveVehicles(), "Active vehicles should be initialized.");

  }

  /** 
   * Test the logging.
   */ 
  @Test
  public void testLogging() {
    try {
      final Charset charset = StandardCharsets.UTF_8;
      simulator.setLogging(true);
      // Create a ByteArrayOutputStream to capture the output
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream testStream = new PrintStream(outputStream);
      testLine.report(testStream);
      outputStream.flush();
      String data = new String(outputStream.toByteArray(), charset);
      testStream.close();
      outputStream.close();

      String strToCompare =
              "====Line Info Start====" + System.lineSeparator()
              + "ID: 0" + System.lineSeparator()
              + "Name: testLine" + System.lineSeparator()
              + "Type: BUS_LINE" + System.lineSeparator()

              // outbound route report
              + "####Route Info Start####" + System.lineSeparator()
              + "ID: 10" + System.lineSeparator()
              + "Name: testRouteOut" + System.lineSeparator()
              + "Num stops: 1" + System.lineSeparator()
              + "****Stops Info Start****" + System.lineSeparator()
              + "++++Next Stop Info Start++++" + System.lineSeparator()
              + "####Stop Info Start####" + System.lineSeparator()
              + "ID: 0" + System.lineSeparator()
              + "Name: test stop 1" + System.lineSeparator()
              + "Position: 44.972392,-93.243774" + System.lineSeparator()
              + "****Passengers Info Start****" + System.lineSeparator()
              + "Num passengers waiting: 0" + System.lineSeparator()
              + "****Passengers Info End****" + System.lineSeparator()
              + "####Stop Info End####" + System.lineSeparator()
              + "++++Next Stop Info End++++" + System.lineSeparator()
              + "****Stops Info End****" + System.lineSeparator()
              + "####Route Info End####" + System.lineSeparator()
              + "####Route Info Start####" + System.lineSeparator()
              + "ID: 11" + System.lineSeparator()
              + "Name: testRouteIn" + System.lineSeparator()
              + "Num stops: 1" + System.lineSeparator()
              + "****Stops Info Start****" + System.lineSeparator()
              + "++++Next Stop Info Start++++" + System.lineSeparator()
              + "####Stop Info Start####" + System.lineSeparator()
              + "ID: 0" + System.lineSeparator()
              + "Name: test stop 1" + System.lineSeparator()
              + "Position: 44.972392,-93.243774" + System.lineSeparator()
              + "****Passengers Info Start****" + System.lineSeparator()
              + "Num passengers waiting: 0" + System.lineSeparator()
              + "****Passengers Info End****" + System.lineSeparator()
              + "####Stop Info End####" + System.lineSeparator()
              + "++++Next Stop Info End++++" + System.lineSeparator()
              + "****Stops Info End****" + System.lineSeparator()
              + "####Route Info End####" + System.lineSeparator()
              + "====Line Info End====" + System.lineSeparator();

      // Assert the actual output matches the expected output
      assertEquals(data, strToCompare);
    } catch (IOException ioe) {
      fail();
    }

  }

  /**
   * Test the setting of Vehile Factories.
   */
  @Test
  public void testSetVehicleFactories() throws Exception {
    simulator.setVehicleFactories(10);


    Field busFactoryField = VisualTransitSimulator.class.getDeclaredField("busFactory");
    busFactoryField.setAccessible(true);
    Object busFactory;
    busFactory = busFactoryField.get(simulator);

    Field trainFactoryField = VisualTransitSimulator.class.getDeclaredField("trainFactory");
    trainFactoryField.setAccessible(true);
    Object trainFactory = trainFactoryField.get(simulator);

    assertNotNull(busFactory, "BusFactory should be initialized.");
    assertNotNull(trainFactory, "TrainFactory should be initialized.");
  }

  /**
   * Test the start method.
   */
  @Test
  public void testStartMethod() {
    List<Integer> vehicleStartTimings = Arrays.asList(5, 10, 15);
    int numTimeSteps = 50;
    simulator.start(vehicleStartTimings, numTimeSteps);
    assertEquals(vehicleStartTimings, simulator.getVehicleStartTimings());
    assertEquals(numTimeSteps, simulator.getNumTimeSteps());
    assertEquals(Arrays.asList(0, 0, 0), simulator.getTimeSinceLastVehicle());
    assertEquals(0, simulator.getSimulationTimeElapsed());
  }

  /**
   * Test the togglePause function.
   */
  @Test
  public void testTogglePause() {
    assertFalse(simulator.isPaused());
    simulator.togglePause();
    assertTrue(simulator.isPaused());
    simulator.togglePause();
    assertFalse(simulator.isPaused());
  }

  /**
   * Test the update funciton when simulator is paused.
   */
  @Test
  public void testPausedUpdate() {
    assertFalse(simulator.isPaused());
    simulator.togglePause();
    simulator.update();
    assertEquals(0, simulator.getSimulationTimeElapsed());
  }

  /**
   * Test the time increment when the simulator is updated.
   */
  @Test
  public void testTimeIncrement() {
    assertFalse(simulator.isPaused());
    simulator.update();
    assertEquals(1, simulator.getSimulationTimeElapsed());
  }
  
  /**
   * Test the update function.
   */
  @Test
  public void testUpdate() {
    try {
      simulator = new VisualTransitSimulator(
          URLDecoder.decode(getClass().getClassLoader()
              .getResource("config.txt").getFile(), "UTF-8"), mockSession);

      assertNotNull(simulator.getLines());

      List<Line> lines = simulator.getLines();

      assertEquals(2, lines.size());

      assertEquals(0, simulator.getSimulationTimeElapsed());
      simulator.setVehicleFactories(LocalDateTime.now().getHour());

      List<Integer> timings = new ArrayList<>();
      timings.add(2);
      timings.add(2);

      assertEquals(timings.size(), lines.size());

      simulator.start(timings, 50);
      simulator.update();
      assertEquals(1, simulator.getSimulationTimeElapsed());

      for (int i = 0; i < 49; i++) {
        if (i == 48) {
          simulator.setLogging(false);
          simulator.getLines().get(0).createIssue();
        }
        simulator.update();
      }
    } catch (Exception e) {
      fail();
    }
  }

  /**
   * Test the setVehicleIssue function.
   */
  @Test
  public void testSetVehicleIssue() {
    Stop stop1 = new Stop(1, "Stop 1", new Position(-93.243774, 44.972392));
    Stop stop2 = new Stop(2, "Stop 2", new Position(-93.250000, 44.980000));
    List<Stop> stopsOut = new ArrayList<>(Arrays.asList(stop1, stop2));
    List<Double> distancesOut = new ArrayList<>(Arrays.asList(0.5, 0.5));
    List<Double> probabilitiesOut = new ArrayList<>(Arrays.asList(0.1, 0.1));
    PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);
    Route routeOut = new Route(1, "RouteOut", stopsOut, distancesOut, generatorOut);

    List<Stop> stopsIn = new ArrayList<>(Arrays.asList(stop2, stop1));
    List<Double> distancesIn = new ArrayList<>(Arrays.asList(0.5, 0.5));
    List<Double> probabilitiesIn = new ArrayList<>(Arrays.asList(0.1, 0.1));
    PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);
    Route routeIn = new Route(2, "RouteIn", stopsIn, distancesIn, generatorIn);

    Line lineWithIssue = new Line(1, "Line With Issue", 
        Line.BUS_LINE, routeOut, routeIn, new Issue());
    lineWithIssue.createIssue();
    Line lineWithoutIssue = new Line(2, "Line Without Issue", 
        Line.TRAIN_LINE, routeOut, routeIn, new Issue());
    
    assertTrue(lineWithIssue.isIssueExist());

    List<Line> lines = new ArrayList<>();
    lines.add(lineWithIssue);
    lines.add(lineWithoutIssue);
    simulator.setLines(lines);
    assertEquals(2, simulator.getLines().size());

    Vehicle vehicleWithIssue = new SmallBus(1, lineWithIssue, 20, 1.0); 
    Vehicle vehicleWithoutIssue = new ElectricTrain(2, lineWithoutIssue, 30, 1.5);

    List<Vehicle> activeVehicles = new ArrayList<>();
    activeVehicles.add(vehicleWithIssue); 
    activeVehicles.add(vehicleWithoutIssue); 
    simulator.setActiveVehicles(activeVehicles);
    assertEquals(2, simulator.getActiveVehicles().size());

    simulator.setVehicleIssues(1);

    assertTrue(simulator.getActiveVehicles().get(0) instanceof IssueDecorator); 
    assertSame(vehicleWithoutIssue, simulator.getActiveVehicles().get(1));     
  }

  /**
   * Test the addObserver function.
   */
  @Test
  public void testAddObserver() {
    Vehicle vehicle = mock(Vehicle.class);
    simulator.addObserver(vehicle);
    assertTrue(simulator.getSubject().getObservers().size() > 0);
  }
  
  /**
   * Clean up our variables after each test.
   */
  @AfterEach
  public void cleanUp() {
    simulator = null;
    mockSession = null;
    testLine = null;
  }
}