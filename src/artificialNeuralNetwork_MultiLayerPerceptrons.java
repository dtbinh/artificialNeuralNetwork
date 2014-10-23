/********************************** Artificial neural network ***********************************
* TODO:	Description, Header, Comments, TechDoc
************************************************************************************************/

/************************************************************************************************
* class MultiLayerPerceptron:
* automatically builds a multi-layer perceptron, using neurons with logistic function, see
* description of constructors for more details
* Max. number of neurons per layer: 32
************************************************************************************************/
class MultiLayerPerceptron
{
private
	Neuron inputNeurons[];
	Neuron hiddenNeurons[][];
	Neuron outputNeurons[];
	int hiddenLayers;
	float inputConnWeights[];
	float hiddenConnWeights[][];
	float outputConnWeights[];
	float outputVector[];
	Connection inputConnections[];
	Connection hiddenConnections[][];
	Connection outputConnections[];
	Boolean keepGoing;	// For training loop

public
	// Constructors
	/****************************************************************************************
	* Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* layer, number of layers and number of output neurons can (has to) be defined.
	* Each neuron will be connected to each neuron of the following layer (Except output layer).
	* In case of more than 1 output neuron the outputs of the last layer are split to the
	* output neurons; the lower hidden neurons to the lower output neurons etc
	* It's not possible to have more output neurons than hidden neurons
	* There must be at least 1 hidden layer
	****************************************************************************************/
	MultiLayerPerceptron(int nrInputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
							int nrOutputNeurons){
	
		int neededNeuronInputs;
		
		/*** Input layer ***/
		// Just to prevent wrong usage
		if (nrInputNeurons < 1)
			nrInputNeurons = 1;
		
		inputNeurons = new Neuron[nrInputNeurons];
		
		// Each input neuron has just 1 input
		for (int i = 0; i < nrInputNeurons; i++){
			inputNeurons[i] = new Neuron(1);
			
			// Initial values: all thresholds = 0
			inputNeurons[i].setThreshold(0);
		}
		
		/*** Hidden layer ***/
		// Just to prevent wrong usage
		if (hiddenNeuronsPerLayer < 1)
			hiddenNeuronsPerLayer = 1;
		
		// Makes no sense when we have hidden neurons
		if (hiddenLayers < 1)
			hiddenLayers = 1;
		
		// We need this in run() too for building the connections
		this.hiddenLayers = hiddenLayers;
		
		hiddenNeurons = new Neuron[hiddenLayers][hiddenNeuronsPerLayer];
			
		// Set number of inputs; resp. number of connections
		// to the previous layer
		for (int i = 0; i < hiddenLayers; i++){
			
			for (int j = 0; j < hiddenNeuronsPerLayer; j++){
				
				// Input layer to 1st hidden
				if (i == 0)
					neededNeuronInputs = nrInputNeurons;
				// All others
				else
					neededNeuronInputs = hiddenNeuronsPerLayer;
					
				hiddenNeurons[i][j] = new Neuron(neededNeuronInputs);
					
				// Initial values: all thresholds = 0
					
				hiddenNeurons[i][j].setThreshold(0);
			}
		}
		
		/*** Output layer ***/
		// Just to prevent wrong usage
		if (nrOutputNeurons < 1)
			nrOutputNeurons = 1;
			
		else if (nrOutputNeurons > hiddenNeuronsPerLayer)
			nrOutputNeurons = hiddenNeuronsPerLayer;
			
		outputNeurons = new Neuron[nrOutputNeurons];
		
		outputVector = new float[nrOutputNeurons];
		
		for (int i = 0; i < outputNeurons.length; i++){
			
			// Set number of inputs; resp. number of connections
			// to the previous layer
			neededNeuronInputs = hiddenNeuronsPerLayer / nrOutputNeurons;
			
			// Maybe we need 1 more input
			if ((hiddenNeuronsPerLayer % nrOutputNeurons) != 0)
				neededNeuronInputs++;
			
			outputNeurons[i] = new Neuron(neededNeuronInputs);
			
			// Initial values: thresholds = 0			
			outputNeurons[i].setThreshold(0);
			
			// Set output to 0
			outputVector[i] = 0;
		}
		
		/*** Connection weights ***/
		// Initialize weights, all weights = 1
		inputConnWeights = new float[nrInputNeurons * hiddenNeuronsPerLayer];
		
		for (int i = 0; i < (nrInputNeurons * hiddenNeuronsPerLayer); i++)
			inputConnWeights[i] = 1;
		
		if (hiddenLayers > 1){
			hiddenConnWeights = new float[hiddenLayers-1][hiddenNeuronsPerLayer * hiddenNeuronsPerLayer];
	
			for (int i = 0; i < hiddenLayers-1; i++){
				
				for (int j = 0; j < hiddenConnWeights[i].length; j++)
					hiddenConnWeights[i][j] = 1;
			}
		}
		
		outputConnWeights = new float[hiddenNeuronsPerLayer];
		
		for (int i = 0; i < (hiddenNeuronsPerLayer); i++)
			outputConnWeights[i] = 1;
		
		/*** Connections ***/
		// We need as many connections as connection weights (obviously)
		inputConnections = new Connection[inputConnWeights.length];
		
		if (hiddenLayers > 1)
			hiddenConnections = new Connection[hiddenLayers-1][hiddenConnWeights[0].length];
		
		outputConnections = new Connection[outputConnWeights.length];
		
		// Connections between input layer and 1st hidden layer
		int positionInLayer = 0;
		
		// From each input neuron...
		for (int inp = 0; inp < inputNeurons.length; inp++){
		
			// ...to each neuron of the 1st hidden layer
			for (int conn = 0; conn < hiddenNeurons[0].length; conn++){
				
				inputConnections[positionInLayer] = new Connection(inputNeurons[inp],
						hiddenNeurons[0][conn], inp, inputConnWeights[positionInLayer]);
				
				positionInLayer++;
			}
		}
		
		// Connections between hidden layers
		for (int layer = 0; layer < (hiddenLayers-1); layer++){
			positionInLayer = 0;
			
			// From each hidden neuron...
			for (int neur = 0; neur < hiddenNeurons[layer].length; neur++){
			
				// ...to each neuron of the next hidden layer
				for (int conn = 0; conn < hiddenNeurons[layer+1].length; conn++){
					
					hiddenConnections[layer][positionInLayer] = new Connection(hiddenNeurons[layer][neur],
							hiddenNeurons[layer+1][conn], neur, hiddenConnWeights[layer][positionInLayer]);
					
					positionInLayer++;
				}
			}
		}
		
		// Connections between last hidden and output layer
		int inp = 0;
		int outNeur = 0;
		int numberOfInputs = (hiddenNeurons[hiddenLayers-1].length/outputNeurons.length);
		
		if ((hiddenNeurons[hiddenLayers-1].length % outputNeurons.length) != 0)
			numberOfInputs++;
		
		// From each hidden neuron...
		for (int hidNeur = 0; hidNeur < hiddenNeurons[hiddenLayers-1].length; hidNeur++, inp++){
			
			// Go to next output neuron if all inputs are connected
			if ((hidNeur > 0) && (hidNeur % numberOfInputs  == 0)){
				outNeur++;
				inp = 0;
			}
			
			// ...to the "nearest" output neuron (if several are available)
			outputConnections[hidNeur] = new Connection(hiddenNeurons[hiddenLayers-1][hidNeur],
					outputNeurons[outNeur], inp, outputConnWeights[hidNeur]);
		}
		
		keepGoing  = false;
	}// MultiLayerPerceptron()
	
