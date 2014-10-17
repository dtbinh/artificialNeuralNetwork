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
* class ThresholdItem:
* up to 32 inputs possible
* inputs are just 0 or 1
* output = input.1*weights[0] + ... + input.32*weights[31]
* So far, output = netInput
* activated just returns true if output >= threshold
* Note! So far, activated can just be called if getOutput was called before
************************************************************************************************/
class ThresholdItem
{
protected
	int inputs;
	int nrInputs;
	// So far also used as output
	float netInput;
	float weights[];
	float threshold;
	// Used as output
	Boolean activated;
	
public
	// Constructors
	ThresholdItem(){};

	ThresholdItem(int nrInputs){
		this.nrInputs = nrInputs;
		weights = new float[nrInputs];
		
		// Initialize inputs, output and weights to 0, threshold to max
		inputs = 0;
		netInput = 0;
		activated = false;
		threshold = 0x7fffffff;
		
		for (int i = 0; i < nrInputs; i++)
			weights[i] = 0;
	}
	
	Boolean setInput(int nrInput){
		if(nrInput < nrInputs){
			inputs |= 1 << nrInput;
			
			return true;
		}
		
		return false;
	}
	
	Boolean unsetInput(int nrInput){
		if(nrInput < nrInputs){
			inputs &= ~(1 << nrInput);
			
			return true;
		}
		
		return false;
	}
	
	Boolean setWeight(int nrInput, float weight){
		if(nrInput < weights.length){
			weights[nrInput] = weight;
			return true;
		}
		
		return false;
	}
	
	void setThreshold(float threshold){
		this.threshold = threshold;
	}
	
	float getOutput(){
		// Reset net input
		netInput = 0;
		
		// Net input function f_net
		for (int i = 0; i < nrInputs; i++){
			if((inputs & (1 << i)) == (1 << i))
				netInput += weights[i];
		}
		
		// Activation function f_act: step function
		if (netInput >= threshold){
			activated = true;
			
			// Output function f_out: Identity
			return netInput;
		}
		else {
			activated = false;
			return 0;
		}
	}
	
	Boolean getActivation(){
		return activated;
	}
}// class ThresholdItem

/************************************************************************************************
* class Neuron:
* Uses logistic function for calculating output
************************************************************************************************/
class Neuron extends ThresholdItem {
private
	int inputs;
	int nrInputs;
	float netInput;
	float weights[];
	float threshold;
	float output;

public
	// Constructor
	Neuron(int nrInputs){
		this.nrInputs = nrInputs;
		weights = new float[nrInputs];
		
		// Initialize inputs, output and weights to 0, threshold to max
		inputs = 0;
		netInput = 0;
		threshold = 0x7fffffff;
		output = 0;
		
		for (int i = 0; i < nrInputs; i++)
			weights[i] = 0;
	}
	
	Boolean setInput(int nrInput){
		if(nrInput < nrInputs){
			inputs |= 1 << nrInput;
			
			return true;
		}
		
		return false;
	}
	
	Boolean unsetInput(int nrInput){
		if(nrInput < nrInputs){
			inputs &= ~(1 << nrInput);
			
			return true;
		}
		
		return false;
	}
	
	Boolean setWeight(int nrInput, float weight){
		if(nrInput < weights.length){
			weights[nrInput] = weight;
			return true;
		}
		
		return false;
	}
	
	void setThreshold(float threshold){
		this.threshold = threshold;
	}
	
	float getOutput(){
		// Reset net input
		netInput = 0;
		
		// Net input function f_net
		for (int i = 0; i < nrInputs; i++){
			if((inputs & (1 << i)) == (1 << i))
				netInput += weights[i];
		}
		
		// Activation function f_act:
		// Logistic function: output = 1 / (1 + e^(-(netInput - threshold)))
		output = (float) (1 / (1 + Math.exp(-(netInput - threshold))));

		// Output function f_out: Identity
		return output;
	}

}// class Neuron

/************************************************************************************************
* class Connection:
* Connects the output of a neuron to an input of another neuron
* The weight of the following input is the output value times the weight of the connection
************************************************************************************************/
class Connection
{
private
	Neuron neuronFrom;
	Neuron neuronTo;
	int input;
	float connWeight;
	float weightToSet;
	
public
	// Constructor
	Connection(Neuron neuronFrom, Neuron neuronTo, int input, float connWeight){
		this.neuronFrom = neuronFrom;
		this.neuronTo = neuronTo;
		this.input = input;
		this.connWeight = connWeight;
	}// Connection()

	// Execute connection
	void run(){
		weightToSet = neuronFrom.getOutput();
		weightToSet *= connWeight;
		
		neuronTo.setWeight(input, weightToSet);
	
		if (neuronFrom.getOutput() != 0)
			neuronTo.setInput(input);
		else
			neuronTo.unsetInput(input);
	}// run()
	
	// Add in training calculated weight difference
	void addWeightDelta(float weightDelta){
		connWeight += weightDelta;
	}// addWeightDelta()
}// class Connection