namespace java edu.uiuc.cs425

typedef i32 int

service DistributedGrep{

	void startProcessing(1:string pattern),
	bool isAlive(),
	int getProgress(),
	bool doneProcessing(),
}