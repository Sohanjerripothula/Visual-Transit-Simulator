package edu.umn.cs.csci3081w.project.model;

import java.io.PrintStream;

public class MaroonDecorator extends ColorDecorator {
  private int red;
  private int green;
  private int blue;
  private Vehicle vehicle;

  /**
   * Constructor.
   * 
   * @param vehicle Vehicle that is being decorated.
   */
  public MaroonDecorator(Vehicle vehicle) {
    super(vehicle);
    this.vehicle = vehicle;
    red = 122;
    green = 0;
    blue = 25;
  }

  @Override
  public void report(PrintStream out) {
    vehicle.report(out);
  }

  @Override
  public int getCurrentCO2Emission() {
    return vehicle.getCurrentCO2Emission();
  }

  @Override
  public int getR() {
    return red;
  }

  @Override
  public int getG() {
    return green;
  }

  @Override
  public int getB() {
    return blue;
  }

  @Override
  public Vehicle getDV() {
    return vehicle.getDV();
  }
}
