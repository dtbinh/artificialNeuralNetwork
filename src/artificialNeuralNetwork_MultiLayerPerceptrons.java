/********************************** Artificial neural network ***********************************
* TODO:	Description, Header, Comments, TechDoc, Aufräumen
************************************************************************************************/
import java.util.ArrayList;

/************************************************************************************************
* class MultiLayerPerceptron:
* automatically builds a multi-layer perceptron, using neurons with logistic function, see
* description of constructors for more details
* Max. number of neurons per layer: 32
************************************************************************************************/
class MultiLayerPerceptron
{
private
	Neuron[] inputNeurons;
	Neuron[] hiddenLayer;
	ArrayList<Neuron[]> hiddenNeurons;
	Neuron[] outputNeurons;
	int[] neuronInputsLayer;
	ArrayList<int[]> neuronInputs;
	float[] outputVector;
	Connection[] inputConnections;
	Connection[] hiddenLyrConnections;
	ArrayList<Connection[]> hiddenConnections;
	Connection[] outputConnections;
	String inputTopology;
	String hiddenTopology;
	String outputTopology;
	// For training loop
	Boolean keepGoing;
	
	// Test variables
public static Boolean multiLayerPerceptronTest = false;
public static Boolean backpropagationTest = false;

public
	// Constructors
	/****************************************************************************************
	* MultiLayerPerceptron(int nrInputNeurons, int[] nrHiddenNeurons, int nrOutputNeurons,
	*			String inputTopology, String hiddenTopology, String outputTopology):
	* Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* each layer, and number of output neurons can (has to) be defined.
	* The type of connection between the layers has to be defined.
	* There must be at least 1 hidden layer
	/****************************************************************************************/
	MultiLayerPerceptron(int nrInputNeurons, int[] nrHiddenNeurons, int nrOutputNeurons,
							String inputTopology, String hiddenTopology, String outputTopology){
	
		neuronInputs = new ArrayList<int[]>();
		
		hiddenNeurons = new ArrayList<Neuron[]>();
		hiddenConnections = new ArrayList<Connection[]>();
		
		if (inputTopology == null)
			inputTopology = new String("each");
		else
			this.inputTopology = inputTopology;
		
		if (hiddenTopology == null)
			hiddenTopology = new String("each");
		else
			this.hiddenTopology = hiddenTopology;
		
		if (outputTopology == null)
			outputTopology = new String("each");
		else
			this.outputTopology = outputTopology;
		
		// Just to prevent wrong usage
		if (nrInputNeurons < 1)
			nrInputNeurons = 1;
			
		if (nrHiddenNeurons.length < 1)
			nrHiddenNeurons = new int[1];
		
		for (int i = 0; i < nrHiddenNeurons.length; i++){
			if (nrHiddenNeurons[i] < 1)
				nrHiddenNeurons[i] = 1;
		}

		if (nrOutputNeurons < 1)
			nrOutputNeurons = 1;
		
		// Creation of connections and calculation of needed inputs in hidden layer 0
		/*** Input layer ***/
		switch (inputTopology){
		
		// Every input neuron will be connected to the nearest hidden neuron
		case "one":
			
			if (nrInputNeurons >= nrHiddenNeurons[0]){
				
				// Needed inputs in 1st hidden layer
				neuronInputsLayer = new int[nrHiddenNeurons[0]];
				
				// Range of hidden neurons with one more input
				int range = nrInputNeurons % nrHiddenNeurons[0];
				int rangeStart  = (nrHiddenNeurons[0] - ((nrHiddenNeurons[0] - range) / 2)) - range;
				
				// For every neuron in hidden layer 0
				for (int m = 0; m < nrHiddenNeurons[0]; m++){
					
					// We need more connections in the middle
					if ((nrInputNeurons % nrHiddenNeurons[0]) != 0){
						
						// DEBUG
//						System.out.println("range: " + range);
//						System.out.println("rangeStart  = ("+nrHiddenNeurons[0]+" - 1) - (("+nrHiddenNeurons[0]+" - "+range+") / 2)");
						
						/***
						 * 		O
						 * 		I -> rangeStart
						 * 		I
						 * 		I -> rangeStart + range
						 * 		O -> n.length-1
						 * 
						 * 		-> m.length - range = Anzahl Neuronen ohne extra Input
						 * 		-> (m.length - range) / 2 = Anzahl Neuronen ohne extra Input unter-/überhalb range
						 * 		=> rangeStart  = (m.length - ((m.length-range) / 2)) - range
						 */
						
						// Inside the range of the neurons with one more connection
						if (m >= rangeStart && m < (rangeStart + range))
							neuronInputsLayer[m] = (nrInputNeurons / nrHiddenNeurons[0]) + 1;
						
						// Outside the range
						else 
							neuronInputsLayer[m] = nrInputNeurons / nrHiddenNeurons[0];
					}// if ((nrInputNeurons % nrHiddenNeurons[0]) != 0)
					
					// Whole-number ratio
					else 
						neuronInputsLayer[m] = nrInputNeurons / nrHiddenNeurons[0];
				}// for (int m = 0; m < nrHiddenNeurons[0]; m++)
					
				neuronInputs.add(neuronInputsLayer);

				inputConnections = new Connection[nrInputNeurons];
			}// if (nrInputNeurons >= nrHiddenNeurons[0])
				
			// Every hidden neuron will be connected to the nearest input neuron
			else {
				
				// Needed inputs in 1st hidden layer
				neuronInputsLayer = new int[nrHiddenNeurons[0]];
				
				// For every neuron in hidden layer 0...
				for (int m = 0; m < nrHiddenNeurons[0]; m++){
					
					// ...we just need one input
					neuronInputsLayer[m] = 1;
				}
					
				neuronInputs.add(neuronInputsLayer);

				inputConnections = new Connection[nrHiddenNeurons[0]];
			}// else (if (nrInputNeurons >= nrHiddenNeurons[0]))
			break;// case "one":
			
		// The hidden neurons will be split into two groups
		// Every input neuron will be connected to every neuron of the nearest group
		case "twoGroups":
			
			// Prevent wrong usage
			// Two groups with one input neuron makes no sense
			if (nrInputNeurons == 1)
				nrInputNeurons++;
			
			// and with just on hidden neuron neither
			if (nrHiddenNeurons[0] == 1)
				nrHiddenNeurons[0]++;
			
			int neededConnections = (nrHiddenNeurons[0]/2) * nrInputNeurons;
			
			// The upper group will have one more neuron
			if (nrHiddenNeurons[0] % 2 != 0){
				
				// Input neurons will be split at nrInputNeurons/2
				if (nrInputNeurons % 2 == 0)
					neededConnections += nrInputNeurons/2;
				else
					neededConnections += (nrInputNeurons/2) + 1;
			}// if (nrHiddenNeurons[0] % 2 != 0)
				
			inputConnections = new Connection[neededConnections];
			
			// Needed inputs in 1st hidden layer
			neuronInputsLayer = new int[nrHiddenNeurons[0]];
			
			// For every neuron in hidden layer 0
			for (int m = 0; m < nrHiddenNeurons[0]; m++){
				
				// The upper group has one input more if there
				// is an odd amount of input neurons
				if (((nrInputNeurons % 2) != 0 ) && (m >= (nrHiddenNeurons[0]/ 2)))
					neuronInputsLayer[m] = (nrInputNeurons/2) + 1;
				
				else
					neuronInputsLayer[m] = nrInputNeurons/2;
			}// for (int m = 0; m < nrHiddenNeurons[0]; m++)
			
			neuronInputs.add(neuronInputsLayer);
			break;// case "twoGroups":

		// "each": Every neuron of the current layer will be connected to every neuron of the following layer
		default:
			
			inputConnections = new Connection[nrInputNeurons * nrHiddenNeurons[0]];
			
			// Needed inputs in 1st hidden layer
			neuronInputsLayer = new int[nrHiddenNeurons[0]];
			
			// For every neuron in hidden layer 0
			for (int m = 0; m < nrHiddenNeurons[0]; m++)
				neuronInputsLayer[m] = nrInputNeurons;
			
			neuronInputs.add(neuronInputsLayer);
		}// switch (inputTopology)
		
		inputNeurons = new Neuron[nrInputNeurons];
		
		// Each input neuron has just 1 input
		for (int i = 0; i < nrInputNeurons; i++){
			inputNeurons[i] = new Neuron(1);
			
			// Initialise thresholds
			// (to 8 so the output is almost 0)
			inputNeurons[i].setThreshold(8);
		}
		
		/*** Hidden layer(s) ***/
		if (nrHiddenNeurons.length == 1){
			hiddenLayer  = new Neuron[nrHiddenNeurons[0]];
			
			hiddenNeurons.add(hiddenLayer);
		}// if (nrHiddenNeurons.length == 1)
			
		else {
			for (int lyr = 0; lyr < nrHiddenNeurons.length; lyr++){
				hiddenLayer = new Neuron[nrHiddenNeurons[lyr]];

				hiddenNeurons.add(hiddenLayer);
			}// for (int lyr = 0; lyr < nrHiddenNeurons.length; lyr++)
			
			// Creation of hidden connections and 
			// calculation of needed inputs in next layer
			switch (hiddenTopology){
			
			// Every neuron will be connected to the nearest neuron in the next layer
			case "one":
				
				// For every layer
				for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++){
					
					if (nrHiddenNeurons[lyr] >= nrHiddenNeurons[lyr+1]){
						
						// Needed inputs in next hidden layer
						neuronInputsLayer = new int[nrHiddenNeurons[lyr+1]];

						// Range of neurons in next layer with one more input
						int range = nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1];
						int rangeStart  = (nrHiddenNeurons[lyr+1] - ((nrHiddenNeurons[lyr+1] - range) / 2)) - range;
						
						// For every neuron in next hidden layer
						for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++){
							
							// We need more connections in the middle
							if ((nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0){
								
								// Inside the range of the neurons with one more connection
								if (m >= rangeStart && m < (rangeStart + range))
									neuronInputsLayer[m] = (nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1]) + 1;
								
								// Outside the range
								else 
									neuronInputsLayer[m] = nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1];
							}
							
							// Whole-number ratio
							else 
								neuronInputsLayer[m] = nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1];
						}
							
						hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr]];
					}
						
					// Every neuron in the next layer will be connected to the nearest neuron in the current layer
					else {
						
						// Needed inputs in next hidden layer
						neuronInputsLayer = new int[nrHiddenNeurons[lyr+1]];
						
						// For every neuron in next hidden layer...
						for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++)
							
							// ...we just need one input
							neuronInputsLayer[m] = 1;
							
						hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr+1]];
					}
					
					neuronInputs.add(neuronInputsLayer);
						
					hiddenConnections.add(hiddenLyrConnections);
				}
				break;// case "one"
				
			// Every neuron will be connected to two neurons of the following layer,
			// the neuron a position up and the neuron a position down
			case "cross":
			
				for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++){

					if (nrHiddenNeurons[lyr] >= nrHiddenNeurons[lyr+1]){
						
						// Needed inputs in next hidden layer
						neuronInputsLayer = new int[nrHiddenNeurons[lyr+1]];

						// Range of neurons in next layer with one more input
						// (There are two overlapping ranges)
						int range = nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1];
						// First neuron in the range
						int rangeStart  = (nrHiddenNeurons[lyr+1] - ((nrHiddenNeurons[lyr+1] - range) / 2)) - range;
						int rangeUpperEnd = 0;
						
						if ((rangeStart+1) + range >= nrHiddenNeurons[lyr+1])
							rangeUpperEnd = ((rangeStart+1) + range) - nrHiddenNeurons[lyr+1];
						
						// DEBUG
//						System.out.println("\t\trange: " + range);
//						System.out.println("\t\trangeStart: " + rangeStart);
//						System.out.println("\t\trangeUpperEnd: " + rangeUpperEnd);
						
						// For every neuron in next hidden layer
						for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++){
							
							neuronInputsLayer[m] = 2 * (nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1]);
							
							// We need more connections
							if ((nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0){
								
								if ((rangeUpperEnd > 0) && (m < rangeUpperEnd))
									neuronInputsLayer[m]++;
									
								// Lower range of the neurons with one more connection
								if (m >= (rangeStart-1) && m < ((rangeStart-1) + range))
									neuronInputsLayer[m]++;
								
								// Upper range of the neurons with one more connection
								if (m >= (rangeStart+1) && m < ((rangeStart+1) + range))
									neuronInputsLayer[m]++;
							}// if ((nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0)
						}// for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++)
							
						hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr] * 2];
					}// if (nrHiddenNeurons[lyr] >= nrHiddenNeurons[lyr+1])
						
					else {
						
						// Needed inputs in next hidden layer
						neuronInputsLayer = new int[nrHiddenNeurons[lyr+1]];
						
						// For every neuron in next hidden layer...
						for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++)
							
							// ...we just need two inputs
							neuronInputsLayer[m] = 2;
							
						hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr+1] * 2];
					}// else (if (nrHiddenNeurons[lyr] >= nrHiddenNeurons[lyr+1]))
					
					neuronInputs.add(neuronInputsLayer);
						
					hiddenConnections.add(hiddenLyrConnections);
				}// for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++)
				break;// case "cross"
				
			// Every neuron will be connected to two neurons of the following layer,
			// the neuron at same position and the neuron a position up
			case "zigzag":
				
				for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++){

					if (nrHiddenNeurons[lyr] >= nrHiddenNeurons[lyr+1]){
						
						// Needed inputs in next hidden layer
						neuronInputsLayer = new int[nrHiddenNeurons[lyr+1]];

						// Range of neurons in next layer with one more input
						// (There are two overlapping ranges)
						int range = nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1];
						// First neuron in the range
						int rangeStart  = (nrHiddenNeurons[lyr+1] - ((nrHiddenNeurons[lyr+1] - range) / 2)) - range;
						int rangeUpperEnd = 0;
						
						if ((rangeStart+1) + range >= nrHiddenNeurons[lyr+1])
							rangeUpperEnd = ((rangeStart+1) + range) - nrHiddenNeurons[lyr+1];
						
						// For every neuron in next hidden layer
						for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++){
							
							neuronInputsLayer[m] = 2 * (nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1]);
							
							// We need more connections
							if ((nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0){
								
								if ((rangeUpperEnd > 0) && (m < rangeUpperEnd))
									neuronInputsLayer[m]++;
									
								// Lower range of the neurons with one more connection
								if (m >= rangeStart && m < (rangeStart + range))
									neuronInputsLayer[m]++;
								
								// Upper range of the neurons with one more connection
								if (m >= (rangeStart+1) && m < ((rangeStart+1) + range))
									neuronInputsLayer[m]++;
							}// if ((nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0)
						}// for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++)
							
						hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr] * 2];
					}// if (nrHiddenNeurons[lyr] >= nrHiddenNeurons[lyr+1])
						
					else {
						
						// Needed inputs in next hidden layer
						neuronInputsLayer = new int[nrHiddenNeurons[lyr+1]];
						
						// For every neuron in next hidden layer...
						for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++)
							
							// ...we just need two inputs
							neuronInputsLayer[m] = 2;
							
						hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr+1] * 2];
					}// else (if (nrHiddenNeurons[lyr] >= nrHiddenNeurons[lyr+1]))
					
					neuronInputs.add(neuronInputsLayer);
						
					hiddenConnections.add(hiddenLyrConnections);
				}// for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++)
				break;// case "zigzag"
			
			// "each": Every neuron of the current layer will be connected to every neuron of the following layer
			default: 
				
				for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++){
					
					hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr] * nrHiddenNeurons[lyr+1]];
					
					hiddenConnections.add(hiddenLyrConnections);
					
					// Needed inputs in 1st hidden layer
					neuronInputsLayer = new int[nrHiddenNeurons[lyr+1]];
					
					// For every neuron in next hidden layer
					for (int m = 0; m < nrHiddenNeurons[lyr+1]; m++)
						neuronInputsLayer[m] = nrHiddenNeurons[lyr];
					
					neuronInputs.add(neuronInputsLayer);
				}// for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++)
			}// switch (hiddenTopology)
		}// else if (nrHiddenNeurons.length == 1)
					
		// Set number of inputs; resp. number of connections
		// to the previous layer
		for (int l = 0; l < hiddenNeurons.size(); l++){
			
			for (int n = 0; n < hiddenNeurons.get(l).length; n++){
				hiddenNeurons.get(l)[n] = new Neuron(neuronInputs.get(l)[n]);
					
				// Initialise thresholds
				hiddenNeurons.get(l)[n].setThreshold(8);
			}
		}// for (int l = 0; l < hiddenNeurons.size(); l++)
		 
		/*** Output layer ***/		
		outputNeurons = new Neuron[nrOutputNeurons];
		
		outputVector = new float[nrOutputNeurons];
		
		switch (outputTopology){
		
		case "one":
			// Every hidden neuron will be connected to the nearest output neuron
			
			if (nrHiddenNeurons[nrHiddenNeurons.length - 1] >= nrOutputNeurons){
				
				// Needed inputs in output layer
				neuronInputsLayer = new int[nrOutputNeurons];
				
				// Range of output neurons with one more input
				int range = nrHiddenNeurons[nrHiddenNeurons.length - 1] % nrOutputNeurons;
				int rangeStart  = (nrOutputNeurons - ((nrOutputNeurons - range) / 2)) - range;
				
				// For every neuron in output layer
				for (int m = 0; m < nrOutputNeurons; m++){
					
					// We need more connections in the middle
					if ((nrHiddenNeurons[nrHiddenNeurons.length - 1] % nrOutputNeurons) != 0){
						
						// Inside the range of the neurons with one more connection
						if (m >= rangeStart && m < (rangeStart + range))
							neuronInputsLayer[m] = (nrHiddenNeurons[nrHiddenNeurons.length - 1] / nrOutputNeurons) + 1;
						
						// Outside the range
						else 
							neuronInputsLayer[m] = nrHiddenNeurons[nrHiddenNeurons.length - 1] / nrOutputNeurons;
					}// if ((nrHiddenNeurons[nrHiddenNeurons.length - 1] % nrOutputNeurons) != 0)
					
					// Whole-number ratio
					else 
						neuronInputsLayer[m] = nrHiddenNeurons[nrHiddenNeurons.length - 1] / nrOutputNeurons;
				}// for (int m = 0; m < nrOutputNeurons; m++)
					
				neuronInputs.add(neuronInputsLayer);

				outputConnections = new Connection[nrHiddenNeurons[nrHiddenNeurons.length - 1]];
			}// if (nrHiddenNeurons[nrHiddenNeurons.length - 1] >= nrOutputNeurons)
				
			// Every hidden neuron will be connected to the nearest input neuron
			else {
				
				// Needed inputs in output layer
				neuronInputsLayer = new int[nrOutputNeurons];
				
				// For every neuron in output...
				for (int m = 0; m < nrOutputNeurons; m++){
					
					// ...we just need one input
					neuronInputsLayer[m] = 1;
				}
					
				neuronInputs.add(neuronInputsLayer);

				outputConnections = new Connection[nrOutputNeurons];
			}// else (if (nrHiddenNeurons[nrHiddenNeurons.length - 1] >= nrOutputNeurons))
			break;// case "one"
			
		//TODO case "twoGroups": ???
		
		// "each": Every neuron of the last hidden layer will be connected to every output neuron
		default:
			
			outputConnections = new Connection[nrOutputNeurons * nrHiddenNeurons[nrHiddenNeurons.length - 1]];
			
			// Needed inputs in output layer
			neuronInputsLayer = new int[nrOutputNeurons];
			
			// For every neuron in output layer
			for (int m = 0; m < nrOutputNeurons; m++)
				neuronInputsLayer[m] = nrHiddenNeurons[nrHiddenNeurons.length - 1];
			
			neuronInputs.add(neuronInputsLayer);			
		}// switch (outputTopology)
		
		for (int i = 0; i < outputNeurons.length; i++){
			outputNeurons[i] = new Neuron(neuronInputs.get(nrHiddenNeurons.length)[i]);
			
			// Initialise thresholds
			outputNeurons[i].setThreshold(8);
			
			// Set output to 0
			outputVector[i] = 0;
		}
		
		/*** Connections ***/
		// Connections between input layer and 1st hidden layer
		
		// Test output: multilayerPerceptronTest
		if (multiLayerPerceptronTest)
			System.out.println("\tinputConnection\t| inputNeuron | hiddenNeuron\tinp|pos");
		
		switch (inputTopology){
		
		case "one":

			if (inputNeurons.length >= hiddenNeurons.get(0).length){

				// Neuron of the next layer
				int m = 0;
				
				// Input connection is going to
				int i = 0;
				
				// From each input neuron...
				for (int n = 0; n < inputNeurons.length; n++){
					
					// Go to next neuron in next layer if all inputs are connected
					if (i >= hiddenNeurons.get(0)[m].getNumberOfInputs()){
						i = 0;
						m++;
					}
					 
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+n+"]\t|\t["+n+"]\t [0]["+m+"]\t\t "+i+" | "+m);
					
					// ...to a hidden neuron (initialise the connection weight with 1)
					inputConnections[n] = new Connection(inputNeurons[n], hiddenNeurons.get(0)[m], i, 1, m);
					
					// Go to next input
					i++;
				}// for (int n = 0; n < inputNeurons.length; n++)
			}// if (inputNeurons.length >= hiddenNeurons.get(0).length)
			
			else {

				// Neuron of the current layer
				int n = 0;
				
				// Range of input neurons with one more connection
				int range = (hiddenNeurons.get(0).length % inputNeurons.length)
								* ((hiddenNeurons.get(0).length / inputNeurons.length) + 1);
				int rangeStart  = (hiddenNeurons.get(0).length - ((hiddenNeurons.get(0).length - range) / 2)) - range;
				
				// Number of connections to next layer
				int c;
				
				// Current connection to next layer
				int c_i = 0;
				
				// From each hidden neuron...
				for (int m = 0; m < hiddenNeurons.get(0).length; m++){
					
					// We need more connections in the middle
					if ((hiddenNeurons.get(0).length % inputNeurons.length) != 0){
						
						// Inside the range of the neurons with one more connection
						if (m > rangeStart && m <= (rangeStart + range))
							c = (hiddenNeurons.get(0).length / inputNeurons.length) + 1;
						
						// Outside the range
						else 
							c = hiddenNeurons.get(0).length / inputNeurons.length;
					}// if ((hiddenNeurons.get(0).length % inputNeurons.length) != 0)
					
					// Whole-number ratio
					else 
						c = hiddenNeurons.get(0).length / inputNeurons.length;
					
					// Go to next neuron of the current layer if all connections are done
					if (c_i >= c){
						n++;
						
						c_i = 0;
					}
					
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+m+"]\t|\t["+n+"]\t [0]["+m+"]\t\t 0 | "+m);
					
					// ...to a hidden neuron (initialise the connection weight with 1)
					inputConnections[m] = new Connection(inputNeurons[n], hiddenNeurons.get(0)[m], 0, 1, m);
					
					// Next connection
					c_i++;
				}// for (int m = 0; m < hiddenNeurons.get(0).length; m++)
			}// else (if (inputNeurons.length >= hiddenNeurons.get(0).length))
			break;// case "one"
			
		case "twoGroups":
			
			// Split the groups here
			int groupLimit;
			
			int positionInLayer = 0;
			
			// We don't need to differentiate between odd and even number of neurons here
			// the neuron more is in the upper group
			groupLimit = hiddenNeurons.get(0).length/2;

			// From each input neuron...
			for (int n = 0; n < inputNeurons.length; n++){
				
				// ...to each neuron in the nearest group
				// Lower group
				if (n < (inputNeurons.length/2)){
						
					for (int m = 0; m < groupLimit; m++){
						
						// Test output: multilayerPerceptronTest
						if (multiLayerPerceptronTest)
							System.out.println("\t\t["+positionInLayer+"]\t|\t["+n+"]\t [0]["+m+"]\t\t "+n+" | "+m);
						
						// Note: The input is select by the number of the input neuron
						inputConnections[positionInLayer] = new Connection(inputNeurons[n],
								hiddenNeurons.get(0)[m], n, 1, m);
						
						positionInLayer++;
					}// for (int m = 0; m < groupLimit; m++)
				}// if (n < (inputNeurons.length/2))
				
				// Upper group
				else {
					
					for (int m = groupLimit; m < hiddenNeurons.get(0).length; m++){
						
						// Test output: multilayerPerceptronTest
						if (multiLayerPerceptronTest)
							System.out.println("\t\t["+positionInLayer+"]\t|\t["+n+"]\t [0]["+m+"]\t\t "+(n-(inputNeurons.length/2))+" | "+m);
						
						// Note: The input is select by the number of the input neuron - the half of the input neurons
						inputConnections[positionInLayer] = new Connection(inputNeurons[n],
								hiddenNeurons.get(0)[m], (n-(inputNeurons.length/2)), 1, m);
						
						positionInLayer++;
					}// for (int m = groupLimit; m < hiddenNeurons.get(0).length; m++)
				}// else (if (n < (inputNeurons.length/2)))
			}// for (int n = 0; n < inputNeurons.length; n++)
			break;// case "twoGroups"
		
		// "each"
		default:
			
			int posInLayer = 0;
			
			// From each input neuron...
			for (int n = 0; n < inputNeurons.length; n++){
			
				// ...to each neuron of the 1st hidden layer
				for (int conn = 0; conn < hiddenNeurons.get(0).length; conn++){
					
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+posInLayer+"]\t|\t["+n+"]\t [0]["+conn+"]\t\t "+n+" | "+conn);
					
					inputConnections[posInLayer] = new Connection(inputNeurons[n],
							hiddenNeurons.get(0)[conn], n, 1, conn);
					
					posInLayer++;
				}// for (int conn = 0; conn < hiddenNeurons.get(0).length; conn++)
			}// for (int n = 0; n < inputNeurons.length; n++)
		}// switch (inputTopology)
		
		// Connections between hidden layers
		if (hiddenNeurons.size() > 1){
				
			// Test output: multilayerPerceptronTest
			if (multiLayerPerceptronTest)
				System.out.println("\thiddenConnection| hiddenNeuron | hiddenNeuron\tinp|pos");
			
			switch (hiddenTopology){
			
			case "one":
				
				// For each layer
				for (int l = 0; l < (hiddenNeurons.size() - 1); l++){
					
					if (hiddenNeurons.get(l).length >= hiddenNeurons.get(l+1).length){

						// Neuron of the next layer
						int m = 0;
						
						// Input connection is going to
						int i = 0;
						
						// From each neuron in the current layer...
						for (int n = 0; n < hiddenNeurons.get(l).length; n++){
							
							// Go to next neuron in next layer if all inputs are connected
							if (i >= hiddenNeurons.get(l+1)[m].getNumberOfInputs()){
								i = 0;
								m++;
							}
							 
							// Test output: multilayerPerceptronTest
							if (multiLayerPerceptronTest)
								System.out.println("\t\t["+n+"]\t|\t["+l+"]["+n+"]\t ["+(l+1)+"]["+m+"]\t\t "+i+" | "+m);
							
							// ...to a hidden neuron (initialise the connection weight with 1)
							hiddenConnections.get(l)[n] = new Connection(hiddenNeurons.get(l)[n], hiddenNeurons.get(l+1)[m], i, 1, m);
							
							// Go to next input
							i++;
						}// for (int n = 0; n < hiddenNeurons.get(l).length; n++)
					}// if (hiddenNeurons.get(l).length >= hiddenNeurons.get(l+1).length)
					
					else {

						// Neuron of the current layer
						int n = 0;
						
						// Range of input neurons with one more connection
						int range = (hiddenNeurons.get(l+1).length % hiddenNeurons.get(l).length)
										* ((hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length) + 1);
						int rangeStart  = (hiddenNeurons.get(l+1).length - ((hiddenNeurons.get(l+1).length - range) / 2)) - range;
						
						// Number of connections to next layer
						int c;
						
						// Current connection to next layer
						int c_i = 0;
						
						// From each hidden neuron...
						for (int m = 0; m < hiddenNeurons.get(l+1).length; m++){
							
							// We need more connections in the middle
							if ((hiddenNeurons.get(l+1).length % hiddenNeurons.get(l).length) != 0){
								
								// Inside the range of the neurons with one more connection
								if (m > rangeStart && m <= (rangeStart + range))
									c = (hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length) + 1;
								
								// Outside the range
								else 
									c = hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length;
							}// if ((hiddenNeurons.get(l+1).length % hiddenNeurons.get(l).length) != 0)
							
							// Whole-number ratio
							else 
								c = hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length;
							
							// Go to next neuron of the current layer if all connections are done
							if (c_i >= c){
								n++;
								
								c_i = 0;
							}
							
							// Test output: multilayerPerceptronTest
							if (multiLayerPerceptronTest)
								System.out.println("\t\t["+m+"]\t|\t["+l+"]["+n+"]\t ["+(l+1)+"]["+m+"]\t\t 0 | "+m);
							
							// ...to a hidden neuron (initialise the connection weight with 1)
							hiddenConnections.get(l)[m] = new Connection(hiddenNeurons.get(l)[n], hiddenNeurons.get(l+1)[m], 0, 1, m);
							
							// Next connection
							c_i++;
						}// for (int m = 0; m < hiddenNeurons.get(l+1).length; m++)
					}// else (if (hiddenNeurons.get(l).length >= hiddenNeurons.get(l+1).length))
				}// for (int l = 0; l < (hiddenNeurons.size() - 1); l++)
				break;// case "one"

			case "cross":
				
				// For each layer
				for (int l = 0; l < (hiddenNeurons.size() - 1); l++){
					
					if (hiddenNeurons.get(l).length >= hiddenNeurons.get(l+1).length){

						// Neuron of the next layer
						int m = 0;
						
						// Input connection is going to
						int i;
						
						// Number of neurons with connection to a neuron in next layer with more inputs
						int range = hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length;
						int rangeStart  = (hiddenNeurons.get(l).length - ((hiddenNeurons.get(l).length - range) / 2))
											- range;
						
						// From each neuron in the current layer...
						for (int n = 0; n < hiddenNeurons.get(l).length; n++){
							
							// We need more connections in the middle
							if ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length) != 0){
								
								// Inside the range of neurons with one more connection
								// TODO: Pretty sure condition and calculation aren't always correct
								if (n >= rangeStart)
									m = n * hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length;
								
								// Outside the range
								// Note: n.length/m.length=int (eg. 8/7 = 1)! That's why it's working
								else 
									m = n / (hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length);
							}// if ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length) != 0)
							
							// Whole-number ratio
							else 
								m = n / (hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length);
							
							// Choose inputs
							// TODO: Find universal i =
							if ((n > 0) && (hiddenConnections.get(l)[(n-1)*2].getPositionNeuronTo() == (m-1)))
								i = 2;
							else
								i = 0;
							
							// ...to the neuron 1 position down in the next hidden layer...
							if (m > 0){
								
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+(n*2)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"]["
													+(m-1)+"]\t\t "+i+" | "+(m-1));

								hiddenConnections.get(l)[n*2] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[m-1], i, 1, (m-1));
							}// if (m > 0)
							
							else {
								
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+(n*2)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"]["
														+(hiddenNeurons.get(l+1).length-1)+"]\t\t "+i+" | "+
														(hiddenNeurons.get(l+1).length-1));	
								
								hiddenConnections.get(l)[n*2] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[hiddenNeurons.get(l+1).length-1], i, 1,
									hiddenNeurons.get(l+1).length-1);
							}// else (if (m > 0))
							
							// TODO: Ugly hack, we need to find something better
							if ((n > 0) && (hiddenConnections.get(l)[(n-1)*2].getPositionNeuronTo() == (m-1)))
								i--;
							
							// ...and the neuron 1 position up in the next hidden layer
							if (m < hiddenNeurons.get(l+1).length - 1){
								
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"]["
													+(m+1)+"]\t\t "+(i+1)+" | "+(m+1));
					
								hiddenConnections.get(l)[(n*2)+1] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[m+1], (i+1), 1, (m+1));
							}// if (m < hiddenNeurons.get(l+1).length - 1)
							
							else {
								
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"][0]" +
														"\t\t "+(i+1)+" | 0");	
								
								hiddenConnections.get(l)[(n*2)+1] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[0], (i+1), 1, 0);
							}// else (if (m < hiddenNeurons.get(l+1).length - 1))
						}// for (int n = 0; n < hiddenNeurons.get(l).length; n++)
					}// if (hiddenNeurons.get(l).length >= hiddenNeurons.get(l+1).length)
					
					else {

						// Neuron in current layer
						int n = 0;
						
						// Number of neurons in next layer with more inputs
						int range = hiddenNeurons.get(l+1).length % hiddenNeurons.get(l).length;
						int rangeStart  = (hiddenNeurons.get(l+1).length - ((hiddenNeurons.get(l+1).length - range) / 2))
											- range;
						
						// From each hidden neuron (of the next layer)...
						for (int m = 0; m < hiddenNeurons.get(l+1).length; m++){
							
							// We need more connections in the middle
							if ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length) != 0){
								
								// Inside the range of neurons with one more connection
								if (m >= rangeStart)
									n = m * hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length;
								
								// Outside the range
								else 
									n= m / (hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length);
							}// if ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length) != 0)
							
							// Whole-number ratio
							else 
								n = m / (hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length);
							
							// ...to the neuron 1 position down...
							if (n > 0){
								
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+(m*2)+"]\t|\t["+(l)+"]["+(n-1)+"]\t ["+(l+1)+"]["
													+m+"]\t\t 0 | "+m);							
					
								hiddenConnections.get(l)[m*2] = new Connection(hiddenNeurons.get(l)[n-1],
									hiddenNeurons.get(l+1)[m], 0, 1, m);
							}// if (n > 0)
							
							else {
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+(m*2)+"]\t|\t["+(l)+"]["+(hiddenNeurons.get(l).length-1)
														+"]\t ["+(l+1)+"]["+m+"]\t\t 0 | "+m);	
								
								hiddenConnections.get(l)[m*2] = new Connection(hiddenNeurons.get(l)[hiddenNeurons.get(l).length-1],
									hiddenNeurons.get(l+1)[m], 0, 1, m);
							}// else (if (m > 0))
							
							// ...and the neuron 1 position up (in the current layer)
							if (n < hiddenNeurons.get(l).length - 1){
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+((m*2)+1)+"]\t|\t["+(l)+"]["+(n+1)+"]\t ["+(l+1)+"]["
													+m+"]\t\t "+1+" | "+m);							
					
								hiddenConnections.get(l)[(m*2)+1] = new Connection(hiddenNeurons.get(l)[n+1],
									hiddenNeurons.get(l+1)[m], 1, 1, (m));
							}// if (m < hiddenNeurons.get(l).length - 1)
							
							else {
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+((m*2)+1)+"]\t|\t["+(l)+"][0]\t ["+(l+1)+"]["
													+m+"]\t\t "+1+" | "+m);	
								
								hiddenConnections.get(l)[(m*2)+1] = new Connection(hiddenNeurons.get(l)[0],
									hiddenNeurons.get(l+1)[m], 1, 1, m);
							}// else (if (m < hiddenNeurons.get(l).length - 1))
						}// for (int m = 0; m < hiddenNeurons.get(l+1).length; m++)
					}// else (if (hiddenNeurons.get(l).length >= hiddenNeurons.get(l+1).length))
				}// for (int l = 0; l < (hiddenNeurons.size() - 1); l++)
				break;// case "cross"

			case "zigzag":
				
				// For each layer
				for (int l = 0; l < (hiddenNeurons.size() - 1); l++){
					
					if (hiddenNeurons.get(l).length >= hiddenNeurons.get(l+1).length){

						// Neuron of the next layer
						int m = 0;
						
						// Input connection is going to
						int i;
						
						// Number of neurons with connection to a neuron in next layer with more inputs
						int range = hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length;
						int rangeStart  = (hiddenNeurons.get(l).length - ((hiddenNeurons.get(l).length - range) / 2))
											- range;
						
						// From each neuron in the current layer...
						for (int n = 0; n < hiddenNeurons.get(l).length; n++){
							
							// We need more connections in the middle
							if ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length) != 0){
								
								// Inside the range of neurons with one more connection
								if (n >= rangeStart)
									m = n * hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length;
								
								// Outside the range
								else 
									m = n / (hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length);
							}// if ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length) != 0)
							
							// Whole-number ratio
							else 
								m = n / (hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length);
							
							// Choose input
							if ((n > 0) && (hiddenConnections.get(l)[(n-1)*2].getPositionNeuronTo() == m))
								i = 2;
							else
								i = 0;
			
							// Test output: multilayerPerceptronTest
							if (multiLayerPerceptronTest)
								System.out.println("\t\t["+(n*2)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"]["
												+(m)+"]\t\t "+i+" | "+m);	
							
							// ...to the neuron at the same position of the next hidden layer...
							hiddenConnections.get(l)[n*2] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[m], i, 1, m);
							
							// TODO: Not working with test case 10 (eg. outputneuron 3)
							// Check if same problem (pretty sure) with "cross"
							if ((n > 0) && (hiddenConnections.get(l)[(n-1)*2].getPositionNeuronTo() == m))
								i--;
							
							// ...and a neuron 1 position up of the next hidden layer
							if (m < hiddenNeurons.get(l+1).length - 1){
								
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"]["
													+(m+1)+"]\t\t "+(i+1)+" | "+(m+1));	
								
								hiddenConnections.get(l)[(n*2)+1] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[m+1], (i+1), 1, (m+1));
							}// if (m < hiddenNeurons.get(l+1).length - 1)
							
							else {
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+((n*2)+1)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"][0]\t\t "+(i+1)+" | 0");	
								
								hiddenConnections.get(l)[(n*2)+1] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[0], (i+1), 1, 0);
							}// else (if (m < hiddenNeurons.get(l+1).length - 1))
						}// for (int n = 0; n < hiddenNeurons.get(l).length; n++)
					}// if (hiddenNeurons.get(l).length >= hiddenNeurons.get(l+1).length)
					
					else {

						// Neuron in current layer
						int n = 0;
						
						// Number of neurons in next layer with more inputs
						int range = hiddenNeurons.get(l+1).length % hiddenNeurons.get(l).length;
						int rangeStart  = (hiddenNeurons.get(l+1).length - ((hiddenNeurons.get(l+1).length - range) / 2))
											- range;
						
						// From each hidden neuron (of the next layer)...
						for (int m = 0; m < hiddenNeurons.get(l+1).length; m++){
							
							// We need more connections in the middle
							if ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length) != 0){
								
								// Inside the range of neurons with one more connection
								if (m >= rangeStart)
									n = m * hiddenNeurons.get(l).length / hiddenNeurons.get(l+1).length;
								
								// Outside the range
								else 
									n= m / (hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length);
							}// if ((hiddenNeurons.get(l).length % hiddenNeurons.get(l+1).length) != 0)
							
							// Whole-number ratio
							else 
								n = m / (hiddenNeurons.get(l+1).length / hiddenNeurons.get(l).length);

							// Test output: multilayerPerceptronTest
							if (multiLayerPerceptronTest)
								System.out.println("\t\t["+(m*2)+"]\t|\t["+(l)+"]["+n+"]\t ["+(l+1)+"]["
												+m+"]\t\t 0 | "+m);	
							
							// ...to the neuron at the same position of the next hidden layer...
							hiddenConnections.get(l)[m*2] = new Connection(hiddenNeurons.get(l)[n],
									hiddenNeurons.get(l+1)[m], 0, 1, m);
							
							// ...and a neuron 1 position up of the next hidden layer
							if (n < hiddenNeurons.get(l).length - 1){
								
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+((m*2)+1)+"]\t|\t["+(l)+"]["+(n+1)+"]\t ["+(l+1)+"]["
													+m+"]\t\t 1 | "+m);	
								
								hiddenConnections.get(l)[(m*2)+1] = new Connection(hiddenNeurons.get(l)[n+1],
									hiddenNeurons.get(l+1)[m], 1, 1, m);
							}// if (n < hiddenNeurons.get(l).length - 1)
							
							else {
								// Test output: multilayerPerceptronTest
								if (multiLayerPerceptronTest)
									System.out.println("\t\t["+((m*2)+1)+"]\t|\t["+(l)+"][0]\t ["+(l+1)+"]["+m+"]\t\t 1 | "+m);	
								
								hiddenConnections.get(l)[(m*2)+1] = new Connection(hiddenNeurons.get(l)[0],
									hiddenNeurons.get(l+1)[m], 1, 1, m);
							}// else (if (n < hiddenNeurons.get(l).length - 1))
						}// for (int m = 0; m < hiddenNeurons.get(l+1).length; m++)
					}// else (if (hiddenNeurons.get(l).length >= hiddenNeurons.get(l+1).length))
				}// for (int l = 0; l < (hiddenNeurons.size() - 1); l++)
				break;// case "zigzag"
				
			default: // "each"
				
				int positionInLayer;
				
				// For each hidden layer...
				for (int layer = 0; layer < (hiddenNeurons.size() - 1); layer++){
					positionInLayer = 0;
					
					// ...from each hidden neuron...
					for (int n = 0; n < hiddenNeurons.get(layer).length; n++){
					
						// ...to each neuron of the next hidden layer
						for (int conn = 0; conn < hiddenNeurons.get(layer+1).length; conn++){
							
							// Test output: multilayerPerceptronTest
							if (multiLayerPerceptronTest)
								System.out.println("\t\t["+positionInLayer+"]\t|\t["+(layer)+"]["+n+"]\t ["+(layer+1)+"]["+conn+"]\t\t "+n+" | "+conn);	
							
							hiddenConnections.get(layer)[positionInLayer] = new Connection(hiddenNeurons.get(layer)[n],
									hiddenNeurons.get(layer+1)[conn], n, 1, conn);
							
							positionInLayer++;
						}// for (int conn = 0; conn < hiddenNeurons.get(layer+1).length; conn++)
					}// for (int n = 0; n < hiddenNeurons.get(layer).length; n++)
				}// for (int layer = 0; layer < (hiddenNeurons.size() - 1); layer++)
			}// switch (hiddenTopology)
		}// if (hiddenNeurons.size() > 1)
		
		// Connections between last hidden and output layer
		
		// Test output: multilayerPerceptronTest
		if (multiLayerPerceptronTest)
			System.out.println("\toutputConnection| hiddenNeuron | outputNeuron\tinp|pos");
						
		switch (outputTopology){
		
		case "one":
			if (hiddenNeurons.get(hiddenNeurons.size() - 1).length >= outputNeurons.length){

				// Neuron of the next layer
				int m = 0;
				
				// Input connection is going to
				int i = 0;
				
				// From each hidden neuron...
				for (int n = 0; n < hiddenNeurons.get(hiddenNeurons.size() - 1).length; n++){
					
					// Go to next neuron in next layer if all inputs are connected
					if (i >= outputNeurons[m].getNumberOfInputs()){
						i = 0;
						m++;
					}
					 
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+n+"]\t|\t["+n+"]\t [0]["+m+"]\t\t "+i+" | "+m);
					
					// ...to an output neuron (initialise the connection weight with 1)
					outputConnections[n] = new Connection(hiddenNeurons.get(hiddenNeurons.size() - 1)[n],
							outputNeurons[m], i, 1, m);
					
					// Go to next input
					i++;
				}// for (int n = 0; n < inputNeurons.length; n++)
			}// if (inputNeurons.length >= hiddenNeurons.get(0).length)
			
			else {

				// Neuron of the current layer
				int n = 0;
				
				// Range of input neurons with one more connection
				int range = (outputNeurons.length % hiddenNeurons.get(hiddenNeurons.size() - 1).length)
								* ((outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length) + 1);
				int rangeStart  = (outputNeurons.length - ((outputNeurons.length - range) / 2)) - range;
				
				// Number of connections to next layer
				int c;
				
				// Current connection to next layer
				int c_i = 0;
				
				// From each output neuron...
				for (int m = 0; m < outputNeurons.length; m++){
					
					// We need more connections in the middle
					if ((outputNeurons.length % hiddenNeurons.get(hiddenNeurons.size() - 1).length) != 0){
						
						// Inside the range of the neurons with one more connection
						if (m > rangeStart && m <= (rangeStart + range))
							c = (outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length) + 1;
						
						// Outside the range
						else 
							c = outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length;
					}// if ((outputNeurons.length % inputNeurons.length) != 0)
					
					// Whole-number ratio
					else 
						c = outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length;
					
					// Go to next neuron of the current layer if all connections are done
					if (c_i >= c){
						n++;
						
						c_i = 0;
					}
					
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+m+"]\t|\t["+n+"]\t [0]["+m+"]\t\t 0 | "+m);
					
					// ...to an output neuron (initialise the connection weight with 1)
					outputConnections[m] = new Connection(hiddenNeurons.get(hiddenNeurons.size() - 1)[n],
															outputNeurons[m], 0, 1, m);
					
					// Next connection
					c_i++;
				}// for (int m = 0; m < outputNeurons.length; m++)
			}// else (if (hiddenNeurons.get(hiddenNeurons.size() - 1).length >= outputNeurons.length))
			break;// case "one"
		
		default: // "each"

			int positionInLayer = 0;
			
			// From each neuron of the last hidden layer...			
			for (int n = 0; n < hiddenNeurons.get(hiddenNeurons.size() - 1).length; n++){
			
				// ...to each output neuron
				for (int m = 0; m < outputNeurons.length; m++){
					
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+positionInLayer+"]\t|\t["+(hiddenNeurons.size()-1)+"]["+n+"]\t ["+m+"]\t\t "+n+" | "+m);	
					
					outputConnections[positionInLayer] = new Connection(hiddenNeurons.get(hiddenNeurons.size() - 1)[n],
							outputNeurons[m], n, 1, m);
					
					positionInLayer++;
				}// for (int m = 0; m < outputNeurons.length; m++)
			}// for (int n = 0; n < hiddenNeurons.get(hiddenNeurons.size() - 1).length; n++)
		}// switch (outputTopology)
		
		keepGoing  = false;
	}// MultiLayerPerceptron(int nrInputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
		// int nrOutputNeurons, String inputTopology, String hiddenTopology)


	/****************************************************************************************
	* MultiLayerPerceptron(int nrInputNeurons, int[] nrHiddenNeurons, int nrOutputNeurons):
	* Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* each layer, and number of output neurons can (has to) be defined.
	* Each neuron will be connected to each neuron of the following layer.
	* There must be at least 1 hidden layer
	/****************************************************************************************/
	MultiLayerPerceptron(int nrInputNeurons, int[] nrHiddenNeurons, int nrOutputNeurons){
		this(nrInputNeurons, nrHiddenNeurons, nrOutputNeurons, "each", "each", "each");
	}

	/****************************************************************************************
	* MultiLayerPerceptron(int nrInputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
	*			int nrOutputNeurons):
	* Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* layer, number of layers and number of output neurons can (has to) be defined.
	* Each neuron will be connected to each neuron of the following layer.
	* There must be at least 1 hidden layer
	/****************************************************************************************/
	MultiLayerPerceptron(int nrInputNeurons, int nrHiddenNeuronsPerLayer, int numberHiddenLayers,
			int nrOutputNeurons){
		this(nrInputNeurons, getHiddenNeurons(nrHiddenNeuronsPerLayer, numberHiddenLayers),
				nrOutputNeurons, "each", "each", "each");
	}
	
