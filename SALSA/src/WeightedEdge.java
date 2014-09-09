
import org.jgrapht.graph.DefaultWeightedEdge;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author YiMin
 */
public class WeightedEdge extends DefaultWeightedEdge{
     public WeightedEdge() {
            super();
        }

        @Override
        public String toString(){
            return Double.toString(getWeight());
        }
    
}
