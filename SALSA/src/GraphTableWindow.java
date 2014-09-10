
import com.jgraph.layout.*;
import com.jgraph.layout.organic.JGraphFastOrganicLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author michelle
 */
public class GraphTableWindow extends javax.swing.JFrame {
    
    JGraph graphComp;
    JGraphModelAdapter<String,WeightedEdge> jgAdapter;
    ListenableUndirectedWeightedGraph<String,WeightedEdge> graph;
    ArrayList<String> vertexList;
    ArrayList<Integer> edgeList;
    int[][] graphData;
    JGraphFastOrganicLayout layout;
    InformationNode current;
    int source;
    final String[] columnName = {"Router","Distance","Path","Added to Tree"};
    
    

    /**
     * Creates new form GraphTableWindow
     */
    public GraphTableWindow() {
        initComponents();
        
        generateGraph();
        makeDefaultGraph();
        generateLSTable();
        layoutGraph();
        
        getEdgeList();
        
        jTable2.setEnabled(false);
        
        
        jComboBox1.setModel(new DefaultComboBoxModel(vertexList.toArray()));
        jComboBox1.setSelectedIndex(0);
        
        current = new InformationNode(0,vertexList.size(),graphData,vertexList.toArray(new String[vertexList.size()]));
        source=0;
        updateInformation();
        this.pack();
        
    }
    
    public void getEdgeList(){
        edgeList=new ArrayList<>();
        for(int i=0;i<vertexList.size();i++){
            for(int j=i+1;j<vertexList.size();j++){
                if(graphData[i][j]!=-1){
                    edgeList.add(i);
                    edgeList.add(j);
                }
            }
        }
    }
    
    public void updateInformation(){
        jTable1.setModel(new DefaultTableModel(current.getLeastCostTable(),columnName));
        jTextArea1.setText(current.text);
        
        DefaultGraphCell cell;
        DefaultEdge edge;
        
        for(int i=0;i<edgeList.size();i+=2){
            WeightedEdge we = graph.getEdge(vertexList.get(edgeList.get(i)), vertexList.get(edgeList.get(i+1)));
            edge= jgAdapter.getEdgeCell(we);
            GraphConstants.setForeground(edge.getAttributes(), Color.BLACK);
            GraphConstants.setLineColor(edge.getAttributes(), Color.BLUE);
            AttributeMap cellAttr = new AttributeMap();
            cellAttr.put(edge, edge.getAttributes());
            jgAdapter.edit(cellAttr, null, null, null);
        }
        
        for(int i=0;i<vertexList.size();i++){
            cell = jgAdapter.getVertexCell(vertexList.get(i));
            if(current.inTree[i]==1){
                GraphConstants.setBackground(cell.getAttributes(), Color.RED);
                
                if(i!=source){
                    WeightedEdge we = graph.getEdge(vertexList.get(current.lastEdgeSource[i]), vertexList.get(i));
                    edge= jgAdapter.getEdgeCell(we);
                    GraphConstants.setForeground(edge.getAttributes(), Color.RED);
                    GraphConstants.setLineColor(edge.getAttributes(), Color.RED);
                    AttributeMap cellAttr = new AttributeMap();
                    cellAttr.put(edge, edge.getAttributes());
                    jgAdapter.edit(cellAttr, null, null, null);
                }
            }
            else{
                GraphConstants.setBackground(jgAdapter.getVertexCell(vertexList.get(i)).getAttributes(), Color.ORANGE);
            }
                AttributeMap cellAttr = new AttributeMap();
                cellAttr.put(cell, cell.getAttributes());
                jgAdapter.edit(cellAttr, null, null, null);
        }
    }
    
