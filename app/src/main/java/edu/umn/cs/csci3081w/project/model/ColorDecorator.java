package edu.umn.cs.csci3081w.project.model;

public abstract class ColorDecorator extends Vehicle {
  protected Vehicle vehicle;

  /**
   * Constructor.
   */
  public ColorDecorator(Vehicle vehicle) {
    super(vehicle);
    this.vehicle = vehicle;
  }
}