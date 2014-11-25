/********************************** Artificial neural network ***********************************
* TODO:	Description, Header, Comments, TechDoc
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
	int[] neededNeuronInputs;
	float[] outputVector;
	Connection[] inputConnections;
	Connection[] hiddenLyrConnections;
	ArrayList<Connection[]> hiddenConnections;
	Connection[] outputConnections;
	// For training loop
	Boolean keepGoing;
	
	// Test variables
public static Boolean multiLayerPerceptronTest = false;
public static Boolean backpropagationTest = false;

public
	// Constructor
	/****************************************************************************************
	* MultiLayerPerceptron(int nrInputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers):
	* Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* layer, number of layers and number of output neurons can (has to) be defined.
	* There must be at least 1 hidden layer
	/****************************************************************************************/
	MultiLayerPerceptron(int nrInputNeurons, int[] nrHiddenNeurons, int nrOutputNeurons){
	
		neededNeuronInputs = new int[nrHiddenNeurons.length + 1];
		
		hiddenNeurons = new ArrayList<Neuron[]>();
		hiddenConnections = new ArrayList<Connection[]>();
		
		/*** Input layer ***/
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
		
		// Every input neuron will be connected to the nearest hidden neuron
		if (nrInputNeurons >= nrHiddenNeurons[0]){
			neededNeuronInputs[0] = nrInputNeurons / nrHiddenNeurons[0];
			
			// Maybe we need 1 more input
			if ((nrInputNeurons % nrHiddenNeurons[0]) != 0)
				neededNeuronInputs[0]++;
			
			inputConnections = new Connection[nrInputNeurons];
		}
			
		// Every hidden neuron will be connected to the nearest input neuron
		else{
			neededNeuronInputs[0] = nrHiddenNeurons[0] / nrInputNeurons;
			
			// Maybe we need 1 more input
			if ((nrHiddenNeurons[0] % nrInputNeurons) != 0)
				neededNeuronInputs[0]++;
			
			inputConnections = new Connection[nrHiddenNeurons[0]];
		}
		
		inputNeurons = new Neuron[nrInputNeurons];
		
		// Each input neuron has just 1 input
		for (int i = 0; i < nrInputNeurons; i++){
			inputNeurons[i] = new Neuron(1);
			
			// Initialise thresholds
			// (to 8 so the output is almost 0)
			inputNeurons[i].setThreshold(8);
		}
		
		/*** Hidden layer ***/		
		if (nrHiddenNeurons.length == 1){
			hiddenLayer  = new Neuron[nrHiddenNeurons[0]];
			
			hiddenNeurons.add(hiddenLayer);
		}
			
		else {
			for (int lyr = 0; lyr < nrHiddenNeurons.length; lyr++){
				hiddenLayer = new Neuron[nrHiddenNeurons[lyr]];

				hiddenNeurons.add(hiddenLayer);
			}
			
			// Every hidden neuron will be connected to the nearest hidden neuron in the next layer			
			for (int lyr = 0; lyr < nrHiddenNeurons.length-1; lyr++){
				
				neededNeuronInputs[lyr + 1] = nrHiddenNeurons[lyr] / nrHiddenNeurons[lyr+1];
				
				// Maybe we need 1 more input
				if ((nrHiddenNeurons[lyr] % nrHiddenNeurons[lyr+1]) != 0)
					neededNeuronInputs[lyr + 1]++;
			}
		} // else (hiddenLayers > 1)
					
		// Set number of inputs; resp. number of connections
		// to the previous layer
		for (int i = 0; i < hiddenNeurons.size(); i++){
			
			for (int n = 0; n < hiddenNeurons.get(i).length; n++){
				hiddenNeurons.get(i)[n] = new Neuron(neededNeuronInputs[i]);
					
				// Initialise thresholds
				hiddenNeurons.get(i)[n].setThreshold(8);
			}
		}
		 
		/*** Output layer ***/		
		outputNeurons = new Neuron[nrOutputNeurons];
		
		outputVector = new float[nrOutputNeurons];
		
		// Every hidden neuron will be connected to the nearest output neuron
		if (nrHiddenNeurons[nrHiddenNeurons.length - 1] >= nrOutputNeurons){
			outputConnections = new Connection[nrHiddenNeurons.length - 1];
			
			neededNeuronInputs[nrHiddenNeurons.length] = nrHiddenNeurons[nrHiddenNeurons.length - 1] / nrOutputNeurons;
			
			// Maybe we need 1 more input
			if ((nrHiddenNeurons[nrHiddenNeurons.length - 1] % nrOutputNeurons) != 0)
				neededNeuronInputs[nrHiddenNeurons.length]++;
		}
		
		// Every output neuron will be connected to the nearest hidden neuron
		else{
			outputConnections = new Connection[nrOutputNeurons];
		
			neededNeuronInputs[nrHiddenNeurons.length] = nrOutputNeurons / nrHiddenNeurons[nrHiddenNeurons.length - 1];
			
			// Maybe we need 1 more input
			if ((nrOutputNeurons % nrHiddenNeurons[nrHiddenNeurons.length - 1]) != 0)
				neededNeuronInputs[nrHiddenNeurons.length]++;
		}
		
		for (int i = 0; i < outputNeurons.length; i++){
			outputNeurons[i] = new Neuron(neededNeuronInputs[nrHiddenNeurons.length]);
			
			// Initialise thresholds
			outputNeurons[i].setThreshold(8);
			
			// Set output to 0
			outputVector[i] = 0;
		}
		
		/*** Connections ***/
		// Connections between input layer and 1st hidden layer
		int inp = 0;
		int neur = 0;
		
		// Test output: multilayerPerceptronTest
		if (multiLayerPerceptronTest)
			System.out.println("\tinputConnection\t| inputNeuron | hiddenNeuron\tinp|pos");
		
		if (inputNeurons.length >= hiddenNeurons.get(0).length){
			
			// From each input neuron...
			for (int inNeur = 0; inNeur < inputNeurons.length; inNeur++){
				
				// ... with step size of the layer difference
				// (We need more connections in the middle)
				if ((inputNeurons.length % hiddenNeurons.get(0).length) != 0){

						// (We just have even numbers of input neurons)
						// (Second extra connection)
						if ((inNeur >= ((inputNeurons.length / 2))
								+ (inputNeurons.length / hiddenNeurons.get(0).length))
								&& ((inputNeurons.length - hiddenNeurons.get(0).length) >= 2)){
							neur = (inNeur - 2) / (inputNeurons.length / hiddenNeurons.get(0).length);
						
							inp = ((inNeur + 1) % ((inputNeurons.length / hiddenNeurons.get(0).length) + 1));
						}
				
						// (First extra connection)
						else if ((inNeur >= (inputNeurons.length / 2) - 1)
								&& ((inputNeurons.length - hiddenNeurons.get(0).length) >= 2)){
							neur = (inNeur - 1) / (inputNeurons.length / hiddenNeurons.get(0).length);
							
							inp = ((inNeur + 1) % ((inputNeurons.length / hiddenNeurons.get(0).length) + 1));
						}
						
						// (Just one extra connection needed)
						else if ((inNeur >= (inputNeurons.length / 2))
								&& ((inputNeurons.length - hiddenNeurons.get(0).length) < 2)){
							neur = (inNeur - 1) / (inputNeurons.length / hiddenNeurons.get(0).length);
							
							inp = ((inNeur + 1) % ((inputNeurons.length / hiddenNeurons.get(0).length) + 1));
						}
						
						else {
							neur = inNeur / (inputNeurons.length / hiddenNeurons.get(0).length);
						
							inp = (inNeur % (inputNeurons.length / hiddenNeurons.get(0).length));
						}
					}
					
					// (Whole-number ratio)
					else {
						neur = inNeur / (inputNeurons.length / hiddenNeurons.get(0).length);
						
						inp = (inNeur % (inputNeurons.length / hiddenNeurons.get(0).length));
					}
					
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+inNeur+"]\t|\t["+inNeur+"]\t [0]["+neur+"]\t\t "+inp+" | "+neur);
					
					// ...to a hidden neuron (initialise the connection weight with 1)
					inputConnections[inNeur] = new Connection(inputNeurons[inNeur],
							hiddenNeurons.get(0)[neur], inp, 1, neur);
				}
			}
			else {
				
				// From each hidden neuron...
				for (int hidNeur = 0; hidNeur < hiddenNeurons.get(0).length; hidNeur++){
					
					// ... with step size of the layer difference
					// (We need more connections in the middle)
					if ((hiddenNeurons.get(0).length % inputNeurons.length) != 0){

						// (We just have even numbers of input neurons)
						// (Second extra connection)
						if (hidNeur >= ((hiddenNeurons.get(0).length / 2)
								+ (hiddenNeurons.get(0).length / inputNeurons.length)))
							neur = (hidNeur - 2) / (hiddenNeurons.get(0).length / inputNeurons.length);
					
						// (First extra connection)
						else if ((hidNeur >= (hiddenNeurons.get(0).length / 2) - 1))
							neur = (hidNeur - 1) / (hiddenNeurons.get(0).length / inputNeurons.length);
						
						// (Just one extra connection needed)
						else if ((hidNeur >= (hiddenNeurons.get(0).length / 2))
								&& ((hiddenNeurons.get(0).length - inputNeurons.length) < 2)){
							neur = (hidNeur - 1) / (hiddenNeurons.get(0).length / inputNeurons.length);
							
							inp = ((hidNeur + 1) % ((hiddenNeurons.get(0).length / inputNeurons.length) + 1));
						}
						
						else
							neur = hidNeur / (hiddenNeurons.get(0).length / inputNeurons.length);
					}
					
					// (Whole-number ratio)
					else
						neur = hidNeur / (hiddenNeurons.get(0).length / inputNeurons.length);
					
					// Test output: multilayerPerceptronTest
					if (multiLayerPerceptronTest)
						System.out.println("\t\t["+hidNeur+"]\t|\t["+neur+"]\t [0]["+hidNeur+"]\t\t 0 | "+hidNeur);
					
					// ...to a hidden neuron (initialise the connection weight with 1)
					inputConnections[hidNeur] = new Connection(inputNeurons[neur],
							hiddenNeurons.get(0)[hidNeur], 0, 1, hidNeur);
				}
			}
		
		// Connections between hidden layers
		if (hiddenNeurons.size() > 1){
			
			for (int lyr = 0; lyr < hiddenNeurons.size() - 1; lyr++){
				
				if (nrHiddenNeurons[lyr] >= nrHiddenNeurons[lyr+1])
					hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr]];
				else
					hiddenLyrConnections = new Connection[nrHiddenNeurons[lyr+1]];

				hiddenConnections.add(hiddenLyrConnections);
			}
				
			// Test output: multilayerPerceptronTest
			if (multiLayerPerceptronTest)
				System.out.println("\thiddenConnection| hiddenNeuron | hiddenNeuron\tinp|pos");
			
			for (int layer = 0; layer < (hiddenNeurons.size() - 1); layer++){
				inp = 0;
				neur = 0;
			
				if (hiddenNeurons.get(layer).length >= hiddenNeurons.get(layer+1).length){
					
					// From each neuron...
					for (int hidNeur = 0; hidNeur < hiddenNeurons.get(layer).length; hidNeur++){
						
						// ... with step size of the layer difference
						// (We need more connections in the middle)
						if ((hiddenNeurons.get(layer).length % hiddenNeurons.get(layer+1).length) != 0){

							// (We just have even numbers of input neurons)
							// (Second extra connection)
							if (hidNeur >= ((hiddenNeurons.get(layer).length / 2)
									+ (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length))){
								neur = (hidNeur - 2) / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
							
								inp = ((hidNeur + 1) % ((hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length) + 1));
							}
							
							// (First extra connection)
							else if ((hidNeur >= (hiddenNeurons.get(layer).length / 2) - 1)){
								neur = (hidNeur - 1) / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
								
								inp = ((hidNeur + 1) % ((hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length) + 1));
							}
							
							// (Just one extra connection needed)
							else if ((hidNeur >= (hiddenNeurons.get(layer).length / 2))
									&& ((hiddenNeurons.get(layer).length - hiddenNeurons.get(layer+1).length) < 2)){
								neur = (hidNeur - 1) / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
								
								inp = ((hidNeur + 1) % ((hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length) + 1));
							}
							
							else {
								neur = hidNeur / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
							
								inp = (hidNeur % (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length));
							}
						}
							
						// (Whole-number ratio)
						else {
							neur = hidNeur / (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length);
								
							inp = (hidNeur % (hiddenNeurons.get(layer).length / hiddenNeurons.get(layer+1).length));
						}
							
						// Test output: multilayerPerceptronTest
						if (multiLayerPerceptronTest)
							System.out.println("\t\t["+hidNeur+"]\t|\t["+(layer)+"]["+hidNeur+"]\t ["+(layer+1)+"]["+neur+"]\t\t "+inp+" | "+neur);
							
						// ...to a hidden neuron (initialise the connection weight with 1)
						hiddenConnections.get(layer)[hidNeur] = new Connection(hiddenNeurons.get(layer)[hidNeur],
								hiddenNeurons.get(layer+1)[neur], inp, 1, neur);
					}
				}
					
				else {
					// From each neuron...
					for (int hidNeur = 0; hidNeur < hiddenNeurons.get(layer+1).length; hidNeur++){
							
						// ... with step size of the layer difference
						// (We need more connections in the middle)
						if ((hiddenNeurons.get(layer+1).length % hiddenNeurons.get(layer).length) != 0){

							// (We just have even numbers of input neurons)
							// (Second extra connection)
							if (hidNeur >= ((hiddenNeurons.get(layer+1).length / 2)
									+ (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length)))
								neur = (hidNeur - 2) / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
							
							// (First extra connection)
							else if ((hidNeur >= (hiddenNeurons.get(layer+1).length / 2) - 1))
								neur = (hidNeur - 1) / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
								
							// (Just one extra connection needed)
							else if ((hidNeur >= (hiddenNeurons.get(layer+1).length / 2))
									&& ((hiddenNeurons.get(layer+1).length - hiddenNeurons.get(layer).length) < 2))
								neur = (hidNeur - 1) / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
								
							else
								neur = hidNeur / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
						}
							
						// (Whole-number ratio)
						else
							neur = hidNeur / (hiddenNeurons.get(layer+1).length / hiddenNeurons.get(layer).length);
						
						// Test output: multilayerPerceptronTest
						if (multiLayerPerceptronTest)
							System.out.println("\t\t["+hidNeur+"]\t|\t["+(layer)+"]["+neur+"]\t ["+(layer+1)+"]["+hidNeur+"]\t\t 0 | "+hidNeur);
						
						// ...to a hidden neuron (initialise the connection weight with 1)
						hiddenConnections.get(layer)[hidNeur] = new Connection(hiddenNeurons.get(layer)[neur],
								hiddenNeurons.get(layer+1)[hidNeur], 0, 1, hidNeur);
					}
				}
			}
		}// if (hiddenLayers > 1)
		
		// Connections between last hidden and output layer
		
		// Test output: multilayerPerceptronTest
		if (multiLayerPerceptronTest)
			System.out.println("\toutputConnection| hiddenNeuron | outputNeuron\tinp|pos");
						
		inp = 0;
		neur = 0;
			
		if (hiddenNeurons.get(hiddenNeurons.size() - 1).length >= nrOutputNeurons){
			
			// From each hidden neuron...
			for (int hidNeur = 0; hidNeur < hiddenNeurons.get(hiddenNeurons.size() - 1).length; hidNeur++){
				
				// ... with step size of the layer difference
				// (We need more connections in the middle)
				if ((hiddenNeurons.get(hiddenNeurons.size() - 1).length % outputNeurons.length) != 0){

					// (We just have even numbers of input neurons)
					// (Second extra connection)
					if (hidNeur >= ((hiddenNeurons.get(hiddenNeurons.size() - 1).length / 2)
							+ (hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length))){
						neur = (hidNeur - 2) / (hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length);
					
						inp = ((hidNeur + 1) % ((hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length) + 1));
					}
					
					// (First extra connection)
					else if ((hidNeur >= (hiddenNeurons.get(hiddenNeurons.size() - 1).length / 2) - 1)){
						neur = (hidNeur - 1) / (hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length);
						
						inp = ((hidNeur + 1) % ((hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length) + 1));
					}
					
					// (Just one extra connection needed)
					else if ((hidNeur >= (hiddenNeurons.get(hiddenNeurons.size() - 1).length / 2))
							&& ((hiddenNeurons.get(hiddenNeurons.size() - 1).length - outputNeurons.length) < 2)){
						neur = (hidNeur - 1) / (hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length);
						
						inp = ((hidNeur + 1) % ((hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length) + 1));
					}
					
					else {
						neur = hidNeur / (hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length);
					
						inp = (hidNeur % (hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length));
					}
				}
					
				// (Whole-number ratio)
				else {
					neur = hidNeur / (hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length);
						
					inp = (hidNeur % (hiddenNeurons.get(hiddenNeurons.size() - 1).length / outputNeurons.length));
				}
				
				// Test output: multilayerPerceptronTest
				if (multiLayerPerceptronTest)
					System.out.println("\t\t["+hidNeur+"]\t|\t["+(hiddenNeurons.size()-1)+"]["+hidNeur+"]\t ["+neur+"]\t\t "+inp+" | "+neur);	
				
				// ...to the "nearest" output neuron (if several are available)
				outputConnections[hidNeur] = new Connection(hiddenNeurons.get(hiddenNeurons.size() - 1)[hidNeur],
						outputNeurons[neur], inp, 1, neur);
			}
		}
		
		else {
			
			// From each output neuron...
			for (int oNeur = 0; oNeur < outputNeurons.length; oNeur++){
				
				// ... with step size of the layer difference
				// (We need more connections in the middle)
				if ((outputNeurons.length % hiddenNeurons.get(hiddenNeurons.size() - 1).length) != 0){

					// (We just have even numbers of input neurons)
					// (Second extra connection)
					if (oNeur >= ((outputNeurons.length / 2)
							+ (outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length)))
						neur = (oNeur - 2) / (outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length);
					
					// (First extra connection)
					else if ((oNeur >= (outputNeurons.length / 2) - 1))
						neur = (oNeur - 1) / (outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length);
						
					// (Just one extra connection needed)
					else if ((oNeur >= (outputNeurons.length / 2))
							&& ((outputNeurons.length - hiddenNeurons.get(hiddenNeurons.size() - 1).length) < 2))
						neur = (oNeur - 1) / (outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length);
						
					else
						neur = oNeur / (outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length);
				}
						
				// (Whole-number ratio)
				else
					neur = oNeur / (outputNeurons.length / hiddenNeurons.get(hiddenNeurons.size() - 1).length);
				
				// Test output: multilayerPerceptronTest
				if (multiLayerPerceptronTest)
					System.out.println("\t\t["+oNeur+"]\t|\t["+(hiddenNeurons.size()-1)+"]["+neur+"]\t ["+oNeur+"]\t\t 0 | "+oNeur);	
				
				// ...to the "nearest" output neuron (if several are available)
				outputConnections[oNeur] = new Connection(hiddenNeurons.get(hiddenNeurons.size() - 1)[neur],
						outputNeurons[oNeur], 0, 1, oNeur);
			}
		}
		
		keepGoing  = false;
	}// MultiLayerPerceptron(int nrInputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers)

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
		}
		
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
		}
		
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
		}
		
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
	* void backpropagation(float[] resultOut, float[] wantedOut):
	* Calculates the differences for weights and thresholds and add them.
	****************************************************************************************/