private static int[] getHiddenNeurons(int nrHiddenNeuronsPerLayer, int numberHiddenLayers){
		int[] nrHiddenNeurons = new int[numberHiddenLayers];
	
		for (int i = 0; i < nrHiddenNeurons.length; i++)
			nrHiddenNeurons[i] = nrHiddenNeuronsPerLayer;
		
		return nrHiddenNeurons;
	}

public
	/****************************************************************************************
	* float[] run(float[] inputVector):
	* Executes the built multi-layer perceptron with given input vector
	* Returns the output vector as single integer
	* Bit is set if depending output value is > 0.5
	****************************************************************************************/
	float[] run(float[] inputVector){
		
		// Prevent wrong usage
		if (inputVector.length != inputNeurons.length){
			float[] vectorCopy = new float[inputNeurons.length];
			
			for (int i = 0; i < vectorCopy.length; i++){
				if (i >= inputVector.length)
					vectorCopy[i] = 0;
				else
					vectorCopy[i] = inputVector[i];
			}
			
			inputVector = new float[inputNeurons.length];
			inputVector = vectorCopy;
		}// if (inputVector.length != inputNeurons.length)
		
		//Set inputs
		for (int i = 0; i < inputNeurons.length; i++)
			inputNeurons[i].setInput(0, inputVector[i]);
		
		// Execute input layer
		for (int pos = 0; pos < inputConnections.length; pos++)
			inputConnections[pos].run();
			
		// Execute hidden layer(s)
		for(int layer = 0; layer < (hiddenNeurons.size()-1); layer++){
			
			for (int pos = 0; pos < hiddenConnections.get(layer).length; pos++)
				hiddenConnections.get(layer)[pos].run();
		}
		
		// Execute output layer
		for (int pos = 0; pos < outputConnections.length; pos++)
			outputConnections[pos].run();
		
		// Now get the result(s)
		for (int i = 0; i < outputNeurons.length; i++)
			outputVector[i] = outputNeurons[i].getOutput();
			
		return outputVector;
	}// run()
	
	/****************************************************************************************
	* int training(float[] trainingInVector, float[] trainingOutVector, float errorTolerance,
	*				int abort, float trainingCoefficient):
	* Trains the multi layer perceptron
	* Executes the perceptron with given input vector, compares with the given output vector,
	* and calculates (if necessary) new connection weights and neuron thresholds
	* The training will be aborted (to avoid endless execution if now result can be found)
	* after the given number of trials
	* The training coefficient defines the learning step size in backpropagation
	* Returns the number of trials
	****************************************************************************************/
	int training(float[] trainingInVector, float[] trainingOutVector, float errorTolerance,
					int abort, float trainingCoefficient){
		float[] outVector;
		Boolean wrong = false;
		
		// Prevent wrong usage (InVector will be corrected in run())
		if (trainingOutVector.length != outputNeurons.length){
			float[] vectorCopy = new float[trainingOutVector.length];
			
			for (int i = 0; i < vectorCopy.length; i++){
				if (i >= trainingOutVector.length)
					vectorCopy[i] = 0;
				else
					vectorCopy[i] = trainingOutVector[i];
			}
			
			trainingOutVector = new float[trainingOutVector.length];
			trainingOutVector = vectorCopy;
		}// if (trainingOutVector.length != outputNeurons.length)
		
		keepGoing = true;
		
		outVector = new float[outputNeurons.length];
		
		// We don't want endless executions
		int abortTraining = 0;
		
		while(keepGoing){
			// Execute perceptron with training input vector
			outVector = run(trainingInVector);
			
			abortTraining++;
			
			// Wrong result, use backpropagation to find a better one and try again
			for (int i = 0; (i < outVector.length) && (wrong == false); i++){
				if ((outVector[i] < (trainingOutVector[i] - errorTolerance))
						|| (outVector[i] > (trainingOutVector[i] + errorTolerance)))
					wrong = true;					
			}
			
			// At least 1 wrong output
			if (wrong == true){
				backpropagation(outVector, trainingOutVector, trainingCoefficient);
				
				wrong = false;
			}
			
			// All results are like we want them, stop training
			else
				keepGoing = false;
			
			// Cancel training if it takes too long
			// Max number of trials ~2*10^9 (2^31)
			if (abortTraining >= abort)
				keepGoing = false;
		}// while(keepGoing)
		
		// Test output: backpropagationTest
		if (backpropagationTest){
			System.out.print("\tresultOutVector:\n\t\t");
			for (int i = 0; i < outVector.length; i++)
				System.out.print("["+ outVector[i] +"]");
			System.out.print("\n");
		}
		
		return abortTraining;
	}// training()
	
	/****************************************************************************************
	* int training(float[] trainingInVector, float[] trainingOutVector, float errorTolerance,
	*				int abort):
	* Trains the multi layer perceptron
	* Executes the perceptron with given input vector, compares with the given output vector,
	* and calculates (if necessary) new connection weights and neuron thresholds
	* The training will be aborted (to avoid endless execution if now result can be found)
	* after the given number of trials
	* The training coefficient is defined as 0.2.
	* Returns the number of trials
	****************************************************************************************/
	int training(float[] trainingInVector, float[] trainingOutVector, float errorTolerance,
			int abort){
		int trials = training(trainingInVector, trainingOutVector, errorTolerance, abort, 0.2f);
		
		return trials;
	}
	
	/****************************************************************************************
	* void backpropagation(float[] resultOut, float[] wantedOut):
	* Calculates the differences for weights and thresholds and add them.
	****************************************************************************************/