	/****************************************************************************************
	* Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* layer, number of layers and number of output neurons can (has to) be defined.
	* Also the type of connection between input layer and 1st hidden layer has to be defined.
	* Each hidden neuron will be connected to each hidden neuron of the following layer (Except input and
	* output layer).
	* In case of more than 1 output neuron the outputs of the last layer are split to the
	* output neurons; the lower hidden neurons to the lower output neurons etc
	* It's not possible to have more output neurons than hidden neurons
	* There must be at least 1 hidden layer
	/****************************************************************************************/
	MultiLayerPerceptron(int nrInputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
				int nrOutputNeurons, String inputTopology){
		// TODO:	Test mit verschiedenen Topologien (auch Training)
		// Connections for input layer:	each: NrConnections = hidden, twoGroups: NrConnections = hidden/2, one: NrConnections = inputNeur
		
		int neededNeuronInputs;
		
		/*** Input layer ***/
		// Just to prevent wrong usage
		if (nrInputNeurons < 1)
			nrInputNeurons = 1;
		
		// Abhängikeiten von inputTopology:	Anzahl der Inputs der hidden[0] Neuronen
		//									Arraygröße inputConnWeights & -> inputConnections
		//									Algorythmus für Connections between input layer and 1st hidden layer
		switch (inputTopology){
		case "one":
			// Every input neuron will be connected to the nearest hidden neuron
			if (nrInputNeurons >= hiddenNeuronsPerLayer){
				inputConnWeights = new float[nrInputNeurons];
				
				neededNeuronInputs = inputConnWeights.length / hiddenNeuronsPerLayer;
				
				// Maybe we need 1 more input
				if ((inputConnWeights.length % hiddenNeuronsPerLayer) != 0)
					neededNeuronInputs++;
			}
				
			// Every hidden neuron will be connected to the nearest input neuron
			else{
				inputConnWeights = new float[hiddenNeuronsPerLayer];
				
				neededNeuronInputs = inputConnWeights.length / nrInputNeurons;
				
				// Maybe we need 1 more input
				if ((inputConnWeights.length % nrInputNeurons) != 0)
					neededNeuronInputs++;
			}
			break;
		
		default: // each
			inputConnWeights = new float[nrInputNeurons * hiddenNeuronsPerLayer];
			neededNeuronInputs = nrInputNeurons;
		}// switch (inputTopology)
		
		inputNeurons = new Neuron[nrInputNeurons];
		
		// Each input neuron has just 1 input
		for (int i = 0; i < nrInputNeurons; i++){
			inputNeurons[i] = new Neuron(1);
			
			// Initial values: all thresholds = 0
			inputNeurons[i].setThreshold(0);
		}
		
		// Initialize weights, all weights = 1
		for (int i = 0; i < inputConnWeights.length; i++)
			inputConnWeights[i] = 1;
		
		// We need as many connections as connection weights (obviously)
		inputConnections = new Connection[inputConnWeights.length];
		
		/*** Hidden layer ***/
		// Just to prevent wrong usage
		if (hiddenNeuronsPerLayer < 1)
			hiddenNeuronsPerLayer = 1;
		
		// Makes no sense when we have hidden neurons
		if (hiddenLayers < 1)
			hiddenLayers = 1;
		
		// We need this in run() too for building the connections
		this.hiddenLayers = hiddenLayers;
		
		hiddenNeurons = new Neuron[hiddenLayers][hiddenNeuronsPerLayer];
			
		// Set number of inputs; resp. number of connections
		// to the previous layer
		for (int i = 0; i < hiddenLayers; i++){
			
			for (int j = 0; j < hiddenNeuronsPerLayer; j++){
				
				// Input layer to 1st hidden in switch
				if (i < 0)
					neededNeuronInputs = hiddenNeuronsPerLayer;
					
				hiddenNeurons[i][j] = new Neuron(neededNeuronInputs);
					
				// Initial values: all thresholds = 0
					
				hiddenNeurons[i][j].setThreshold(0);
			}
		}
		
		/*** Output layer ***/
		// Just to prevent wrong usage
		if (nrOutputNeurons < 1)
			nrOutputNeurons = 1;
			
		else if (nrOutputNeurons > hiddenNeuronsPerLayer)
			nrOutputNeurons = hiddenNeuronsPerLayer;
			
		outputNeurons = new Neuron[nrOutputNeurons];
		
		outputVector = new float[nrOutputNeurons];
		
		for (int i = 0; i < outputNeurons.length; i++){
			
			// Set number of inputs; resp. number of connections
			// to the previous layer
			neededNeuronInputs = hiddenNeuronsPerLayer / nrOutputNeurons;
			
			// Maybe we need 1 more input
			if ((hiddenNeuronsPerLayer % nrOutputNeurons) != 0)
				neededNeuronInputs++;
			
			outputNeurons[i] = new Neuron(neededNeuronInputs);
			
			// Initial values: thresholds = 0
			outputNeurons[i].setThreshold(0);
			
			// Set output to 0
			outputVector[i] = 0;
		}
		
		/*** Connection weights ***/
		
		if (hiddenLayers > 1){
			hiddenConnWeights = new float[hiddenLayers-1][hiddenNeuronsPerLayer * hiddenNeuronsPerLayer];
	
			for (int i = 0; i < hiddenLayers-1; i++){
				
				for (int j = 0; j < hiddenConnWeights[i].length; j++)
					hiddenConnWeights[i][j] = 1;
			}
		}
		
		outputConnWeights = new float[hiddenNeuronsPerLayer];
		
		for (int i = 0; i < (hiddenNeuronsPerLayer); i++)
			outputConnWeights[i] = 1;
		
		/*** Connections ***/
		// We need as many connections as connection weights (obviously)
		
		// Connections between input layer and 1st hidden layer
		int positionInLayer = 0;
		int inp = 0;
		int outNeur = 0;
		int numberOfInputs;
		
		switch (inputTopology){
		case "one": // Still TODO

			if (nrInputNeurons >= hiddenNeuronsPerLayer){
				numberOfInputs = (inputNeurons.length/hiddenNeurons[0].length);
				
				if ((inputNeurons.length % hiddenNeurons[0].length) != 0)
					numberOfInputs++;
				
				// From each input neuron...
				for (int inNeur = 0; inNeur < inputNeurons.length; inNeur++, inp++){
					
					// Go to next hidden neuron if all inputs are connected
					if ((inNeur > 0) && (inNeur % numberOfInputs == 0)){
						outNeur++;
						inp = 0;
					}
					
					System.out.println("inputConnections["+inNeur+"] = new Connection(inputNeurons["+inNeur
							+"] ,hiddenNeurons[0]["+outNeur+"], "+inp+", inputConnWeights["+inNeur+"]);");
					
					// ...to the "nearest" hidden neuron (if several are available)
					inputConnections[inNeur] = new Connection(inputNeurons[inNeur],
							hiddenNeurons[0][outNeur], inp, inputConnWeights[inNeur]);
				}
			}
			else{
				numberOfInputs = (hiddenNeurons[0].length/inputNeurons.length);
				
				if ((hiddenNeurons[0].length % inputNeurons.length) != 0)
					numberOfInputs++;
				
				// From each hidden neuron...
				for (int hidNeur = 0; hidNeur < hiddenNeurons[0].length; hidNeur++, inp++){
					
					// Go to next hidden neuron if all inputs are connected
					if ((hidNeur > 0) && (hidNeur % numberOfInputs == 0)){
						outNeur++;
						inp = 0;
					}
					
					System.out.println("inputConnections["+hidNeur+"] = new Connection(inputNeurons["+outNeur
							+"] ,hiddenNeurons[0]["+hidNeur+"], "+inp+", inputConnWeights["+hidNeur+"]);");
					
					// ...to the "nearest" input neuron (if several are available)
					inputConnections[hidNeur] = new Connection(inputNeurons[outNeur],
							hiddenNeurons[0][hidNeur], inp, inputConnWeights[hidNeur]);
				}
			}
			break;
		
		default: // From each input neuron...
			for (inp = 0; inp < inputNeurons.length; inp++){
			
				// ...to each neuron of the 1st hidden layer
				for (int conn = 0; conn < hiddenNeurons[0].length; conn++){
					
					inputConnections[positionInLayer] = new Connection(inputNeurons[inp],
							hiddenNeurons[0][conn], inp, inputConnWeights[positionInLayer]);
					
					positionInLayer++;
				}
			}
		}// switch (inputTopology)
		
		if (hiddenLayers > 1)
			hiddenConnections = new Connection[hiddenLayers-1][hiddenConnWeights[0].length];
		
		outputConnections = new Connection[outputConnWeights.length];
		
		// Connections between hidden layers
		for (int layer = 0; layer < (hiddenLayers-1); layer++){
			positionInLayer = 0;
			
			// From each hidden neuron...
			for (int neur = 0; neur < hiddenNeurons[layer].length; neur++){
			
				// ...to each neuron of the next hidden layer
				for (int conn = 0; conn < hiddenNeurons[layer+1].length; conn++){
					
					hiddenConnections[layer][positionInLayer] = new Connection(hiddenNeurons[layer][neur],
							hiddenNeurons[layer+1][conn], neur, hiddenConnWeights[layer][positionInLayer]);
					
					positionInLayer++;
				}
			}
		}
		
		// Connections between last hidden and output layer
		inp = 0;
		outNeur = -1;
		
		// From each hidden neuron...
		for (int hidNeur = 0; hidNeur < hiddenNeurons[hiddenLayers-1].length; hidNeur++, inp++){
			
			// Go to next output neuron if all inputs are connected
			if ((hidNeur % (hiddenNeurons[hiddenLayers-1].length/outputNeurons.length)) == 0){
				outNeur++;
				inp = 0;
			}
			
			// ...to the "nearest" output neuron (if several are available)
			outputConnections[hidNeur] = new Connection(hiddenNeurons[hiddenLayers-1][hidNeur],
					outputNeurons[outNeur], inp, outputConnWeights[hidNeur]);
		}
		
		keepGoing  = false;
	}// MultiLayerPerceptron()

