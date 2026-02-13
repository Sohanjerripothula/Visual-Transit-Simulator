package edu.umn.cs.csci3081w.project.model;

public abstract class AlphaDecorator extends Vehicle {
  protected Vehicle vehicle;

  public AlphaDecorator(Vehicle vehicle) {
    super(vehicle);
    this.vehicle = vehicle;
  }
}
