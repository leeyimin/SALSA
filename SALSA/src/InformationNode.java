
import java.util.ArrayList;
import java.util.Collections;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author YiMin
 */
public class InformationNode {
    
    private InformationNode prev,next;
    int counter;
    int size;
    int source;
    int[][] lsdb;
    int[] inTree;
    int[] dist;
    int minV;
    boolean allIn;
    String[] path;
    String[] vertexName;
    String text;
    int[] lastEdgeSource;
    
    
    
    public InformationNode(int sourceIndex, int no, int[][] LSD, String[] vName){
        prev = null;
        next=null;
        size=no;
        counter=1;
        lsdb=LSD;
        vertexName = vName;
        source = sourceIndex;
        lastEdgeSource= new int[size];
        
        inTree=new int[size];
        for(int i=0;i<size;i++) inTree[i]=0;
        inTree[source]=1;
        //storing source and immediate neighbours in table
        dist= new int[size];
        path = new String[size];
        for(int i=0;i<size;i++){
            dist[i]=LSD[source][i];
            if(dist[i]==-1)path[i]="-";
            else if(i==source)path[i] ="root";
            else {
                path[i]= vertexName[source]+" -> "+vertexName[i];
                lastEdgeSource[i]=source;
            }
        }
        
        allIn=true;
        for(int i=0;i<size;i++){
            if(inTree[i]==0){
                allIn=false;
                break;
            }
        }
        
        text="The link-state database is set up and the calculation for the least cost tree can start.";
    }
    
    public InformationNode(InformationNode previous){
        prev=previous;
        next=null;
        size=prev.size;
        source = prev.source;
        vertexName=prev.vertexName;
        inTree= prev.inTree.clone();
        dist=prev.dist.clone();
        path=prev.path.clone();
        allIn = prev.allIn;
        lsdb=prev.lsdb;
        counter=prev.counter+1;
        minV=prev.minV;
        lastEdgeSource=prev.lastEdgeSource;//store last node travelled so that the edge can be coloured on graph GUI if it is part of the tree
        //initialising variables
        
        if(counter%2==0){//to find node with next shortest dist
            minV=-1;
            for(int i=0;i<size;i++){
                if(minV==-1){
                    if(inTree[i]==0&&dist[i]!=-1)
                        minV=i;
                }
                else{
                    if(inTree[i]==0&&dist[i]!=-1&&dist[i]<dist[minV])
                        minV=i;
                }
            }//find node index of node with next shortest dist
            inTree[minV]=1;//add to tree
            text= vertexName[minV]+" has the least distance to the source node out of the nodes which are not added to the tree.\n"
                    + "Thus, it is added to the least-cost tree and the shortest distance from "+vertexName[source]+" to "
                +vertexName[minV]+ " is "+dist[minV]+".";
            
            allIn=true;
            for(int i=0;i<size;i++){
                if(inTree[i]==0){
                    allIn=false;
                    break;
                }
            }//if all nodes are in tree, ensure getNext() return this
            if(allIn){
                text+=" The least-cost tree is complete.";
            }
        }
        else{
            for(int i=0;i<size;i++){
                if(lsdb[minV][i]!=-1){
                    if(dist[i]==-1 || (dist[i]>(dist[minV]+lsdb[minV][i])) ){ 
                        dist[i]=dist[minV]+lsdb[minV][i];//update
                        lastEdgeSource[i]=minV;
                        path[i]=path[minV]+" -> "+ vertexName[i];
                    }
                }
            }//updating distance based on the new node added last step
            text="The distance to the different routers are updated based on the new node added.";
        }
        
        
    }
            
    public InformationNode getNext(){
        if(next!=null){//if it is alr generated
            return next;
        }
        if(allIn){//no more step
            return this;
        }
        next = new InformationNode(this);
        return next;
    }
    
    public InformationNode getPrev(){
        return prev;
    }
  
    
    public String[][] getLeastCostTable(){//generating table data for least cost table
        String[][] result = new String[size][4];
        for(int i=0;i<size;i++){
            result[i][0]=vertexName[i];
            result[i][1]=""+dist[i];
            result[i][2]=path[i];
            result[i][3]=inTree[i]==1?"Yes":"No";
        }
        return result;
    }
    
}
