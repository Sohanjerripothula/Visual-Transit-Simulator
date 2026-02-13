package edu.umn.cs.csci3081w.project.model;

import java.io.PrintStream;

public class GreenDecorator extends ColorDecorator {
  private int red;
  private int green;
  private int blue;
  private Vehicle vehicle;

  /**
   * Constructor.
   * 
   * @param vehicle vehicle being decorated.
   */
  public GreenDecorator(Vehicle vehicle) {
    super(vehicle);
    this.vehicle = vehicle;
    red = 60;
    green = 179;
    blue = 113;
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
