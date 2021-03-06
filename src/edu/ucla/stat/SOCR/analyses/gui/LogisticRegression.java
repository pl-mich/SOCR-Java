/****************************************************
Statistics Online Computational Resource (SOCR)
http://www.StatisticsResource.org
 
All SOCR programs, materials, tools and resources are developed by and freely disseminated to the entire community.
Users may revise, extend, redistribute, modify under the terms of the Lesser GNU General Public License
as published by the Open Source Initiative http://opensource.org/licenses/. All efforts should be made to develop and distribute
factually correct, useful, portable and extensible resource all available in all digital formats for free over the Internet.
 
SOCR resources are distributed in the hope that they will be useful, but without
any warranty; without any explicit, implicit or implied warranty for merchantability or
fitness for a particular purpose. See the GNU Lesser General Public License for
more details see http://opensource.org/licenses/lgpl-license.php.
 
http://www.SOCR.ucla.edu
http://wiki.stat.ucla.edu/socr
 It s Online, Therefore, It Exists! 
****************************************************/

/* 	modified by annie che 200508.
	separate the gui part from the model part
*/

package edu.ucla.stat.SOCR.analyses.gui;

import edu.ucla.stat.SOCR.distributions.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import edu.ucla.stat.SOCR.analyses.data.*;
import edu.ucla.stat.SOCR.analyses.result.*;
import edu.ucla.stat.SOCR.analyses.exception.*;
import edu.ucla.stat.SOCR.analyses.model.*;
import edu.ucla.stat.SOCR.analyses.example.ChiSquareModelFitExamples;
import edu.ucla.stat.SOCR.analyses.example.LogisticRegressionExamples;
import edu.ucla.stat.SOCR.analyses.xml.*;
import edu.ucla.stat.SOCR.util.AnalysisUtility;


import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;

import edu.ucla.stat.SOCR.analyses.util.Expected_value_expotential_function;
import edu.ucla.stat.SOCR.analyses.util.Logistic_Regression;
import edu.ucla.stat.SOCR.analyses.util.pca_analysis;
import edu.ucla.stat.SOCR.analyses.util.Gini;

/** this class is for Logistic Regression only. */
public class LogisticRegression extends Analysis implements PropertyChangeListener {

        private double betas[];
	public String[][] example = new String[1][1];	// the example data
	public String[] columnNames = new String[1];
	private double[] xData = null;
	private double[][] xDataArray = null;
	private double[] yData = null;
	private double[] predicted = null;
	private double[] residuals = null;
	private double[] sortedResiduals = null;
	private double[] sortedStandardizedResiduals = null;
	private int[] sortedResidualsIndex = null;
	private double[] sortedNormalQuantiles = null;
	private double[] sortedStandardizedNormalQuantiles = null;

	private String dependentHeader = null, independentHeader = null;
	static int times = 0;

	FileDialog fileDialog;
	Frame fileDialogFrame = new Frame();
	File file;
	//FileInputStream fstream;
	private String fileName = "";
	private boolean useHeader = true;
	private String header = null;

	public JTabbedPane tabbedPanelContainer;
	//objects
	private JToolBar toolBar;

	private Frame frame;
	private String xmlInputString = null;
	
	LogisticRegressionResult result;
	int independentListLength;
	int xLength, yLength;
	

	/**Initialize the Analysis*/
	public void init(){
		showInput = false;
		showSelect = false;
		showVisualize= false;
		super.init();
		
		analysisType = AnalysisType.LOGISTIC_REGRESSION;
		useInputExample = false;
		useLocalExample = false;
		useRandomExample = true;
		useServerExample = false;
		
		useStaticExample = LogisticRegressionExamples.availableExamples;

		onlineDescription = "http://en.wikipedia.org/wiki/Logistic_regression";
		depMax = 1; // max number of dependent var
		indMax = 15; // max number of independent var
		resultPanelTextArea.setFont(new Font(outputFontFace,Font.BOLD, outputFontSize));
		frame = getFrame(this);
		setName("Regression & Correlation Analysis");
		// Create the toolBar
		toolBar = new JToolBar();
		createActionComponents(toolBar);
		this.getContentPane().add(toolBar,BorderLayout.NORTH);

		// use the new JFreeChar function. annie che 20060312
		chartFactory = new Chart();
		resetGraph();
		validate();
	}