	/****************************************************************************************
	* Executes the built multi-layer perceptron with given input vector
	* Returns the output vector as single integer
	* Bit is set if depending output value is > 0.5
	****************************************************************************************/
	float[] run(float[] inputVector){
		if (inputVector.length == inputNeurons.length){
		
			// DEBUG
//			System.out.println("inputNeurons: " + inputNeurons.length +
//					", hiddenNeuronsPerLayer: " + hiddenNeurons[0].length +
//					", hiddenLayers: " + hiddenLayers +
//					", outputNeurons: " + outputNeurons.length);
			
			//Set inputs
			for (int i = 0; i < inputNeurons.length; i++)
				inputNeurons[i].setInput(0, inputVector[i]);
			
			// Execute input layer
			for (int pos = 0; pos < inputConnections.length; pos++){
				
				// DEBUG
//				System.out.println("inputConnection[" + pos + "]");
				
				inputConnections[pos].run();
			}
			
			// Execute hidden layer(s)
			for(int layer = 0; layer < (hiddenLayers-1); layer++){
				
				//DEBUG
//				System.out.println("connection["+layer+"].length: "+ hiddenConnections[layer].length);
				
				for (int pos = 0; pos < hiddenConnections[layer].length; pos++){
					
					// DEBUG
//					System.out.println("hiddenConnection[" + layer + "][" + pos + "]");
					
					hiddenConnections[layer][pos].run();
				}
			}
			
			for (int pos = 0; pos < outputConnections.length; pos++){
				
				// DEBUG
//				System.out.println("outputConnection[" + pos + "]");
				
				outputConnections[pos].run();
			}
			
			// Now get the result(s)
			for (int i = 0; i < outputNeurons.length; i++)
				outputVector[i] = outputNeurons[i].getOutput();
	
			return outputVector;
		}
		
		else {
			float[] error;
			error = new float[1];
			
			error[0] = -1;
			
			return error;
		}
	}// run()
	