private	void backpropagation(float[] resultOut, float[] wantedOut, float trainingCoefficient){
		float weightDelta = 0;
		Connection[] connections = new Connection[0];
		Connection[] succConnections = new Connection[0];
		int numberOfThresholds;
		float outputOfNeuron;
		float inputOfNeuron;
		int position;
		
		// For calculating the weight differences for every connection loop over every
		// (connection) layer...
		for (int layer = 0; layer <= (hiddenNeurons.size() - 1); layer++){
			
			// Outputconnection layer
			if (layer == (hiddenNeurons.size() - 1)){
				connections = new Connection [outputConnections.length];
				connections = outputConnections;
				
				numberOfThresholds = outputNeurons.length;
			}// if (layer == (hiddenNeurons.size() - 1))
			
			// InputConnection layer
			else if (layer == 0){
				
				if ((hiddenNeurons.size() - 1) > 1)
					succConnections = hiddenConnections.get(0);

				else
					succConnections = outputConnections;

				connections = new Connection [inputConnections.length];
				connections = inputConnections;
				
				numberOfThresholds = inputNeurons.length;
			}// else if (layer == 0)
			
			// Hidden Layer
			else {

				if (layer == hiddenNeurons.size() - 2)
					succConnections = outputConnections;

				else
					succConnections = hiddenConnections.get(layer + 1);
				
				connections = new Connection [hiddenConnections.get(layer).length];
				connections = hiddenConnections.get(layer);
				
				numberOfThresholds = hiddenNeurons.get(layer).length;
			}// else (if (layer == (hiddenNeurons.size() - 1)))
			
			// ...and now over all connections
			for (int conn = 0; conn < connections.length; conn++){
				
				// For output layer: Gradient descent
				if (layer == (hiddenNeurons.size() - 1)){
					position = connections[conn].getPositionNeuronTo();
					
					weightDelta = wantedOut[position] - resultOut[position];
					
					// Everything else we need comes after the for-loops
				}// if (layer == (hiddenNeurons.size() - 1))
				
				// Da real backpropagation for the rest
				else {
					
					position = connections[conn].getPositionNeuronTo();
					
					// Sum over successive neurons
					for (int succ = 0; succ < succConnections[position].getNeuronTo().getNumberOfInputs(); succ++){
						int sumOutputNeurons = 0;
						
						// Sum over output neurons
						for (int out = 0; out < outputNeurons.length; out++){
							sumOutputNeurons += ((wantedOut[out] - resultOut[out]) *
										resultOut[out] * (1- resultOut[out]));
						}
						
						weightDelta += sumOutputNeurons;
						
						weightDelta *= succConnections[position + succ].getConnectionWeight();
					}// Sum over successive neurons
				}// else (if (layer == (hiddenNeurons.size() - 1)))
				
				outputOfNeuron = connections[conn].getNeuronTo().getOutput();
				inputOfNeuron = connections[conn].getNeuronFrom().getOutput();
					
				weightDelta *= (outputOfNeuron * (1 - outputOfNeuron) * inputOfNeuron);
					
				weightDelta *= trainingCoefficient;
					
				// Add calculated weight difference to connection weight
				connections[conn].addWeightDelta(weightDelta);
			}// for() over all connections
			
			// Calculation of thresholds
			for (int thresh = 0; thresh < numberOfThresholds; thresh++){
				int positionInLayer = thresh * (connections.length / numberOfThresholds);
				// TODO: Welche Version stimmt?
				float oldThresh = /*-1 * */connections[positionInLayer].getNeuronTo().getThreshold();
				
				// For output layer: Gradient descent
				if (layer == hiddenNeurons.size() - 1)
					weightDelta = wantedOut[thresh] - resultOut[thresh];
				
				// Backpropagation for the rest
				else {
					// Sum over successive neurons
					for (int succ = 0; succ < succConnections[positionInLayer].getNeuronTo().getNumberOfInputs(); succ++){
						int sumOutputNeurons = 0;
						
						// Sum over output neurons
						for (int out = 0; out < outputNeurons.length; out++)
							sumOutputNeurons += ((wantedOut[out] - resultOut[out]) *
									      resultOut[out] * (1- resultOut[out]));
				
						weightDelta += sumOutputNeurons;
						
						weightDelta *= succConnections[positionInLayer + succ].getConnectionWeight();
					}// Sum over successive neurons
				}// else (if (layer == hiddenNeurons.size() - 1))
				
				outputOfNeuron = connections[positionInLayer].getNeuronTo().getOutput();
					
				weightDelta *= (outputOfNeuron * (1 - outputOfNeuron));
					
				weightDelta *= trainingCoefficient;
					
				// Add calculated weight difference to old threshold and set new threshold
				connections[positionInLayer].getNeuronTo().setThreshold(oldThresh + weightDelta);
			}// for() over thresholds
		}// for() over every (connection) layer
	}// backpropagation()

	/****************************************************************************************
	* String[] findFastest()
	*
	****************************************************************************************/
	String[] findFastest(float[] inputVector, float[] outputVector, float errorTolerance, int abort){
		MultiLayerPerceptron  perceptron;
		int[] nrHiddenNeurons;
		String[] inputTopologies;
		String[] hiddenTopologies;
		String[] outputTopologies;
		String[][] topologies;
		String[] topoSort;
		String[] fastestString;
		int[] trials;
		int trialPos;
		int trialsSort;
		
		nrHiddenNeurons = new int[hiddenNeurons.size()];
		
		for (int i = 0; i < nrHiddenNeurons.length; i++)
			nrHiddenNeurons[i] = hiddenNeurons.get(i).length;
		
		inputTopologies = new String[3];
		hiddenTopologies = new String[4];
		outputTopologies = new String[2];
		
		fastestString = new String[4];
		
		trials = new int[inputTopologies.length * hiddenTopologies.length * outputTopologies.length];
		trialPos = 0;
		topologies = new String[trials.length][3];
		topoSort = new String[3];
		
		inputTopologies[0] = hiddenTopologies[0] = outputTopologies[0] = "one";
		inputTopologies[1] = hiddenTopologies[3] = outputTopologies[1] = "twoGroups";
		inputTopologies[2] = "each";
		hiddenTopologies[1] = "cross";
		hiddenTopologies[2] = "zigzag";
		
		for (int inTop = 0; inTop < inputTopologies.length; inTop++){
			
			for (int hidTop = 0; hidTop < hiddenTopologies.length; hidTop++){
				
				for (int outTop = 0; outTop < outputTopologies.length; outTop++){
					
					perceptron = new MultiLayerPerceptron(inputNeurons.length, nrHiddenNeurons, outputNeurons.length
															, inputTopologies[inTop], hiddenTopologies[hidTop]
															, outputTopologies[outTop]);
					
					topologies[trialPos][0] = inputTopologies[inTop];
					topologies[trialPos][1] = hiddenTopologies[hidTop];
					topologies[trialPos][2] = outputTopologies[outTop];
					
					trials[trialPos] = perceptron.training(inputVector, outputVector, errorTolerance, abort);
					
					trialPos++;
				}// for (hiddenTopologies)
			}// for (hiddenTopologies)
		}// for (inputTopologies)
		
		for (int i = 0; i < trials.length; i++){
			
			for (int j = (trials.length-1); j > 0; j--){
				
				if (trials[j-1] > trials[j]){
					
					trialsSort = trials[j];
					trials[j] = trials[j-1];
					trials[j-1] = trialsSort;
					
					for (int k = 0; k < 3; k++){
						topoSort[k] = topologies[j][k];
						topologies[j][k] = topologies[j-1][k];
						topologies[j-1][k] = topoSort[k];
					}
				}// if (trials[j-1] > trials[j])
			}// for (int j = (trials.length-1); j > 0; j--)
		}// for (int i = 0; i < trials.length; i++)
		
		for (int i = 0; i < 3; i++)
			fastestString[i] = topologies[0][i];
		
		if (trials[0] == abort)
			fastestString[3] = "ABORTED";
		else
			fastestString[3] = Integer.toString(trials[0]);
		
		// DEBUG
//		System.out.print("\t\tFastest:\t");
		
		for (int j = 0; j < trials.length; j++){
			
			if (trials[j] == trials[0]){
				
				System.out.print("\t\t");
				
				for (int i = 0; i < 3; i++)
					System.out.print(topologies[j][i] + ", ");
				
				System.out.println("\n\t\tTrials: " + trials[j]);
				
			}// if (trials[j] == trials[0])
		}// for (int j = 0; j < trials.length; j++)
		
		trialPos = 0;
		
		return fastestString;
	}// findFastest()
}// class MultiLayerPerceptron