	/** Create the actions for the buttons */
       protected void createActionComponents(JToolBar toolBar){
       		super.createActionComponents(toolBar);
      }

	/**This method sets up the analysis protocol, when the applet starts*/
	public void start(){
	}

	/**This method defines the specific statistical Analysis to be carried our on the user specified data. ANOVA is done in this case. */
	public void doAnalysis(){
		if(dataTable.isEditing())
			dataTable.getCellEditor().stopCellEditing();
		////////System.out.println("MLR hasExample = " + hasExample);


		if (!hasExample ) {
			JOptionPane.showMessageDialog(this, DATA_MISSING_MESSAGE);
			return;
		}

		if (dependentIndex < 0 || independentIndex < 0 || independentLength == 0) {
			JOptionPane.showMessageDialog(this, VARIABLE_MISSING_MESSAGE);
			return;
		}
                
                String cellValue = null;
                
                int originalRow = 0;
                
                for (int k = 0; k < dataTable.getRowCount(); k++) 
                {
			cellValue = ((String)dataTable.getValueAt(k, 0));
                
			if (cellValue != null && !cellValue.equals("")) {
				originalRow++;
						
                                }
                }
                
                cellValue = null;
                int originalColumn = 0;
                
                for (int k = 0; k < dataTable.getColumnCount(); k++) 
                {
			cellValue = ((String)dataTable.getValueAt(0, k));
                
			if (cellValue != null && !cellValue.equals("")) {
				originalColumn++;
						
                                }
                }

                double covariate[][] = new double[originalRow][originalColumn-1];
                
                System.out.println("Covariate has column: " + (originalColumn-1) + "\n");
                
                for (int i = 0; i < originalRow; i++)
                {
                    covariate[i][0] = Double.parseDouble((String)dataTable.getValueAt(i, 0));
                }
                
                
		for (int k = 0; k < originalRow; k++) 
                    for (int j = 2; j < originalColumn; j++){
                   
		    if (dataTable.getValueAt(k, j) != null && !dataTable.getValueAt(k, j).equals("")) {
			covariate[k][j-1] = Double.parseDouble((String)dataTable.getValueAt(k, j));
                    }
		}    // added

                
                
                int iterations= 20;
                String constant="Yes";                
                double tolerance=0.001;

                
                
                
		Object[] independentVar = independentList.toArray();

		dependentHeader = columnModel.getColumn(dependentIndex).getHeaderValue().toString().trim();



		int varIndex = -1;
		int varIndexList[] = new int[independentVar.length];
		independentListLength = independentList.size();
		////////System.out.println("MLR independentVar.length = " + independentVar.length);
		independentHeaderArray = new String[independentVar.length];

		for (int i = 0; i < independentVar.length; i++) {
			varIndex = ( (Integer)independentList.get(i)).intValue();
			independentHeader = columnModel.getColumn(varIndex).getHeaderValue().toString().trim();
			independentHeaderArray[i] = independentHeader;
			////////System.out.println("MLR independentHeader["+i+"] = " + independentHeader);
			//resultPanelTextArea.append("  "  + independentHeader);// + " original index = " + varIndex );
			varIndexList[i] = varIndex;
		}

		data = new Data();

		/******************************************************************
		From this point, the code has been modified to work with input cells that are empty.
		******************************************************************/
		xLength = 0;
		yLength = 0;
		cellValue = null;
		ArrayList<String> xList = new ArrayList<String>();
		ArrayList<String> yList = new ArrayList<String>();
		xData = null;
		yData = null;
		xDataArray = new double[independentLength][xLength];
		int xIndex = 0;

		try {
			for (int k =0; k < dataTable.getRowCount(); k++) {
				try {
					cellValue = ((String)dataTable.getValueAt(k, dependentIndex)).trim();
					if (cellValue != null && !cellValue.equals("")) {
						try {
							yList.add(yLength , cellValue);
							yLength++;
							} catch (Exception e ) {
							//////////System.out.println(" Inner Get Cell Value Exception = " + e);
							}
						}
						else {
							continue; // to the next for
						}
					} catch (Exception e) {
						resultPanelTextArea.append("\n\tSample Size =" + xLength);
					}
			} // end for k

			yData = new double[yLength];
			for (int i = 0; i < yLength; i++) {
				try {
					yData[i] = Double.parseDouble((String)yList.get(i));
					////System.out.println("Y = " + yData[i]);
					} catch (Exception e) {
						resultPanelTextArea.append("\n\tSample Size =" + this.DATA_ERROR_MESSAGE);
					}
			}
			if (yLength <= 0) {
				JOptionPane.showMessageDialog(this, NULL_VARIABLE_MESSAGE);
			return;
			}
                        

			data.appendY(dependentHeader, yData, DataType.QUANTITATIVE);
			//////System.out.println("\nindependentListLength = " +independentListLength);
			for (int index = 0; index < independentListLength; index++) { // for each independent variable
				xLength = 0;
				//////////System.out.println("\nvarIndexList[index] = " +varIndexList[index]);
				for (int k =0; k < dataTable.getRowCount(); k++) {
					try {
						cellValue = ((String)dataTable.getValueAt(k, varIndexList[index])).trim();
						if (cellValue != null && !cellValue.equals("")) {
							xList.add(xLength , cellValue);
							xLength++;
						}
						else {
							continue; // to the next for iteration
						}
					} catch (Exception e) {
						resultPanelTextArea.append("\n\tSample Size =" + this.DATA_ERROR_MESSAGE);
					}
				}
			xData = new double[xLength];

			for (int i = 0; i < xLength; i++) {
				try {
					xData[i] = Double.parseDouble((String)xList.get(i));
					////System.out.println("X = " +xList.get(i));
					} catch (Exception e) {
						resultPanelTextArea.append("\n\tSample Size =" + this.DATA_ERROR_MESSAGE);
					}
			}
			xDataArray[xIndex] = xData;
			String tempHeader = columnModel.getColumn(varIndexList[index]).getHeaderValue().toString().trim();
			xIndex++;
			data.appendX(tempHeader, xData, DataType.QUANTITATIVE);
			if (xLength <= 0) {
				JOptionPane.showMessageDialog(this, NULL_VARIABLE_MESSAGE);
			return;
			}

			}

		} catch (Exception e) {
			resultPanelTextArea.append("\n\tSample Size =" + this.DATA_ERROR_MESSAGE);
		}

		// this following passage should be duplicated for ANOVA two way, ANCOVA, etc. -- any of those that have multiple number of regressors.
		boolean isColinear = false;
		String colinearVar1 = "";
		String colinearVar2 = "";
		String colinearMessage = "\n";
		for (int i = 1; i < independentLength; i++) {
			for (int j = 0; j < independentLength - 1; j++) {
				if (i != j && AnalysisUtility.dataColinear(xDataArray[i],xDataArray[j])) {
					isColinear = true;
					// next is all priting, otherwise the user wouldn't know what vars to remove.
					colinearVar1 = columnModel.getColumn(i).getHeaderValue().toString().trim();
					colinearVar2 = columnModel.getColumn(j).getHeaderValue().toString().trim();
					try {
						colinearMessage = colinearMessage + "Correlation(" +colinearVar1 + ", " + colinearVar2 + ") = " + AnalysisUtility.sampleCorrelation(xDataArray[i], xDataArray[j]) + "\n";
					} catch (DataIsEmptyException e) {
						JOptionPane.showMessageDialog(this, e.toString());
					}
				}
			}
		}

	


		for (int i = 0; i < independentVar.length; i++) {
			varIndex = ( (Integer)independentList.get(i)).intValue();
			independentHeader = columnModel.getColumn(varIndex).getHeaderValue().toString().trim();
			independentHeaderArray[i] = independentHeader;
			////////System.out.println("MLR independentHeader["+i+"] = " + independentHeader);
			resultPanelTextArea.append("  "  + independentHeader);// + " original index = " + varIndex );
			varIndexList[i] = varIndex;
		}
		if (isColinear) {
			JOptionPane.showMessageDialog(this, DATA_COLINEAR_MESSAGE);			resultPanelTextArea.append("\n\t" + colinearMessage + "\n");
			return;
		}
		resultPanelTextArea.append("\n\n\tLogistic Regression Results:" );
                
                // Start
  
                
                Logistic_Regression Logit= new Logistic_Regression();
                try{
                        Logit.regression(covariate, yData, constant, tolerance, iterations);  // added here
                }catch(Exception e)
                {
                        JOptionPane.showMessageDialog(this, DATA_COLINEAR_MESSAGE);  
                        resultPanelTextArea.setText("\n"); // clear first
                        resultPanelTextArea.append("Please check linear dependence among predictor variables. Try modifying the original data by a small amount" + "\n");
                        resultPanelTextArea.append("(e.g. change 1.00 to 1.01) to avoid colinearity while maintaining an accurate result."+ "\n");
                        return;
                }
                        betas = Logit.getbetas();
		
                        double odds []= Logit.get_odds();
		
                        double walds []= Logit.getWald();

                        /*System.out.println("The p-value is: "+betas[0]+" With odds of "+odds[0]+", wald statistic " +walds[0]+ " and pvalue of " +walds_pvalue[0]);
		
                        for (int i=1; i <covariate[0].length+1; i++ ) {
                            System.out.println("The Beta"+i+" is: "+betas[i]+" With odds of "+odds[i]+", wald statistic " +walds[i]+ " and pvalue of "+walds_pvalue[i]);
                        }*/ 
                        
                        
                        //for testing
                        /*double covariateTest1[][] = new double[originalRow][originalColumn-1];
                        
                        for (int j = 0; j < originalColumn-1; j++)
                            for (int i = 0; i < originalRow; i++)
                            {
                                covariateTest1[i][j] = covariate[i][0]*betas[j+1];
                            }
                        
                        double total = 1.50;
                        double predictorY[] = new double[14];
                        double totalError1 = 0;
                        
                        for (int i = 0; i < 6; i++)
                            for (int j = 0; j < originalColumn-1; j++)
                            {
                                total = total + covariateTest1[i][j];
                                if (j == originalColumn-2)
                                {
                                    predictorY[i] = 1/(1 + Math.exp(total*(-1)));
                                    totalError1 = totalError1 + Math.pow(predictorY[i], 2);
                                    total = 1.50;
                                }
                            }
                        
                        for (int i = 6; i < 14; i++)
                            for (int j = 0; j < originalColumn-1; j++)
                            {
                                total = total + covariateTest1[i][j];
                                if (j == originalColumn-2)
                                {
                                    predictorY[i] = 1/(1 + Math.exp(total*(-1)));
                                    totalError1 = totalError1 + Math.pow(predictorY[i]-1, 2);
                                    total = 1.50;
                                }
                                
                            }
                        
                        double errorRate1 = Math.sqrt(totalError1);
                        
                        // R calculation
                        
                        double betas2[] = new double[5];
                        betas2[0] = 663;
                        betas2[1] = 20.3;
                        betas2[2] = 1.927;
                        betas2[3] = -7.18;
                        betas2[4] = -0.0001724;
                        
                        double covariateTest2[][] = new double[originalRow][originalColumn-1];
                        
                        for (int j = 0; j < originalColumn-1; j++)
                            for (int i = 0; i < originalRow; i++)
                            {
                                covariateTest2[i][j] = covariate[i][0]*betas2[j+1];
                            }
                        
                        double total2 = 663;
                        double predictorY2[] = new double[14];
                        double totalError2 = 0;
                        
                        for (int i = 0; i < 6; i++)
                            for (int j = 0; j < originalColumn-1; j++)
                            {
                                total2 = total2 + covariateTest2[i][j];
                                if (j == originalColumn-2)
                                {
                                    predictorY2[i] = 1/(1 + Math.exp(total2*(-1)));
                                    totalError2 = totalError2 + Math.pow(predictorY2[i], 2);
                                    total2 = 663;
                                }
                            }
                        
                        for (int i = 6; i < 14; i++)
                            for (int j = 0; j < originalColumn-1; j++)
                            {
                                total2 = total2 + covariateTest2[i][j];
                                if (j == originalColumn-2)
                                {
                                    predictorY2[i] = 1/(1 + Math.exp(total2*(-1)));
                                    totalError2 = totalError2 + Math.pow(predictorY2[i]-1, 2);
                                    total2 = 663;
                                }
                                
                            }
                        
                        double errorRate2 = Math.sqrt(totalError2);
                        
                        System.out.println("\n Newton-Raphson Betas: \n");
                        
                        for (int i = 0; i < betas.length; i++)
                        {
                            System.out.println("Beta " + i + " is " + betas[i] + "\n");
                        }
                        
                        
                        System.out.println("\nError Rate 1 is: " + errorRate1 + "\n");
                        
                        System.out.println("\n Betas in R: \n");
                        
                        for (int i = 0; i < betas2.length; i++)
                        {
                            System.out.println("Beta " + i + " is " + betas2[i] + "\n");
                        }
                        System.out.println("Error Rate 2 is: " + errorRate2 + "\n");*/
                        
		// Call the Controller method getAnalysis() delegate the work to Model
		////System.out.println("LogisticRegression applet begin result" );
		result = null;
		String className = null;


		try {
			//////System.out.println("LogisticRegression result start" );

			result = (LogisticRegressionResult)data.getAnalysis(AnalysisType.LOGISTIC_REGRESSION);
			////System.out.println("LogisticRegression applet begin result = " + result);

		} catch (DataIsEmptyException e) {
			//resultPanelTextArea.append("\n\tError:" + this.DATA_ERROR_MESSAGE);

		} catch (Exception e) {
			//resultPanelTextArea.append("\n\tError:" + this.DATA_ERROR_MESSAGE);
		}

	
		
		updateResults();
                doGraph();
		/* doGraph is underconstruction thus commented out. annie che 20060314 */
		//if (useGraph)

	}
	
