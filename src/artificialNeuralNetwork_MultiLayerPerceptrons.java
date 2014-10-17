/********************************** Artificial neural network ***********************************
* TODO:	Description, Header, Comments, TechDoc
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
	* TODO: Next Constructors: With connectionsPerNeuron the number of connections to the neurons of the following
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
			
			// Initial values: all thresholds = 1
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
					
				// Initial values: all thresholds = 1
					
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
			
			// Initial values: thresholds = 1			
			outputNeuron[i].setThreshold(1);
			
			// Set output to 0
			outputVector[i] = 0;
		}
		
		/*** Connection weights ***/
		// Initialize weights, all weights = 1
		inputConnWeights = new float[inputNeurons * hiddenNeuronsPerLayer];
		
		for (int i = 0; i < (inputNeurons * hiddenNeuronsPerLayer); i++)
			inputConnWeights[i] = 1;
		
		hiddenConnWeights = new float[hiddenLayers-1][hiddenNeuronsPerLayer * hiddenNeuronsPerLayer];

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
		hiddenConnection = new Connection[hiddenLayers-1][hiddenConnWeights[0].length];
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
		// Connections for hidden Layer: each, cross and zigzag\\
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
	* Executes the built multi-layer perceptron with given input vector
	* Returns the output vector as single integer
	* Bit is set if depending output value is > 0.5
	****************************************************************************************/
	float[] run(float[] inputVector){
		if (inputVector.length == inputNeuron.length){
		
			// DEBUG
			System.out.println("inputNeurons: " + inputNeuron.length +
					", hiddenNeuronsPerLayer: " + hiddenNeuron[0].length +
					", hiddenLayers: " + hiddenLayers +
					", outputNeurons: " + outputNeuron.length);
			
			//Set inputs
			for (int i = 0; i < inputNeuron.length; i++)
				inputNeuron[i].setInput(0, inputVector[i]);
			
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
	int runInt(int inputVector){
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
	
	/****************************************************************************************
	* Executes the built multi-layer perceptron with given input vector
	* Returns the output vector as single integer
	* TODO: Bit is set if depending output value is > threshold
	****************************************************************************************/
	/*int run(int inputVector, float threshold){
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
	* Returns true if the output vector is correct, false is given output vector is too long
	* or too short
	****************************************************************************************/
	Boolean training(float[] trainigInVector, float[] trainingOutVector){
		float[] outVector;
		outVector = new float[outputNeuron.length];
		
		int debugStopTraining = 0;
		
		while(keepGoing){
			// Execute perceptron with training input vector
			outVector = run(trainigInVector);
			
			// For debug purposes cancel training after 100 trials
			debugStopTraining++;
			
			for (int i = 0; i < outVector.length; i++){
				// For debug purposes, print output vector
				System.out.println("Trial " + debugStopTraining + ":");
				
				for (int deb = 0; deb < outVector.length; deb++){
					System.out.print("trainingOutVector[" + deb + "]: " + trainingOutVector[deb]);
					System.out.println(", outVector[" + deb + "]: " + outVector[deb]);
				}
				
				// Wrong result, use backpropagation to find a better one and try again
				if (outVector != trainingOutVector){
					backpropagation(trainigInVector, outVector, trainingOutVector);
					break;
				}
			
				// All results are like we want them, stop training
				else
					keepGoing = false;
			}
			
			// For debug purposes cancel training after 100 trials
			if (debugStopTraining >= 100)
				return false;
		}
		
		return true;
	}// training()
	
// TODO	Boolean training(float[] trainingVector, float errorTolerance) // in percentage terms

// TODO	Boolean training(int trainigInVector, int trainingOutVector)
	
	/****************************************************************************************
	* TODO: Klären ob inkl. threshold
	*	Kommentare in TechDoc
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
	* delta Gewichtsvektor = Lernfaktor (Erwartete Ausgabe - Ausgabe) Ausgabe (1 - Ausgabe) * Eingabevektor
	* -> pro inputneuron:
	* delta Gewicht = Lernfaktor (Erwartete Ausgabe - Ausgabe) Ausgabe (1 - Ausgabe) * Eingabe
	*
	* Um deltaGewicht für jede verbindung zu berechnen erst mit for-loop über outputconnection.length
	* dann for (int i = hiddenLayer-2; i>=0; i++) -> loop über hiddenLayer[i].length
	* dann for-loop über inputConnection.length
	*
	* Um Folgeneuronen zu ermitteln Position in Array benötigt, dann loops über die folgende
	* Netzstruktur (nicht einfach über die ConnectionArrays)
	****************************************************************************************/
private	void backpropagation(float[] inputVector, float[] resultOut, float[] wantedOut){
		// TODO: Kommentare wofür Variablen, Richtige Reihenfolge
		int connectionsOfNeuron;
		int neuronsInLayer;
		int neuronsInNextLayer;
		int offset;
		float trainingCoefficient = 0.2f;
		float weightDelta = 0;
		float connWeights[];
		Connection connections[];
		
		// Um deltaGewicht für jede verbindung zu berechnen loop über alle (Verbindungs-)Layer...
		for (int layer = hiddenLayers; layer >= 0; layer--){
			// Bestimmen der Position des Neurons im Netz und der folgenden
			// Struktur für die Summe über die Folgeneuronen,
			// d.h. für die Grenze der for-Schleife. Daher nur int neuronsInLayer benötigt
			// -> mit layer ermitteln in welchem Layer: output, hidden o input
			// -> mit neuron an welcher Stelle im Layer
			// Grenze für Summe über Folgeneuronen
			
			// Outputconnection layer
			if (layer == hiddenLayers){
				neuronsInLayer = hiddenNeuron[layer-1].length;
				neuronsInNextLayer = outputNeuron.length;
				
				// Es gibt nur Verbindung(en) vom letztem Hidden Layer zu OutputNeuron(en)
				
				// TODO: Abbruchbedingung, ist noch falsch, benötigt allgemein gültigen Ausdruck
				// Go to next output neuron if all inputs are connected
				// Funktioniert nur wenn neuron > 0
				//while ((neuron % (hiddenNeuron[hiddenLayers-1].length/outputNeuron.length)) != 0){}
				
				// Im Moment nur eine Verbindung von hidden Neuron zu outputLayer
				connectionsOfNeuron = 1;
				
			/***************************************************************
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
			***************************************************************/
			
				// TODO: Auch hier allgemeine Formulierungen
				connWeights = new float [outputConnWeights.length];
				connWeights = outputConnWeights;
				
				connections = new Connection [outputConnection.length];
				connections = outputConnection;
			}
			
			// InputConnection layer
			else if (layer == 0){
				neuronsInLayer = inputNeuron.length;
				neuronsInNextLayer = hiddenNeuron[0].length;
				
				// TODO: Allgemein gültiger Ausdruck
				// Im Moment nur von jeden inputNeuron zu jedem hiddenNeuron
				connectionsOfNeuron = hiddenNeuron[0].length;
				
			/***************************************************************
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
			***************************************************************/
			
				connWeights = new float [inputConnWeights.length];
				connWeights = inputConnWeights;

				connections = new Connection [inputConnection.length];
				connections = inputConnection;
						
			/***************************************************************
			// Connection weights
			inputConnWeights = new float[inputNeurons * hiddenNeuronsPerLayer];
			
			hiddenConnWeights = new float[hiddenLayers-1][hiddenNeuronsPerLayer * hiddenNeuronsPerLayer];

			outputConnWeights = new float[hiddenNeuronsPerLayer];
			
			Struktur siehe Connections weiter oben
			***************************************************************/
			}
			
			// Hidden Layer
			else {
				neuronsInLayer = hiddenNeuron[layer-1].length;
				neuronsInNextLayer = hiddenNeuron[layer].length;
				
				// TODO: Allgemein gültiger Ausdruck
				// Im Moment nur von jeden hiddenNeuron zu jedem hiddenNeuron im Folgelayer
				connectionsOfNeuron = hiddenNeuron[layer].length;
				
				connWeights = new float [hiddenConnWeights[layer-1].length];
				connWeights = hiddenConnWeights[layer-1];

				connections = new Connection [hiddenConnection[layer-1].length];
				connections = hiddenConnection[layer-1];
			}
			
			// ..., über alle Neuronen in dem Layer...
			for (int neuron = 0; neuron < neuronsInLayer; neuron++){
						
				// Position of 1st connection of this neuron (Is the same
				// for all neurons in this layer)
				offset = neuron * connectionsOfNeuron;
			
				// ...und nu über alle Verbindungen des Neurons
				for (int conn = 0; conn < connectionsOfNeuron; conn++){
					/*** Notwendig?
					// pro Inputneuron:
					// Notwendig? wie sonst inputVector[inp] verwenden?
					for (int inp = 0; inp < inputNeuron.length; inp++){
					}// for() pro Inputneuron
					***/
					
					// Beginn der Berechnung
					// deltaGewichtsvektor_u = Lernfaktor * -> hinter die Summen verschoben
					// weightDelta = trainingCoefficient * 
					
					// Summe über Folgeneuronen
					for (int succ = 0; succ < neuronsInNextLayer; succ++){
					
						// Summe über Ausgabeneuronen
						for (int out = 0; out < outputNeuron.length; out++){
							weightDelta += (
							// Erwartete Ausgabe_Ausgabeneuron - Ausgabe_Ausgabeneuron
							(wantedOut[out] - resultOut[out]) *
							//dAusgabe nach dNetzeingabe
							resultOut[out] * (1- resultOut[out]));
						}// for() Summe über Ausgabeneuronen
						
						// Bestimmen der Position im Netz um Gewicht_Neuron-FolgeNeuron zu laden
						// layer und neuron: Position von Neuron im Netz
						// : Anzahl der Folgeneuronen von Neuron
						// succ: x-te FolgeNeuron von Neuron; Offset zu Neuron
						
						// * Gewicht_Neuron-FolgeNeuron
						weightDelta *= connWeights[offset + succ];
					}// for() Summe über Folgeneuronen
					
					// * Ausgabe_u (1 - Ausgabe_u) * Eingabevektor_u
					//TODO: weightDelta *= 
					
					// deltaGewichtsvektor_u = Lernfaktor * -> hinter die Summen verschoben
					weightDelta *= trainingCoefficient;
					
					// Add calculated weight difference to connection weight
					connections[conn + offset].addWeightDelta(weightDelta);
				}// for() über alle Verbindungen
			}// for() über alle Neuronen
		}// for() über alle (Verbindungs-)Layer
	}// backpropagation()
}// class MultiLayerPerceptron

/************************************************************************************************
* TODO: class MultiLayerPerceptronInt:
* copy of class MultiLayerPerceptron but with NeuronInt
************************************************************************************************/