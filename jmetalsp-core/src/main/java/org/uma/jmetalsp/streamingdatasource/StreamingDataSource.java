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
package org.uma.jmetalsp.streamingdatasource;

import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.updatedata.UpdateData;

import java.io.Serializable;

/**
 * Created by ajnebro on 18/4/16.
 */
public interface StreamingDataSource<D extends UpdateData> extends Serializable {
  public void setProblem(DynamicProblem<?, D> problem) ;
  public void start(JavaStreamingContext context) ;
}