	public void updateResults(){
		/*******************************************************/
		////System.out.println("FINISH try result = " + result);
		// Retreive the data from Data Object using HashMap
		int varLength = independentListLength + 1;
		double[] beta = null;
		double[] seBeta =null;
		double[] tStat = null;
		double[] pValue = null;
		int dfError = 0;
		double rSquare = 0;

		//ArrayList varName = null;
		String[] varList = null;
		
		if (result==null)
			return;
		
		result.setDecimalFormat(dFormat);
		
		resultPanelTextArea.setText("\n");//clear first
                
                resultPanelTextArea.setText("**Please note: Your Target Variable need to be bianry and have only the values of '0' and '1'\n");
                        
		resultPanelTextArea.append("\n\tNumber of Independent Variable(s) = "  + independentListLength);


		resultPanelTextArea.append("\n\tSample Size =" + xLength);

		resultPanelTextArea.append("\n\tDependent Variable  = "  + dependentHeader);// + " original index = " + dependentIndex );
		resultPanelTextArea.append("\n\tIndependent Variable(s) = ");
		
		for (int i = 0; i < independentHeaderArray.length; i++) {
                        if (i == -1)
                            continue;
			resultPanelTextArea.append("  "  + independentHeaderArray[i]);// + " original index = " + varIndex );
		}
		
		try {
			//varName = (ArrayList)(result.getTexture().get(MultiLinearRegressionResult.VARIABLE_LIST));
			varList = result.getVariableList();
			////System.out.println("varList = " + varList);

			//(String[])(result.getTexture().get(MultiLinearRegressionResult.VARIABLE_LIST));
		} catch (NullPointerException e) {
			////System.out.println("varList e = " + e);

			//showError("NullPointerException  = " + e);
		}
		try {
			beta = result.getBeta();//(double[])(result.getTexture().get(MultiLinearRegressionResult.BETA));
		} catch (NullPointerException e) {
			//showError("NullPointerException  = " + e);
		}
		try {
			seBeta = result.getBetaSE();//(double[])(result.getTexture().get(MultiLinearRegressionResult.BETA_SE));
		} catch (NullPointerException e) {
			//showError("NullPointerException  = " + e);
		}
		try {
			tStat = result.getBetaTStat();//(double[])(result.getTexture().get(MultiLinearRegressionResult.T_STAT));
		} catch (NullPointerException e) {
			//showError("NullPointerException  = " + e);
		}
		try {
			pValue = (double[]) (result.getBetaPValue());//(String[])(result.getTexture().get(MultiLinearRegressionResult.P_VALUE));
		} catch (NullPointerException e) {
		}
		try {
			rSquare = result.getRSquare();//(String[])(result.getTexture().get(MultiLinearRegressionResult.P_VALUE));
		} catch (NullPointerException e) {
		}

		try {
			dfError = result.getDF();//(Integer)result.getTexture().get(MultiLinearRegressionResult.DF_ERROR)).intValue();
		} catch (NullPointerException e) {
			//showError("\nException = " + e);
		}

		try {
			predicted = result.getPredicted();//(double[])(result.getTexture().get(MultiLinearRegressionResult.PREDICTED));
		} catch (NullPointerException e) {
			//showError("NullPointerException  = " + e);
		}
		try {
			residuals = result.getResiduals();//(double[])(result.getTexture().get(MultiLinearRegressionResult.RESIDUALS));
		} catch (NullPointerException e) {
			//showError("NullPointerException  = " + e);
		}

		//HashMap residualMap = AnalysisUtility.getResidualNormalQuantiles(residuals, dfError);

		try {
			sortedResiduals = result.getSortedResiduals();//(double[])residualMap.get(MultiLinearRegressionResult.SORTED_RESIDUALS);
		} catch (NullPointerException e) {
			//showError("\nException = " + e);
		}
		try {
			sortedStandardizedResiduals = result.getSortedStandardizedResiduals();//(double[])residualMap.get(MultiLinearRegressionResult.SORTED_STANDARDIZED_RESIDUALS);
		} catch (NullPointerException e) {
			//showError("\nException = " + e);
		}

		try {
			sortedResidualsIndex = result.getSortedResidualsIndex();//(int[])residualMap.get(MultiLinearRegressionResult.SORTED_RESIDUALS_INDEX);
		} catch (NullPointerException e) {
			//showError("\nException = " + e);
		}
		try {
			sortedNormalQuantiles = result.getSortedNormalQuantiles();//(double[])residualMap.get(MultiLinearRegressionResult.SORTED_NORMAL_QUANTILES);
		} catch (NullPointerException e) {
			//showError("\nException = " + e);
		}
		try {
			sortedStandardizedNormalQuantiles = result.getSortedStandardizedNormalQuantiles();//(double[])residualMap.get(MultiLinearRegressionResult.SORTED_STANDARDIZED_NORMAL_QUANTILES);
		} catch (NullPointerException e) {
			//showError("\nException = " + e);
		}

		resultPanelTextArea.append("\n\n\tRegression Model:\n\t\t" + dependentHeader + " = ");
                resultPanelTextArea.append(" "+ "1/(1+exp(-Z)\n\t\t");
                resultPanelTextArea.append("where Z = ");
                
		for (int i = 0; i < betas.length; i++) {
                    
			if (i==0) resultPanelTextArea.append(" "+ result.getFormattedDouble(betas[i]));
			
			else {
                            if (independentHeaderArray.length-i == -1)
                                break;
                            resultPanelTextArea.append(" +"+ result.getFormattedDouble(betas[i])+"*"+independentHeaderArray[independentHeaderArray.length-i]);
                        }
		}
		resultPanelTextArea.append(".\n\n");
                
		for (int i = 0; i < betas.length; i++) {
                    
                        if (i == -1)
                            break;
			//resultPanelTextArea.append("\n\n"+varName.get(i) + "\n\tEstimate = "+ beta[i] + "\n\tStd. Error" + seBeta[i] + "\n\t t-valuer" + tStat[i] + "\n\tp-value " + pValue[i]);
                        if (i == 0) resultPanelTextArea.append("\n\n\t" + "INTERCEPT"+ ":\n\tEstimate = "+ result.getFormattedDouble(betas[i]) + "\n\tStandard Error = " + result.getFormattedDouble(seBeta[i]) + "\n\tWald P-Value = " + result.getFormattedDouble(pValue[i]));
                        else{ 
                            if (independentHeaderArray.length-i == -1)
                                break;
                            resultPanelTextArea.append("\n\n\t"+independentHeaderArray[independentHeaderArray.length-i] + ":\n\tEstimate = "+ result.getFormattedDouble(betas[i]) + "\n\tStandard Error = " + result.getFormattedDouble(seBeta[i])+ "\n\tWald P-Value = " + result.getFormattedDouble(pValue[i]));
                        }
			/*if (pValue[i].equals("0.0")) {
				resultPanelTextArea.append("\n\tP-Value: <2E-16");
			}
			else {*/
			//resultPanelTextArea.append("\n\tP-Value = " + AnalysisUtility.enhanceSmallNumber(pValue[i]));

		}
		resultPanelTextArea.append("\n\n\tR-Square = " + result.getFormattedDouble(rSquare));


/*
		resultPanelTextArea.append("\nPREDICTED        = " );


		for (int i = 0; i < xLength; i++) {
			resultPanelTextArea.append(" " + predicted[i]);
		}
		resultPanelTextArea.append("\nRESIDUALS        = " );

		for (int i = 0; i < xLength; i++) {
			resultPanelTextArea.append(" " + residuals[i]);
		}

		resultPanelTextArea.append("\nRESIDUALS SORTED= " );

		for (int i = 0; i < xLength; i++) {
			resultPanelTextArea.append(" " + sortedResiduals[i]);
		}
		resultPanelTextArea.append("\nRESIDUALS INDEX SORTED= " );

		for (int i = 0; i < xLength; i++) {
			resultPanelTextArea.append(" " + sortedResidualsIndex[i]);
		}
		resultPanelTextArea.append("\nRESIDUALS NORMAL QUANTILES = " );

		for (int i = 0; i < xLength; i++) {
			resultPanelTextArea.append(" " + sortedNormalQuantiles[i]);
		}

		resultPanelTextArea.append("\nStandardized RESIDUALS Standardized = " );

		for (int i = 0; i < xLength; i++) {
			resultPanelTextArea.append(" " + sortedStandardizedResiduals[i]);
		}
		resultPanelTextArea.append("\nStandardized NORMAL QUANTILES = " );

		for (int i = 0; i < xLength; i++) {
			resultPanelTextArea.append(" " + sortedStandardizedNormalQuantiles[i]);
		}

*/		resultPanelTextArea.append("\n" );

		resultPanelTextArea.setForeground(Color.BLUE);
                
                
                
	}

