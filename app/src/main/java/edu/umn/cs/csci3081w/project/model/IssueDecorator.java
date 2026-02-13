package edu.umn.cs.csci3081w.project.model;

import java.io.PrintStream;

public class IssueDecorator extends AlphaDecorator {
  private Vehicle vehicle;
  private int alpha;

  /**
   * Constructor.
   * 
   * @param vehicle vehicle that has the issue.
   */
  public IssueDecorator(Vehicle vehicle) {
    super(vehicle);
    this.vehicle = vehicle;
    alpha = 155;
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
  public int getAlpha() {
    return alpha;
  }

  @Override
  public Vehicle getDV() {
    return vehicle.getDV();
  }

  @Override
  public int getR() {
    return vehicle.getR();
  }

  @Override
  public int getG() {
    return vehicle.getG();
  }

  @Override
  public int getB() {
    return vehicle.getB();
  }
}
