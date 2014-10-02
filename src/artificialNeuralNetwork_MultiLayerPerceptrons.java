/********************************** Artificial neural network ***********************************
* TODO:	Description, Header, Comments
* 		class Neuron64 mit long für inputs
* 		class NeuronFloat & class Neuron64 implements class Neuron?
* 		connectionFloat
* 		Activation function und Output function von außen setzbar machen: (In extra classes, damit class Neuron möglichst klein bleibt)
*		(siehe ComputaionalIntelligence S.52)
*		Sprungfunktion:		if (netInput >= threshold) return netInput; else return 0;
*		semi-lineare Funktion:	if (netInput > threshold + 1/2) return netInput;
*					else if ((netInput < threshold + 1/2) && (netInput > threshold - 1/2)) output = (netInput - threshold) + 1/2;
*					else return 0;
*		Sinus bis Sättigung:	if (netInput > threshold + pi/2) return netInput;
*					else if ((netInput < threshold + pi/2) && (netInput > threshold - pi/2)) output = (sin(netInput - threshold) + 1)/2;
*					else return 0;
*		logistische Funktion:	output = 1 / (1 + e^(-(netInput - threshold))) // geht nur von 0 - 1
*		radiale Basis Funktionen
*		enum für Funktionen in Klasse über Neuron
*
* Beispiel für Aufbau:
* (aus NeuronaleKodierung.pdf)
* Neuron integriert Eingangsignale zu Ausgang
* Ausgang verzweigt sich am Ende weiter und ist mit anderen Neuronen verbunden
* Spikes können, wie Buchstaben, als Grundelement der Sprache gesehen werden
* Spike-Kombinationen bilden neuronalen Code
* Nicht nur Anzahl der Spikes, sondern auch das zeitliche Muster könnte Informationen in sich bergen
* Ein Neuron der Gehirnrinde empfängt die Aktivitäten einer ganzen Population vorgeschalteter Neuronen
* Sie berechen durch Mittelwertbildung über die von den einzelnen Neuronen bevorzugten Augenpositionen, jeweils gewichtet mit deren Aktivität, einen so genannten Populationsvektor
* ->
* Attribute Farbe:	enthält 3 Neuronen Rot, Gelb, Blau
*			-> 3 Inputs
*			-> Output Farbvektor
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
	Neuron inputNeuron[];
	Neuron hiddenNeuron[][];
	Neuron outputNeuron[];
	int hiddenLayers;
	//int connectionsPerNeuron;
	float inputConnWeights[];
	float hiddenConnWeights[][];
	float outputConnWeights[];
	float outputVector[];
	Connection inputConnection[];
	Connection hiddenConnection[][];
	Connection outputConnection[];
	Boolean keepGoing;	// For training loop

public
	// Constructors
	/****************************************************************************************
	* Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* layer, number of layers and number of output neurons can (has to) be defined.
	* Each neuron will be connected to each neuron of the following layer (Except output layer).
	* TODO: Next Constructos: With connectionsPerNeuron the number of connections to the neurons of the following
	* layer are defined (-> number of outputs of neuron (excl output neurons))
	* In case of more than 1 output neuron the outputs of the last layer are splitted to the
	* output neurons; the lower hidden neurons to the lower output neurons etc
	* It's not possible to have more output neurons than hidden neurons
	* There must be at least 1 hidden layer
	****************************************************************************************/
	MultiLayerPerceptron(int inputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
							int outputNeurons){
	
		int neededNeuronInputs;
		
		/*** Input layer ***/
		// Just to prevent wrong usage
		if (inputNeurons < 1)
			inputNeurons = 1;
		
		inputNeuron = new Neuron[inputNeurons];
		
		// Each input neuron has just 1 input
		for (int i = 0; i < inputNeurons; i++){
			inputNeuron[i] = new Neuron(1);
			
			// Initial values: all weights and thresholds = 1
			inputNeuron[i].setWeight(0, 1);
			inputNeuron[i].setThreshold(1);
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
		
		hiddenNeuron = new Neuron[hiddenLayers][hiddenNeuronsPerLayer];
			
		// Set number of inputs; resp. number of connections
		// to the previous layer
		for (int i = 0; i < hiddenLayers; i++){
			
			for (int j = 0; j < hiddenNeuronsPerLayer; j++){
				
				// Input layer to 1st hidden
				if (i == 0)
					neededNeuronInputs = inputNeurons;
				// All others
				else
					neededNeuronInputs = hiddenNeuronsPerLayer;
					
				hiddenNeuron[i][j] = new Neuron(neededNeuronInputs);
					
				// Initial values: all weights and thresholds = 1
				for (int k = 0; k < neededNeuronInputs; k++)
					hiddenNeuron[i][j].setWeight(k, 1);
					
				hiddenNeuron[i][j].setThreshold(1);
			}
		}
		
		/*** Output layer ***/
		// Just to prevent wrong usage
		if (outputNeurons < 1)
			outputNeurons = 1;
			
		else if (outputNeurons > hiddenNeuronsPerLayer)
			outputNeurons = hiddenNeuronsPerLayer;
			
		outputNeuron = new Neuron[outputNeurons];
		
		outputVector = new float[outputNeurons];
		
		for (int i = 0; i < outputNeuron.length; i++){
			
			// Set number of inputs; resp. number of connections
			// to the previous layer
			neededNeuronInputs = hiddenNeuronsPerLayer / outputNeurons;
			
			// Maybe we need 1 more input
			if ((hiddenNeuronsPerLayer % outputNeurons) != 0)
				neededNeuronInputs++;
			
			outputNeuron[i] = new Neuron(neededNeuronInputs);
			
			// Initial values: all weights and thresholds = 1
			for (int j = 0; j < neededNeuronInputs; j++)
				outputNeuron[i].setWeight(j, 1);
			
			outputNeuron[i].setThreshold(1);
			
			// Set output to 0
			outputVector[i] = 0;
		}
		
		/*** Connection weights ***/
		// Initialize weights, all weights = 1
		inputConnWeights = new float[inputNeurons * hiddenNeuronsPerLayer];
		
		for (int i = 0; i < (inputNeurons * hiddenNeuronsPerLayer); i++)
			inputConnWeights[i] = 1;
		
		hiddenConnWeights = new float[hiddenLayers][hiddenNeuronsPerLayer * hiddenNeuronsPerLayer];

		for (int i = 0; i < hiddenLayers; i++){
			
			for (int j = 0; j < hiddenConnWeights[i].length; j++)
				hiddenConnWeights[i][j] = 1;
		}
		
		outputConnWeights = new float[hiddenNeuronsPerLayer];
		
		for (int i = 0; i < (hiddenNeuronsPerLayer); i++)
			outputConnWeights[i] = 1;
		
		/*** Connections ***/
		// We need as many connections as connection weights (obviously)
		inputConnection = new Connection[inputConnWeights.length];
		hiddenConnection = new Connection[hiddenLayers][hiddenConnWeights[0].length];
		outputConnection = new Connection[outputConnWeights.length];
		
		// Connections between input layer and 1st hidden layer
		int positionInLayer = 0;
		
		// From each input neuron...
		for (int inp = 0; inp < inputNeuron.length; inp++){
		
			// ...to each neuron of the 1st hidden layer
			for (int conn = 0; conn < hiddenNeuron[0].length; conn++){
				
				inputConnection[positionInLayer] = new Connection(inputNeuron[inp],
						hiddenNeuron[0][conn], inp, inputConnWeights[positionInLayer]);
				
				positionInLayer++;
			}
		}
		
		// Connections between hidden layers
		for (int layer = 0; layer < (hiddenLayers-1); layer++){
			positionInLayer = 0;
			
			// From each hidden neuron...
			for (int neur = 0; neur < hiddenNeuron[layer].length; neur++){
			
				// ...to each neuron of the next hidden layer
				for (int conn = 0; conn < hiddenNeuron[layer+1].length; conn++){
					
					hiddenConnection[layer][positionInLayer] = new Connection(hiddenNeuron[layer][neur],
							hiddenNeuron[layer+1][conn], neur, hiddenConnWeights[layer][positionInLayer]);
					
					positionInLayer++;
				}
			}
		}
		
		// Connections between last hidden and output layer
		int inp = 0;
		int outNeur = -1;
		
		// From each hidden neuron...
		for (int hidNeur = 0; hidNeur < hiddenNeuron[hiddenLayers-1].length; hidNeur++, inp++){
			
			// Go to next output neuron if all inputs are connected
			if ((hidNeur % (hiddenNeuron[hiddenLayers-1].length/outputNeuron.length)) == 0){
				outNeur++;
				inp = 0;
			}
			
			// ...to the "nearest" output neuron (if several are available)
			outputConnection[hidNeur] = new Connection(hiddenNeuron[hiddenLayers-1][hidNeur],
					outputNeuron[outNeur], inp, outputConnWeights[hidNeur]);
		}
		
		keepGoing  = false;
	}// MultiLayerPerceptron()

	/****************************************************************************************
	* Multi-layer perceptron where number of input neurons, number of hidden neurons per
	* layer, number of layers and number of output neurons can be defined.
	* Also it is possible to define the type of connection between input layer and 1st hidden
	* layer and between the hidden layers.
	/****************************************************************************************/
	MultiLayerPerceptron(int inputNeurons, int hiddenNeuronsPerLayer, int hiddenLayers,
				int outputNeurons, String inputConnections, String hiddenConnections){
		// Connections for input layer:	each: NrConnections = hidden, twoGroups: NrConnections = hidden/2, one: NrConnections = inputNeur\\
		// Connections forr hidden Layer: each, cross and zigzag\\
	}// MultiLayerPerceptron()
	
	/****************************************************************************************
	* Multi-layer perceptron with output topologies each und groups
	****************************************************************************************/
	/****************************************************************************************
	* Multi-layer perceptron with different numbers of hidden neurons per layer
	* int[] hiddenNeuronsPerLayer, - int hiddenLayers,
	* hiddenLayers = hiddenNeuronsPerLayer.length;
	****************************************************************************************/
	/****************************************************************************************
	* Multi-layer perceptron with hidden neurons rising from inputs to variable / layer
	****************************************************************************************/
	
	/****************************************************************************************
	* Execute the built multi-layer perceptron with given input vector
	* Returns the output vector
	****************************************************************************************/
	float[] run(int inputVector){
		// DEBUG
		System.out.println("inputNeurons: " + inputNeuron.length +
				", hiddenNeuronsPerLayer: " + hiddenNeuron[0].length +
				", hiddenLayers: " + hiddenLayers +
				", outputNeurons: " + outputNeuron.length);
		
		//Set input if corresponding bit is high, unset if low
		for (int i = 0; i < inputNeuron.length; i++){
			
			if ((inputVector & (1 << i)) == (1 << i))
				inputNeuron[i].setInput(0);
			else
				inputNeuron[i].unsetInput(0);
		}
		
		// Execute input layer
		for (int pos = 0; pos < inputConnection.length; pos++){
			
			// DEBUG
			System.out.println("inputConnection[" + pos + "]");
			
			inputConnection[pos].run();
		}
		
		// Execute hidden layer(s)
		for(int layer = 0; layer < (hiddenLayers-1); layer++){
			
			//DEBUG
			System.out.println("connection["+layer+"].length: "+ hiddenConnection[layer].length);
			
			for (int pos = 0; pos < hiddenConnection[layer].length; pos++){
				
				// DEBUG
				System.out.println("hiddenConnection[" + layer + "][" + pos + "]");
				
				hiddenConnection[layer][pos].run();
			}
		}
		
		for (int pos = 0; pos < outputConnection.length; pos++){
			
			// DEBUG
			System.out.println("outputConnection[" + pos + "]");
			
			outputConnection[pos].run();
		}
		
		// Now get the result(s)
		for (int i = 0; i < outputNeuron.length; i++)
			outputVector[i] = outputNeuron[i].getOutput();
				
		return outputVector;
	}// run()
	
	/****************************************************************************************
	* Trains the multi layer perceptron
	* Executes the perceptron with given input vector, compares with the given output vector,
	* and calculates (if necessary) new connection weights and neuron thresholds
	* Returns true if the output vector is correct, false is given output vector is too long
	* or too short
	****************************************************************************************/
	Boolean training(int trainigInVector, float[] trainingOutVector){
		float outVector[] = new float[outputVector.length];
		
		if (outVector.length != trainingOutVector.length)
			return false;
		
		int debugStopTraining = 0;
		
		while(keepGoing){
			outVector = run(trainigInVector);
			
			for (int i = 0; i < outVector.length; i++){
			
				// For debug purposes, print output vector
				System.out.println("Trial " + debugStopTraining + ":");
				
				for (int deb = 0; deb < outVector.length; deb++){
					System.out.print("trainingOutVector[" + deb + "]: " + trainingOutVector[deb]);
					System.out.println(", outVector[" + deb + "]: " + outVector[deb]);
				}
			
				// Wrong result, use backpropagation to find a better one
				if (outVector[i] != trainingOutVector[i]){
					//TODO: Backpropagation
				}
				
				// All results are like we want them, stop training
				else if (i == (outVector.length - 1))
					keepGoing = false;
			}
			
			// For debug purposes cancel training after 100 trials
			debugStopTraining++;
			
			if (debugStopTraining >= 100)
				return false;
		}
		
		return true;
	}// training()
	
	//Boolean training(int[] trainingVector, float errorTolerance) // in percentage terms
}// class MultiLayerPerceptron

/************************************************************************************************
* TODO: class MultiLayerPerceptronFloat:
* copy of class MultiLayerPerceptron but with NeuronFloat
************************************************************************************************/