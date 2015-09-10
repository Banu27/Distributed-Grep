namespace java edu.uiuc.cs425

typedef i32 int

service DistributedGrep{

	oneway void startProcessing(1:string pattern),
	bool isAlive(),
	int getProgress(),
	oneway void doneProcessing(1:int nodeIndex ),
	oneway void transferOutput(1: int nodeIndex, string data),
}