	/** convert a generic string s to a fixed length one. */
    	public String monoString(String s) {
		String sAdd = new String(s + "                                      ");
		return sAdd.substring(0,14);
    	}

	/** convert a generic double s to a "nice" fixed length string */
    	public String monoString(double s) {
		final double zero = 0.00001;
        	Double sD = new Double(s);
		String sAdd = new String();
		if(s>zero)
	    		sAdd = new String(sD.toString());
		else  sAdd = "<0.00001";

		sAdd=sAdd.toLowerCase();
		int i=sAdd.indexOf('e');
		if(i>0)
	    		sAdd = sAdd.substring(0,4)+"E"+sAdd.substring(i+1,sAdd.length());
		else if(sAdd.length()>10)
				sAdd = sAdd.substring(0,10);

		sAdd = sAdd + "                                      ";
		return sAdd.substring(0,14);
    	}

	/** convert a generic integer s to a fixed length string */
    	public String monoString(int s) {
		Integer sD = new Integer(s);
		String sAdd = new String(sD.toString());
		sAdd = sAdd + "                                      ";
		return sAdd.substring(0,14);
    	}

        public void reset() {
        	super.reset();
        	independentHeaderArray = null;
        }


    /** Implementation of PropertyChageListener.*/
    public void propertyChange(PropertyChangeEvent e) {
		String propertyName = e.getPropertyName();
		if(propertyName.equals("DataUpdate")) {
			//update the local version of the dataTable by outside source
			dataTable = (JTable)(e.getNewValue());
			dataPanel.removeAll();
		   	dataPanel.add(new JScrollPane(dataTable));
		}
   }
	public Container getDisplayPane() {
		this.getContentPane().add(toolBar,BorderLayout.NORTH);
		return this.getContentPane();
	}
   	protected void doGraph() {
            
		// graphComponent is available here
		// data: variables double xData, yData, residuals, predicted are available here after doAnalysis() is run.
		graphPanel.removeAll();
		/************************************/
		JPanel innerPanel = new JPanel();
		JScrollPane graphPane = new JScrollPane(innerPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		graphPanel.add(graphPane);
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
		graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.Y_AXIS));

