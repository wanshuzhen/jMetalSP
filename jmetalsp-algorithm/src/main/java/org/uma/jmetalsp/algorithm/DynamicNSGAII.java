//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetalsp.algorithm;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetalsp.problem.DynamicProblem;

import java.util.List;

/**
 * Class implementing a dynamic version of NSGA-II. Most of the code of the original NSGA-II is
 * reused, and measures are used to allow external components to access the results of the
 * computation.
 *
 * @todo Explain the behaviour of the dynamic algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicNSGAII<S extends Solution<?>>
    extends NSGAII<S>
    implements DynamicAlgorithm<List<S>> {

  private int completedIterations ;
  protected SimpleMeasureManager measureManager ;
  protected BasicMeasure<List<S>> solutionListMeasure ;

  public DynamicNSGAII(DynamicProblem<S, ?> problem, int maxEvaluations, int populationSize, CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
    super(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);

    completedIterations = 0 ;

    solutionListMeasure = new BasicMeasure<>() ;
    measureManager = new SimpleMeasureManager() ;
    measureManager.setPushMeasure("currentPopulation", solutionListMeasure);
  }

  @Override
  public DynamicProblem<S, ?> getDynamicProblem() {
    return (DynamicProblem<S, ?>) super.getProblem();
  }

  @Override
  public int getCompletedIterations() {
    return completedIterations ;
  }

  @Override protected boolean isStoppingConditionReached() {
    if (evaluations >= maxEvaluations) {

      solutionListMeasure.push(getPopulation()) ;
      
      SolutionListUtils.restart(getPopulation(), getDynamicProblem(), 100);
      evaluator.evaluate(getPopulation(), getDynamicProblem()) ;

      initProgress();
      completedIterations++;
    }
    return false;
  }

  @Override protected void updateProgress() {
    if (getDynamicProblem().hasTheProblemBeenModified()) {
      SolutionListUtils.restart(getPopulation(), getDynamicProblem(), 100);

      evaluator.evaluate(getPopulation(), getDynamicProblem()) ;
      getDynamicProblem().reset();
    }
    evaluations += getMaxPopulationSize() ;
    completedIterations ++ ;
  }

  @Override
  public String getName() {
    return "DynamicNSGAII";
  }

  @Override
  public String getDescription() {
    return "Dynamic version of algorithm NSGA-II";
  }

  @Override
  public MeasureManager getMeasureManager() {
    return measureManager ;
  }
}