	/****************************************************************************************
	* Executes the built multi-layer perceptron with given input vector
	* Returns the output vector as single integer
	****************************************************************************************/
/*	int runInt(int inputVector){
		int output = 0;
		float networkInputVector[] = new float[inputNeuron.length];
		float networkOutputVector[] = new float[outputVector.length];
		
		//Set input if corresponding bit is high, unset if low
		for (int i = 0; i < inputNeuron.length; i++){
			
			if ((inputVector & (1 << i)) == (1 << i))
				networkInputVector[i] = 1;
			else
				networkInputVector[i] = 0;
		}
		
		networkOutputVector = run(networkInputVector);
		
		for (int i = 0; i < networkOutputVector.length || i < 32; i++){
			if(networkOutputVector[i] >= 0.5f)
				output |= (1 << i);
		}
		
		return output;
	}// run()
*/	
	/****************************************************************************************
	* Executes the built multi-layer perceptron with given input vector
	* Returns the output vector as single integer
	* Bit is set if depending output value is > threshold
	****************************************************************************************/
	/*int runInt(int inputVector, float threshold){
		int output = 0;
		float networkOutputVector[] = new float[outputVector.length];
		
		networkOutputVector = run(inputVector);
		
		for (int i = 0; i < networkOutputVector.length || i < 32; i++){
			if(networkOutputVector[i] >= threshold)
				output |= (1 << i);
		}
		
		return output;
	}// run()*/
	
