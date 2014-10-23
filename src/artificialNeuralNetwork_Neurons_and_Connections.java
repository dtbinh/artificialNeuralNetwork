/********************************** Artificial neural network ***********************************
* TODO:	Description, Header, Comments
************************************************************************************************/

/************************************************************************************************
* class Neuron:
* Uses logistic function for calculating output
************************************************************************************************/
class Neuron {
private
	int nrInputs;
	float inputs[];
	float netInput;
	float threshold;
	float output;

public
	// Constructor
	Neuron(int nrInputs){
		this.nrInputs = nrInputs;
		
		inputs = new float[nrInputs];
		
		// Initialize inputs and output to 0, threshold to max
		netInput = 0;
		threshold = 0x7fffffff;
		output = 0;
		
		for (int i = 0; i < nrInputs; i++)
			inputs[i] = 0;
	}
	
	Boolean setInput(int nrInput, float value){
		if(nrInput < nrInputs){
			inputs[nrInput] = value;
			
			return true;
		}
		
		return false;
	}
	
	Boolean unsetInput(int nrInput){
		if(nrInput < nrInputs){
			inputs[nrInput] = 0;
			
			return true;
		}
		
		return false;
	}
	
	float getThreshold(){
		return threshold;
	}
	
	void setThreshold(float threshold){
		this.threshold = threshold;
	}
	
	float getOutput(){
		// Reset net input
		netInput = 0;
		
		// Net input function f_net
		for (int i = 0; i < nrInputs; i++){
			netInput += inputs[i];
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
	float valueToSet;
	
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
		valueToSet = neuronFrom.getOutput();
		valueToSet *= connWeight;
		
		neuronTo.setInput(input, valueToSet);
	}// run()
	
	// Add in training calculated weight difference
	void addWeightDelta(float weightDelta){
		connWeight += weightDelta;
	}// addWeightDelta()
	
	Neuron getNeuronFrom(){
		return neuronFrom;
	}
	
	Neuron getNeuronTo(){
		return neuronTo;
	}
}// class Connection