namespace java edu.uiuc.cs425

typedef i32 int

service DistributedGrep{

	oneway void startProcessing(1:string pattern),
	bool isAlive(),
	int getProgress(), //Not implemented - For future
	oneway void doneProcessing(1:int nodeIndex, 2:string data, 3:int count ),
}