	/****************************************************************************************
	* Trains the multi layer perceptron
	* Executes the perceptron with given input vector, compares with the given output vector,
	* and calculates (if necessary) new connection weights and neuron thresholds
	* Returns true if the output vector is within error tolerance,
	* returns false if given input or output vector is too long or too short
	****************************************************************************************/
	/* DEBUG Boolean*/ int training(float[] trainingInVector, float[] trainingOutVector, float errorTolerance){	 // in percentage terms
		float[] outVector;
		float[] absErrorTol;
		Boolean wrong = false;
		
		if (trainingInVector.length == inputNeurons.length)
			keepGoing = true;
		else
//			return false;
			return -1;
		
		if (trainingOutVector.length != outputNeurons.length){
			//DEBUG
			System.out.println("trainingOutVector.length ("+trainingOutVector.length+
					") != outputNeuron.length("+outputNeurons.length+")");
			keepGoing = false;
			
//			return false;
			return -1;
		}
		
		outVector = new float[outputNeurons.length];
		absErrorTol = new float[trainingOutVector.length];
		
		// Calculate absolute acceptable error
		for (int i = 0; i < trainingOutVector.length; i++){
			absErrorTol[i] = trainingOutVector[i] / 100 * errorTolerance;
			
			// DEBUG
			System.out.println("absErrorTol["+i+"] = " + absErrorTol[i]);
		}
		
		int debugStopTraining = 0;
		
		while(keepGoing){
			// Execute perceptron with training input vector
			outVector = run(trainingInVector);
			
			// For debug purposes cancel training after 100 trials
			debugStopTraining++;
			
			// For debug purposes, print output vector
			System.out.println("Trial " + debugStopTraining + ":");
			
			for (int deb = 0; deb < outVector.length; deb++){
				System.out.print("trainingOutVector[" + deb + "]: " + (trainingOutVector[deb] - absErrorTol[deb]));
				System.out.print(" - " + (trainingOutVector[deb] + absErrorTol[deb]));
				System.out.println(", outVector[" + deb + "]: " + outVector[deb]);
			}
			
			// Wrong result, use backpropagation to find a better one and try again
			// TODO: In gitk schauen, wie for-loop zuerst war
			for (int i = 0; (i < outVector.length) && (wrong == false); i++){
				if ((outVector[i] < (trainingOutVector[i] - absErrorTol[i]))
						|| (outVector[i] > (trainingOutVector[i] + absErrorTol[i])))
					wrong = true;					
			}
			
			// At least 1 wrong output
			if (wrong == true){
				backpropagation(outVector, trainingOutVector);
				
				wrong = false;
			}
			
			// All results are like we want them, stop training
			else
				keepGoing = false;
			
			// For debug purposes cancel training after 10000 trials
			// Max number of trials ~2*10^9 (2^31)
			if (debugStopTraining >= 10000)
//				return false;
				return debugStopTraining;
		}
		
//		return true;
		return debugStopTraining;
	}// training()
	