private	void backpropagation(float[] resultOut, float[] wantedOut, float trainingCoefficient){
		float weightDelta = 0;
//		float succWeights[] = new float[1];
		Connection connections[] = new Connection[1];
		int succNeurons = 0;
		int numberOfThresholds;
		float outputOfNeuron;
		float inputOfNeuron;
		int position;
		
		// For calculating the weight differences for every connection loop over every
		// (connection) layer...
		int connectionLayer = hiddenNeurons.size() - 1;
		
		for (int layer = connectionLayer; layer >= 0; layer--){
			
			// Output connection layer
			if (layer == connectionLayer){
				connections = new Connection [outputConnections.length];
				connections = outputConnections;
				
				numberOfThresholds = outputNeurons.length;
			}
			
			// Input connection layer
			else if (layer == 0){
				
				// More than one hidden layer
				if (connectionLayer > 1){
					
					// More input neurons than hidden neurons in 1st layer
					if (inputNeurons.length >= hiddenNeurons.get(0).length)
						// Just one connection to the next layer
						succNeurons = 1;
					else
						// TODO: Beenden
						// In the middle there are extra connections
						// 	0 -> 0-0
						// 0	1 -> 0-1
						// 1	2 -> 1-2
						// 	3 -> 1-3
						succNeurons = 1;
				}
				
				else {
				
					// More input neurons than output neurons
					if (inputNeurons.length >= outputNeurons.length)
						// Just one connection to the next layer
						succNeurons = 1;
					else
						// TODO: Beenden
						// In the middle there are extra connections
						succNeurons = outputConnections.length/outputNeurons.length;
				}

				connections = new Connection [inputConnections.length];
				connections = inputConnections;
				
				numberOfThresholds = inputNeurons.length;
			}
			
			// Hidden Layer
			else {
				
				if (layer == connectionLayer - 1){
//					succWeights = new float [outputConnWeights.length];
//					succWeights = outputConnWeights;
					
					// Just one connection per neuron to output layer
					succNeurons = 1;
				}

				else {
//					succWeights = new float [hiddenConnWeights.get(layer + 1).length];
//					succWeights = hiddenConnWeights.get(layer + 1);
				
					// To each neuron of the following layer
					succNeurons = hiddenNeurons.get(layer + 1).length;
				}
				
				connections = new Connection [hiddenConnections.get(layer).length];
				connections = hiddenConnections.get(layer);
				
				numberOfThresholds = hiddenNeurons.get(layer).length;
			}
			
			// ...and now over all connections
			for (int conn = 0; conn < (connections.length); conn++){
				
				// For output layer: Gradient descent
				if (layer == connectionLayer){
					position = connections[conn].getPositionNeuronTo();
					
					weightDelta = wantedOut[position] - resultOut[position];
					
					// Everything else we need comes after the for-loops
				}
				
				// Da real backpropagation for the rest
				else {
					// Sum over successive neurons
					for (int succ = 0; succ < succNeurons; succ++){
						int sumOutputNeurons = 0;
						
						// Sum over output neurons
						for (int out = 0; out < outputNeurons.length; out++){
							sumOutputNeurons += ((wantedOut[out] - resultOut[out]) *
										resultOut[out] * (1- resultOut[out]));
						}
						
						weightDelta += sumOutputNeurons;
						
						position = connections[conn].getPositionNeuronTo();
						
						weightDelta *= succWeights[position];
					}// Sum over successive neurons
				}
				
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
				float oldThresh = -1 * connections[positionInLayer].getNeuronTo().getThreshold();
				
				// For output layer: Gradient descent
				if (layer == connectionLayer)
					weightDelta = wantedOut[thresh] - resultOut[thresh];
				
				// Backpropagation for the rest
				else {
					// Sum over successive neurons
					for (int succ = 0; succ < succNeurons; succ++){
						int sumOutputNeurons = 0;
						
						// Sum over output neurons
						for (int out = 0; out < outputNeurons.length; out++)
							sumOutputNeurons += ((wantedOut[out] - resultOut[out]) *
									      resultOut[out] * (1- resultOut[out]));
				
						weightDelta += sumOutputNeurons;
						
						weightDelta *= succWeights[succ];
					}// Sum over successive neurons
				}
				
				outputOfNeuron = connections[positionInLayer].getNeuronTo().getOutput();
					
				weightDelta *= (outputOfNeuron * (1 - outputOfNeuron));
					
				weightDelta *= trainingCoefficient;
					
				// Add calculated weight difference to old threshold and set new threshold
				connections[positionInLayer].getNeuronTo().setThreshold(oldThresh + weightDelta);
			}// for() over thresholds
		}// for() over every (connection) layer
	}// backpropagation()

	/****************************************************************************************
	* float[] getInputConnectionWeights():
	* Needed for the visualisation
	****************************************************************************************/
	float[] getInputConnectionWeights(){
		float[] inputConnWeights = new float[inputConnections.length];
		
		for (int conn = 0; conn < inputConnections.length; conn++)
			inputConnWeights[conn] = inputConnections[conn].getConnectionWeight();
		
		return inputConnWeights;
	}

}// class MultiLayerPerceptron