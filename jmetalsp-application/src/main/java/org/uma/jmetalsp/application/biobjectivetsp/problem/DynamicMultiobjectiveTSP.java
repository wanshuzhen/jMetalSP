package org.uma.jmetalsp.application.biobjectivetsp.problem;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;
import org.uma.jmetalsp.application.biobjectivetsp.updateData.MultiobjectiveTSPUpdateData;
import org.uma.jmetalsp.problem.DynamicProblem;

/**
 * Version of the multi-objective TSP aimed at being solving dynamically.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicMultiobjectiveTSP
    extends AbstractIntegerPermutationProblem
    implements ConstrainedProblem<PermutationSolution<Integer>>,
    DynamicProblem<PermutationSolution<Integer>, MultiobjectiveTSPUpdateData> {
  public static final double NON_CONNECTED = Double.POSITIVE_INFINITY ;
  private int         numberOfCities ;
  private double [][] distanceMatrix ;
  private double [][] costMatrix;

  private boolean theProblemHasBeenModified;
  private static final int DISTANCE = 0;
  private static final int COST = 1;

  public OverallConstraintViolation<PermutationSolution<Integer>> overallConstraintViolationDegree ;


  public DynamicMultiobjectiveTSP(int numberOfCities,
                                  double[][] distanceMatrix,
                                  double[][] costMatrix) {
    this.numberOfCities = numberOfCities ;
    this.distanceMatrix = distanceMatrix ;
    this.costMatrix = costMatrix ;

    theProblemHasBeenModified = false ;

    setName("DMoTSP");
    setNumberOfVariables(numberOfCities);
    setNumberOfObjectives(2);
    setNumberOfConstraints(1);

    overallConstraintViolationDegree = new OverallConstraintViolation<PermutationSolution<Integer>>() ;
  }

  @Override
  public synchronized void evaluate(PermutationSolution<Integer> solution) {
    double fitness1   ;
    double fitness2   ;

    fitness1 = 0.0 ;
    fitness2 = 0.0 ;

    for (int i = 0; i < (numberOfCities - 1); i++) {
      int x ;
      int y ;

      x = solution.getVariableValue(i) ;
      y = solution.getVariableValue(i+1) ;

      if (distanceMatrix[x][y] != NON_CONNECTED) {
        fitness1 += distanceMatrix[x][y];
        fitness2 += costMatrix[x][y];
      }
    }
    int firstCity ;
    int lastCity  ;

    firstCity = solution.getVariableValue(0) ;
    lastCity = solution.getVariableValue(numberOfCities - 1) ;

    if (distanceMatrix[firstCity][lastCity] != NON_CONNECTED) {
      fitness1 += distanceMatrix[firstCity][lastCity];
      fitness2 += costMatrix[firstCity][lastCity];
    }

    solution.setObjective(0, fitness1);
    solution.setObjective(1, fitness2);
  }

  public synchronized void evaluateConstraints(PermutationSolution<Integer> solution) {
    int nonConnectedLinks = 0 ;
    for (int i = 0; i < (numberOfCities - 1); i++) {
      int x ;
      int y ;

      x = solution.getVariableValue(i) ;
      y = solution.getVariableValue(i+1) ;

      if (distanceMatrix[x][y] == NON_CONNECTED) {
        nonConnectedLinks ++ ;
      }
    }

    int firstCity ;
    int lastCity  ;
    firstCity = solution.getVariableValue(0) ;
    lastCity = solution.getVariableValue(numberOfCities - 1) ;

    if (distanceMatrix[firstCity][lastCity] == NON_CONNECTED) {
      nonConnectedLinks ++ ;
    }
    overallConstraintViolationDegree.setAttribute(solution, -1.0 * nonConnectedLinks);
    //System.out.println("Violation: " + nonConnectedLinks + ". Fitness: " + solution.getObjective(0)) ;
  }

  @Override
  public int getPermutationLength() {
    return numberOfCities;
  }

  public synchronized void updateCostValue(int row, int col, double newValue) {
    costMatrix[row][col] = newValue ;
    theProblemHasBeenModified = true ;
    //JMetalLogger.logger.info("Updated cost: " + row + ", " + col + ": " + newValue) ;
  }

  public synchronized void updateDistanceValue(int row, int col, double newValue) {
    distanceMatrix[row][col] = newValue ;
    theProblemHasBeenModified = true ;
   // JMetalLogger.logger.info("Updated distance: " + row + ", " + col + ": " + newValue) ;
  }

  /* Getters/Setters */
  public synchronized double[][] getDistanceMatrix() {
    return distanceMatrix;
  }

  public synchronized double[][] getCostMatrix() {
    return costMatrix;
  }

  public synchronized void setNumberOfCities(int numberOfCities) {
    this.numberOfCities = numberOfCities ;
  }

  public synchronized boolean hasTheProblemBeenModified() {
    return theProblemHasBeenModified;
  }

  @Override
  public synchronized void update(MultiobjectiveTSPUpdateData data) {
    if (data!=null && data.getMatrixID()==COST){
      updateCostValue(data.getX(),data.getY(),data.getValue());
    }else if(data!=null && data.getMatrixID()==DISTANCE){
      updateDistanceValue(data.getX(),data.getY(),data.getValue());
    }
  }

  @Override
  public synchronized void reset() {
    theProblemHasBeenModified = false ;
  }

  public String toString() {
    String result = "" ;
    for (int i = 0; i < numberOfCities; i++) {
      for (int j = 0; j < numberOfCities; j++) {
        result += "" +  distanceMatrix[i][j] + " " ;
      }
      result += "\n" ;
    }

    return result ;
  }
}