	/****************************************************************************************
	* TODO: Aufräumen
	*	
	* ---------------------------------------------------------------------------------------
	* Beispiel:	2 Input, 4 Hidden, 2 Hiddenlayer, 2 Output
	*		Connection each, Oupuzt connection 2 Groups
	*              ----------------------------------------------------------------
	*		inputConnection[] | hiddenConnection[][] | outputConnection[]
	*	0 :	0 >			0 >			0
	*					1
	*					2
	*					3
	*		1 >			0
	*					1
	*					2
	*					3
	*		2 >			0
	*					1 >			0
	*					2
	*					3
	*		3 >			0
	*					1
	*					2
	*					3
	*	1 :	0 >			0
	*					1
	*					2 >			1
	*					3
	*		1 >			0
	*					1
	*					2
	*					3
	*		2 >			0
	*					1
	*					2
	*					3 >			1
	*		3 >			0
	*					1
	*					2
	*					3
	*              ----------------------------------------------------------------
	* ---------------------------------------------------------------------------------------
	*
	* Backpropagation allgemein:
	* deltaGewichtsvektor_u = Lernfaktor (Summe über Folgeneuronen((Summe über Ausgabeneuronen(
	* (Erwartete Ausgabe - Ausgabe) dAusgabe_Ausgabeneuron nach dNetzeingabe_Folgeneuron))*Gewicht_Neuron-FolgeNeuron)
	* ) dAusgabe_u nach dNetzeingabe_u * Eingabevektor_u (S.71)
	*
	* Für Identiät als Ausgabefunktion und logistischer Aktivierungsfunktion:
	* deltaGewichtsvektor_u = Lernfaktor (Summe über Folgeneuronen((Summe über Ausgabeneuronen(
	* (Erwartete Ausgabe_Ausgabeneuron - Ausgabe_Ausgabeneuron)dAusgabe_Ausgabeneuron nach dNetzeingabe_Folgeneuron))*Gewicht_FolgeNeuron-u)
	* ) Ausgabe_u (1 - Ausgabe_u) * Eingabevektor_u
	
	* -> SCHICHTENWEISE
	-> Summe über Ausgabeneuronen:
	* -> Summe über Folgeneuronen: Pfad im Netz muss mathematisch nachgebildet werden
	* -> Eingabevektor muss angepasst werden, enthält Bitmuster
	* -> bei logistischer Funktion:
	*	dAusgabe_Ausgabeneuron nach dNetzeingabe_Folgeneuron = Ausgabe_Ausgabeneuron (1 - Ausgabe_Ausgabeneuron)
	*
	* Einzelner Gradientenabstieg:
	* delta Gewichtsvektor_u = Lernfaktor (Erwartete Ausgabe - Ausgabe) Ausgabe (1 - Ausgabe) * Eingabevektor_u
	* -> pro inputneuron:
	* delta Gewicht_u = Lernfaktor (Erwartete Ausgabe - Ausgabe) Ausgabe (1 - Ausgabe) * Eingabe_u
	*
	* Um deltaGewicht für jede verbindung zu berechnen erst mit for-loop über outputconnection.length
	* dann for (int i = hiddenLayer-2; i>=0; i++) -> loop über hiddenLayer[i].length
	* dann for-loop über inputConnection.length
	*
	* Um Folgeneuronen zu ermitteln Position in Array benötigt, dann loops über die folgende
	* Netzstruktur (nicht einfach über die ConnectionArrays)
	*
	* Umwandlung des Schwellenwertes in ein Gewicht: Der Schwellenwert wird auf 0 festgelegt, als Ausgleich wird ein zusätzlicher (imaginärer)
	* Eingang (x_0) eingeführt, der den festen Wert 1 hat und mit dem negierten Schwellenwert gewichtet wird.(S.32)
	****************************************************************************************/
private	void backpropagation(float[] resultOut, float[] wantedOut){
		// TODO: wofür Variablen, Richtige Reihenfolge
		float trainingCoefficient = 0.2f;
		float weightDelta = 0;
		float succWeights[] = new float[1];
		Connection connections[] = new Connection[1];
		int succNeurons = 0;
		int numberOfThresholds;
		float outputOfNeuron;
		float inputVectorOfNeuron;
		
		// Um deltaGewicht für jede verbindung zu berechnen erst loop über alle (Verbindungs-)Layer...
		int connectionLayer = hiddenLayers - 1;
		
		for (int layer = connectionLayer; layer >= 0; layer--){
			
			// Outputconnection layer
			if (layer == connectionLayer){
				connections = new Connection [outputConnections.length];
				connections = outputConnections;
				
				numberOfThresholds = outputNeurons.length;
			}
			
			// InputConnection layer
			else if (layer == 0){
				if (connectionLayer > 1){
					succWeights = new float [hiddenConnWeights[0].length];
					succWeights = hiddenConnWeights[0];
					
					// To each neuron of the following layer
					succNeurons = hiddenNeurons[0].length;
				}
				else {
					succWeights = new float [outputConnWeights.length];
					succWeights = outputConnWeights;
					
					// To each neuron of the following layer
					succNeurons = outputNeurons.length;
				}

				connections = new Connection [inputConnections.length];
				connections = inputConnections;
				
				numberOfThresholds = inputNeurons.length;
			}
			
			// Hidden Layer
			else {
				if (layer == connectionLayer - 1){
					succWeights = new float [outputConnWeights.length];
					succWeights = outputConnWeights;
					
					// Nur eine Verbingung pro Neuron zum outputLayer
					succNeurons = 1;
				}

				else {
					succWeights = new float [hiddenConnWeights[layer + 1].length];
					succWeights = hiddenConnWeights[layer + 1];
				
					// To each neuron of the following layer
					succNeurons = hiddenNeurons[layer + 1].length;
				}
				
				connections = new Connection [hiddenConnections[layer].length];
				connections = hiddenConnections[layer];
				
				numberOfThresholds = hiddenNeurons[layer].length;
			}
			
			// ...und nu über alle Verbindungen
			for (int conn = 0; conn < (connections.length); conn++){
				
				// Gradientenabstieg für outputLayer
				if (layer == connectionLayer){
					// delta Gewichtsvektor_u = Lernfaktor (Erwartete Ausgabe - Ausgabe) Ausgabe (1 - Ausgabe) * Eingabevektor_u
					// -> delta Gewichtsvektor_u = Lernfaktor Ausgabe (1 - Ausgabe) * Eingabevektor_u auch in backpropagation (hinter for-loops)
					// -> nur (Erwartete Ausgabe - Ausgabe) benötigt
					// Erwartete Ausgabe - Ausgabe
					// TODO: Allgemeine Formulierung, wenn andere Verbindungen möglich
					// out: Output des Neurons, conn: aktuelle Verbindung
					/***************************************************************************************
					// From each hidden neuron...
					for (int hidNeur = 0; hidNeur < hiddenNeurons[hiddenLayers-1].length; hidNeur++, inp++){
						
						// Go to next output neuron if all inputs are connected
						if ((hidNeur % (hiddenNeurons[hiddenLayers-1].length/outputNeurons.length)) == 0){
							outNeur++;
							inp = 0;
						}
						
						// ...to the "nearest" output neuron (if several are available)
						outputConnections[hidNeur] = new Connection(hiddenNeurons[hiddenLayers-1][hidNeur],
								outputNeurons[outNeur], inp, outputConnWeights[hidNeur]);
					}
					***************************************************************************************/
					/*** Zur Verfügung
					connections = new Connection [outputConnections.length];
					connections = outputConnections;
				
					numberOfThresholds = outputNeurons.length;
					 ***/
					int out = 0;
					// Anzahl der Neuronen im vorherigen Layer / Anzahl der Output Neuronen
					// -> Anzahl der Verbindungen zu einem Outputneuron -> Distanz zwischen einzelnen Output-
					// neuronen im connectionsArray
					// -> Anzahl der Neuronen im vorherigen Layer: connections.length / numberOfThresholds
					// -> Anzahl der Output Neuronen: numberOfThresholds
					if (conn > (connections.length/(numberOfThresholds * numberOfThresholds)))
						out++;
					
					weightDelta = wantedOut[out] - resultOut[out];
				}
				
				// Da real backpropagation
				else {
					// Beginn der Berechnung
					// deltaGewichtsvektor_u = Lernfaktor * -> hinter die Summen verschoben
					// weightDelta = trainingCoefficient * 
				  
					// Summe über Folgeneuronen
					for (int succ = 0; succ < succNeurons; succ++){
						int sumOutputNeurons = 0;
						
						// Summe über Ausgabeneuronen
						for (int out = 0; out < outputNeurons.length; out++){
							sumOutputNeurons += (
							// Erwartete Ausgabe_Ausgabeneuron - Ausgabe_Ausgabeneuron
							(wantedOut[out] - resultOut[out]) *
							//dAusgabe nach dNetzeingabe
							resultOut[out] * (1- resultOut[out]));
						}// for() Summe über Ausgabeneuronen
				
						weightDelta += sumOutputNeurons;
						
						// * Gewicht_Neuron-FolgeNeuron
						weightDelta *= succWeights[succ];
					}// for() Summe über Folgeneuronen
				}
				
				// * Ausgabe_u (1 - Ausgabe_u) * Eingabevektor_u
				// Ausgabe_u	= getOutput von aktuellem Neuron -> connection[neuron + conn].getNeuronTo.getOutput
				// Eingabevektor_u	= inputs[] von aktuellem Neuron -> getInputVector() in Neuron implementieren
				outputOfNeuron = connections[conn].getNeuronTo().getOutput();
				inputVectorOfNeuron = connections[conn].getNeuronFrom().getOutput();
					
				weightDelta *= (outputOfNeuron * (1 - outputOfNeuron) * inputVectorOfNeuron);
					
				// deltaGewichtsvektor_u = Lernfaktor * -> hinter die Summen verschoben
				weightDelta *= trainingCoefficient;
					
				// Add calculated weight difference to connection weight
				connections[conn].addWeightDelta(weightDelta);
			}// for() über alle Verbindungen
			
			// for über die Thresholds
			for (int thresh = 0; thresh < numberOfThresholds; thresh++){
				// TODO: Der Schwellenwert wird auf 0 festgelegt??? noch notwendig?
				// , als Ausgleich wird ein zusätzlicher (imaginärer)
				// Eingang (x_0) eingeführt, der den festen Wert 1 hat und mit dem negierten Schwellenwert gewichtet wird.(S.32)
				// Position in Layer = threshold * Anzahl der Verbindungen pro Neuron -> Anzahl der Verbinungen in Layer / Anzahl der Neuronen (Thresholds)
				int positionInLayer = thresh * (connections.length / numberOfThresholds);
				float oldThresh = -1 * connections[positionInLayer].getNeuronTo().getThreshold();
				
				// Gradientenabstieg für outputLayer
				if (layer == connectionLayer){
					// delta Gewichtsvektor_u = Lernfaktor (Erwartete Ausgabe - Ausgabe) Ausgabe (1 - Ausgabe) * Eingabevektor_u
					// -> delta Gewichtsvektor_u = Lernfaktor Ausgabe (1 - Ausgabe) * Eingabevektor_u auch in backpropagation (hinter for-loops)
					// -> nur (Erwartete Ausgabe - Ausgabe) benötigt
					// Erwartete Ausgabe - Ausgabe
					// Note: Anzahl thresholds entspricht Anzahl, und somit Position, von OutputNeuron
					weightDelta = wantedOut[thresh] - resultOut[thresh];
				}
				
				// Da real backpropagation
				else {
					// Beginn der Berechnung
					// deltaGewichtsvektor_u = Lernfaktor * -> hinter die Summen verschoben
					// weightDelta = trainingCoefficient * 
				  
					// Summe über Folgeneuronen
					for (int succ = 0; succ < succNeurons; succ++){
						int sumOutputNeurons = 0;
						
						// Summe über Ausgabeneuronen
						for (int out = 0; out < outputNeurons.length; out++){
							sumOutputNeurons += (
							// Erwartete Ausgabe_Ausgabeneuron - Ausgabe_Ausgabeneuron
							(wantedOut[out] - resultOut[out]) *
							//dAusgabe nach dNetzeingabe
							resultOut[out] * (1- resultOut[out]));
						}// for() Summe über Ausgabeneuronen
				
						weightDelta += sumOutputNeurons;
						
						// * Gewicht_Neuron-FolgeNeuron
						weightDelta *= succWeights[succ];
					}// for() Summe über Folgeneuronen
				}
				
				// * Ausgabe_u (1 - Ausgabe_u) * Eingabevektor_u
				// Ausgabe_u	= getOutput von aktuellem Neuron -> connection[neuron + conn].getNeuronTo.getOutput
				// Eingabevektor_u	= inputs[] von aktuellem Neuron -> getInputVector() in Neuron implementieren				
				outputOfNeuron = connections[positionInLayer].getNeuronTo().getOutput();
				// inputVectorOfNeuron nicht benötigt, da nur = 1;
					
				weightDelta *= (outputOfNeuron * (1 - outputOfNeuron));
					
				// deltaGewichtsvektor_u = Lernfaktor * -> hinter die Summen verschoben
				weightDelta *= trainingCoefficient;
					
				// Add calculated weight difference to old threshold and set new threshold
				connections[positionInLayer].getNeuronTo().setThreshold(oldThresh + weightDelta);
			}// for() über thresholds
		}// for() über alle (Verbindungs-)Layer
	}// backpropagation()
}// class MultiLayerPerceptron