		//JFreeChart scatterChart = chartFactory.getQQChart("Scatter Plot of " + dependentHeader + " vs " + independentHeader, independentHeader, dependentHeader, dependentHeader + " Value  " , xData, yData,  "Regression Line", intercept, slope, "");
		//ChartPanel chartPanel = new ChartPanel(scatterChart, false);
		//chartPanel.setPreferredSize(new Dimension(plotWidth,plotHeight));
		//innerPanel.add(chartPanel);
		/************************************/

		ChartPanel chartPanel = null;

		// 1. scatter plot of data: yData vs. xData
		////////System.out.println("MLR doGraph independentHeaderArray.length = " + independentHeaderArray.length);
		for (int i = 0; i < xDataArray.length; i++) {
			xData = xDataArray[i];
			independentHeader = independentHeaderArray[i];
			JFreeChart scatterChart = chartFactory.getLineChart("Scatter Plot of " + dependentHeader + " vs. " + independentHeader, independentHeader, dependentHeader, xData, yData, "noline");//getChart(title, xlabel, ylabel, xdata,ydata)

			//JFreeChart scatterChart = chartFactory.getQQChart("Scatter Plot of " + dependentHeader + " vs " + independentHeader, independentHeader, dependentHeader, dependentHeader , xData, yData,  "", 0, 0, "");


			chartPanel = new ChartPanel(scatterChart, false);
			chartPanel.setPreferredSize(new Dimension(plotWidth,plotHeight));
			innerPanel.add(chartPanel);
		}


