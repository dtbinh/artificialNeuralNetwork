/********************************** Artificial neural network ***********************************
* Description:	This file contains the basic classes neuron and connection for the artificial
* 				neural network. The network itself is built with the class
*				MultiLayerPerceptron.
* 
* Author:		Giso Pillar
* 
* Date:			03.2015
************************************************************************************************/

/************************************************************************************************
* class Neuron:
* 	Basic element of the network. The inputs are summed up. By using a logistic function the
* 	output is calculated.
************************************************************************************************/
class Neuron
{
	
private
	float[] inputs;
	float netInput;
	float threshold;
	float output;

protected
	// Constructor
	Neuron(int nrInputs){
	
		inputs = new float[nrInputs];
		
		// Initialize inputs and output to 0, threshold to 8
		netInput = 0;
		threshold = 8;
		output = 0;
		
		for (int i = 0; i < inputs.length; i++)
			inputs[i] = 0;
	}
	
	Boolean setInput(int nrInput, float value){
		
		if(nrInput < inputs.length){
			inputs[nrInput] = value;
			
			return true;
		}
		
		return false;
	}
	
	int getNumberOfInputs(){
		
		return inputs.length;
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
		for (int i = 0; i < inputs.length; i++){
			
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
* 	Connects the output of a neuron to an input of another neuron.
* 	The weight of the following input is the output value times the weight of the connection.
************************************************************************************************/
class Connection
{
	
private
	Neuron neuronFrom;
	Neuron neuronTo;
	int input;
	float connectionWeight;
	float valueToSet;
	int positionNeuronTo;
	
protected
	// Constructor
	Connection(Neuron neuronFrom, Neuron neuronTo, int input, float connectionWeight, int positionNeuronTo){
	
		this.neuronFrom = neuronFrom;
		this.neuronTo = neuronTo;
		this.input = input;
		this.connectionWeight = connectionWeight;
		this.positionNeuronTo = positionNeuronTo;
	}

	// Execute connection
	void run(){
		
		valueToSet = neuronFrom.getOutput();
		valueToSet *= connectionWeight;
		
		neuronTo.setInput(input, valueToSet);
	}
	
	// Add in training calculated weight difference
	void addWeightDelta(float weightDelta){
		
		connectionWeight += weightDelta;
	}
	
	float getConnectionWeight(){
		
		return connectionWeight;
	}
	
	Neuron getNeuronFrom(){
		
		return neuronFrom;
	}
	
	Neuron getNeuronTo(){
		
		return neuronTo;
	}
	
	int getPositionNeuronTo(){
		
		return positionNeuronTo;
	}
}// class Connection