    public void makeDefaultGraph(){
        
        graphData= new int[10][10];
        
        for(int i=0;i<10;i++){
            vertexList.add("R"+(i+1));
            graph.addVertex("R"+(i+1));
        }
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                graphData[i][j]=-1;
            }
            graphData[i][i]=0;
        }
        int[] toAdd = {0,1,1,
            0,2,4,
            0,4,3,
            1,5,2,
            1,6,5,
            2,8,7,
            2,7,2,
            3,7,3,
            3,4,1,
            3,8,1,
            4,6,3,
            5,6,1,
            5,9,4,
            6,9,7,
            7,9,2,
            8,9,3};
        
        int x,y,l;
        WeightedEdge we;
        for(int i=0;i<toAdd.length;i+=3){
            x=toAdd[i];
            y=toAdd[i+1];
            l=toAdd[i+2];
            graphData[x][y]=graphData[y][x]=l;
            we=graph.addEdge(vertexList.get(x), vertexList.get(y));
            graph.setEdgeWeight(we, l);
        }
    }
    
    public void generateGraph(){
        graph = new ListenableUndirectedWeightedGraph<>(WeightedEdge.class);
        jgAdapter= new JGraphModelAdapter<>(graph);
        graphComp = new JGraph(jgAdapter);
        graphComp.setEditable(false);
        vertexList= new ArrayList<>();
        
        
    }
    
    public void layoutGraph(){
        layout= new JGraphFastOrganicLayout();
        JGraphFacade graphFacade = new JGraphFacade(graphComp);      
        layout.run(graphFacade);
        Map nestedMap = graphFacade.createNestedMap(true, true);
        graphComp.getGraphLayoutCache().edit(nestedMap);
        graphComp.setDisconnectable(false);
        jScrollPane2.setViewportView(graphComp);
    }
    
    public void generateLSTable(){
        String[][] data = new String[graphData.length][graphData.length+1];
        for(int i=0;i<vertexList.size();i++){
            data[i][0]=vertexList.get(i);
            for(int j=0;j<vertexList.size();j++){
                data[i][j+1]=""+graphData[i][j];
            }
        }
        String col[]= new String[vertexList.size()+1];
        col[0]="Routers";
        for(int i=0;i<vertexList.size();i++)col[i+1]=vertexList.get(i);
        jTable2.setModel(new DefaultTableModel(data,col));
       
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Simulator of Link-State Algorithm");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setEnabled(false);
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("Choose file to import graph...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1))
        );

        jButton3.setText("Previous");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Next");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Calibri", 0, 12)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        jScrollPane4.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addContainerGap(45, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Graph", jScrollPane2);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTable2);

        jTabbedPane1.addTab("Link-State Database", jScrollPane3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int returnVal = jFileChooser1.showOpenDialog(this);
        if(returnVal==JFileChooser.APPROVE_OPTION){
            try {
                File file=jFileChooser1.getSelectedFile();
                Scanner sc = new Scanner(file);
                generateGraph();
                int n, en,x,y,l;
                n=sc.nextInt();
                en=sc.nextInt();
                sc.nextLine();
                for(int i=0;i<n;i++){
                    vertexList.add(sc.nextLine());
                    graph.addVertex(vertexList.get(i));
                    System.out.println(vertexList.get(i));
                }
                
                graphData= new int[n][n];
                for(int i=0;i<n;i++){
                    for(int j=0;j<n;j++){
                        graphData[i][j]=-1;
                    }
                    graphData[i][i]=0;
                }
                WeightedEdge we;
                for(int i=0;i<en;i++){
                    x=sc.nextInt();
                    y=sc.nextInt();
                    l=sc.nextInt();
                    x--;
                    y--;
                    graphData[x][y]=graphData[y][x]=l;
                    we=graph.addEdge(vertexList.get(x), vertexList.get(y));
                    graph.setEdgeWeight(we, l);
                }
                
                generateLSTable();
                layoutGraph();
                getEdgeList();
                
                jComboBox1.setModel(new DefaultComboBoxModel(vertexList.toArray()));
                jComboBox1.setSelectedIndex(0);

                current = new InformationNode(0,vertexList.size(),graphData,vertexList.toArray(new String[vertexList.size()]));
                source=0;
                updateInformation();
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GraphTableWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        source = jComboBox1.getSelectedIndex();
        current = new InformationNode(source,vertexList.size(),graphData,vertexList.toArray(new String[vertexList.size()]));
        updateInformation();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        if(current.getPrev()!=null) current=current.getPrev();
        updateInformation();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if(current.getNext()!=null){
            current=current.getNext();
        }
        updateInformation();
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GraphTableWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GraphTableWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GraphTableWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GraphTableWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GraphTableWindow().setVisible(true);
            }
        });
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