		// this is only a test for having more than one charts in a boxlayout


		// 2. residual on fit plot: residuals vs. predicted

		JFreeChart rfChart = chartFactory.getLineChart("Residual on Fit Plot", "Predicted", "Residuals", predicted, residuals, "noline");

		//JFreeChart rfChart = chartFactory.getQQChart("Residual on Fit Plot", "Predicted", "Residuals", " Predicted Value  " , predicted, residuals,  "At Residual = 0", 0, 0, "");

		chartPanel = new ChartPanel(rfChart, false);
		chartPanel.setPreferredSize(new Dimension(plotWidth,plotHeight));
		innerPanel.add(chartPanel);

		// 3. residual on fit plot: residuals vs. xData
		/*JFreeChart rxChart = chartFactory.getLineChart("Residual on covariate Plot", "xData", "Residuals", xData, residuals);
		chartPanel = new ChartPanel(rxChart, false);
		chartPanel.setPreferredSize(new Dimension(plotWidth,plotHeight));
		graphPanel.add(chartPanel);
		*/
		for (int i = 0; i < xDataArray.length; i++) {
			xData = xDataArray[i];
			independentHeader = independentHeaderArray[i];
			JFreeChart scatterChart = chartFactory.getLineChart("Residual on covariate Plot: Residuals vs. " + independentHeader, independentHeader, "Residuals", xData, yData, "noline");//getChart(title, xlabel, ylabel, xdata,ydata)
			//JFreeChart qqChart = chartFactory.getLineChart("Residual Normal QQ Plot", "Theoretical Quantiles", "Standardized Residuals", sortedStandardizedNormalQuantiles, sortedStandardizedResiduals, "noline");

			//JFreeChart scatterChart = chartFactory.getQQChart("Residual on Covariate Plot: Residuals vs. " + independentHeader, independentHeader, "Residuals", "Residuals" , xData, residuals,  "At Residual = 0", 0, 0, "noline");

			chartPanel = new ChartPanel(scatterChart, false);
			chartPanel.setPreferredSize(new Dimension(plotWidth,plotHeight));
			innerPanel.add(chartPanel);
		}
		// 4. Normal QQ plot: need residuals and standardized normal scores
		//JFreeChart qqChart = chartFactory.getLineChart("Residual Normal QQ Plot", "Theoretical Quantiles", "Standardized Residuals", sortedStandardizedNormalQuantiles, sortedStandardizedResiduals);

		//JFreeChart qqChart = chartFactory.getQQChart("Residual Normal QQ Plot", "Theoretical Quantiles", "Standardized Residuals", "", sortedStandardizedNormalQuantiles, sortedStandardizedResiduals, "", 0, 0, "");
		JFreeChart qqChart = chartFactory.getLineChart("Residual Normal QQ Plot", "Theoretical Quantiles", "Standardized Residuals", sortedStandardizedNormalQuantiles, sortedStandardizedResiduals, "noline");
		chartPanel = new ChartPanel(qqChart, false);
		chartPanel.setPreferredSize(new Dimension(plotWidth,plotHeight));
		innerPanel.add(chartPanel);

		graphPanel.validate();
	}
	protected void resetGraph()
	{

		JFreeChart chart = chartFactory.createChart(); // an empty  chart
		ChartPanel chartPanel = new ChartPanel(chart, false);
		chartPanel.setPreferredSize(new Dimension(400,300));
		graphPanel.removeAll();
		graphPanel.add(chartPanel);
		graphPanel.validate();

	}
	public String getOnlineDescription(){
		return onlineDescription;
	}
}